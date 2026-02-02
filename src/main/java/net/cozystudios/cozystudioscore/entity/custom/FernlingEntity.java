package net.cozystudios.cozystudioscore.entity.custom;

import net.cozystudios.cozystudioscore.config.FernlingConfig;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.entity.variant.FernlingVariant;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.cozystudios.cozystudioscore.world.gen.ModEntitySpawns; // <-- added
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FernlingEntity extends TameableEntity {

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(FernlingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState sitAnimationState = new AnimationState();

    private int bonemealCooldown = 0;

    public FernlingEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new SitGoal(this));

        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.1D, 10f, 3f, false));
        this.goalSelector.add(1, new FollowParentGoal(this, 1.1D));

        this.goalSelector.add(2, new AnimalMateGoal(this, 1.15D)); // breedable
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(ModItems.COZY_CRUMBS), false));

        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F) {
            @Override
            public boolean canStart() {
                return !FernlingEntity.this.isInSittingPose() && super.canStart();
            }
        });

        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (isInSittingPose()) {
            sitAnimationState.startIfNotRunning(this.age);
        } else {
            sitAnimationState.stop();
        }
    }

    protected void updateLimbs(float v) {
        float f;
        if (this.getPose() == EntityPose.STANDING) {
            f = Math.min(v * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }

        if (!this.getWorld().isClient()
                && this.isTamed()
                && !this.isSitting()
                && this.isAlive()) {

            if (bonemealCooldown > 0) {
                bonemealCooldown--;
            } else {
                FernlingConfig cfg = FernlingConfig.get();
                if (this.getRandom().nextFloat() < (float) cfg.fernlingBonemealChance) {
                    boolean didBonemeal = tryBonemealNearby(cfg.fernlingBonemealRadius);
                    bonemealCooldown = cfg.fernlingBonemealCooldownMin +
                            this.random.nextInt(cfg.fernlingBonemealCooldownMax - cfg.fernlingBonemealCooldownMin + 1);
                    if (didBonemeal) bonemealCooldown += 40;
                } else {
                    bonemealCooldown = 40;
                }
            }
        }
    }

    private boolean tryBonemealNearby(int radius) {
        if (!(this.getWorld() instanceof ServerWorld world)) return false;

        BlockPos base = this.getBlockPos();
        for (int tries = 0; tries < 10; tries++) {
            BlockPos pos = base.add(
                    this.random.nextBetween(-radius, radius),
                    this.random.nextBetween(-1, 1),
                    this.random.nextBetween(-radius, radius)
            );

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof Fertilizable fertilizable) {
                if (fertilizable.isFertilizable(world, pos, state, false) &&
                        fertilizable.canGrow(world, world.random, pos, state)) {

                    fertilizable.grow(world, world.random, pos, state);
                    world.syncWorldEvent(net.minecraft.world.WorldEvents.BONE_MEAL_USED, pos, 0);
                    world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE,
                            this.getSoundCategory(), 0.7f, 1.0f);
                    return true;
                }
            }
        }
        return false;
    }

    public static DefaultAttributeContainer.Builder createFernlingAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        FernlingEntity baby = ModEntities.FERNLING.create(world);
        if (baby != null) {
            if (entity instanceof FernlingEntity other) {
                if (this.getVariant() == other.getVariant()) {
                    baby.setVariant(this.getVariant());
                } else {
                    baby.setVariant(Util.getRandom(FernlingVariant.values(), this.random));
                }
            } else {
                baby.setVariant(this.getVariant());
            }
        }
        return baby;
    }

    /* SOUNDS */
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.FERNLING_IDLE;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }

    /* TAME / HEAL / SIT / BREED */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = ModItems.COZY_CRUMBS;

        // Tame
        if (item == itemForTaming && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }
                if (!this.getWorld().isClient()) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7); // hearts
                    setSitting(true);
                    setInSittingPose(true);
                }
                return ActionResult.SUCCESS;
            }
        }

        // Heal if tamed
        if (isTamed() && item == itemForTaming && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().creativeMode) {
                itemstack.decrement(1);
            }
            this.heal(2.0F);
            return ActionResult.SUCCESS;
        }

        // Sit toggle (ignore when holding breeding/taming item)
        if (isTamed() && this.isOwner(player) && hand == Hand.MAIN_HAND &&
                item != itemForTaming && !isBreedingItem(itemstack)) {
            boolean sitting = !isSitting();
            setSitting(sitting);
            setInSittingPose(sitting);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.COZY_CRUMBS);
    }

    public FernlingVariant getVariant() {
        return FernlingVariant.byId(this.dataTracker.get(DATA_ID_TYPE_VARIANT) & 255);
    }

    public void setVariant(FernlingVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.dataTracker.get(DATA_ID_TYPE_VARIANT));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(FernlingVariant.byId(nbt.getInt("Variant")));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setVariant(
                ModEntitySpawns.pickFernlingVariantForBiome(world, this.getBlockPos())
                        .orElse(Util.getRandom(FernlingVariant.values(), this.random))
        );
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}
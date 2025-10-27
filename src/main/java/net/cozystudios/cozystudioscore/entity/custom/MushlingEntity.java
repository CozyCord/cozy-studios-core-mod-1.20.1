package net.cozystudios.cozystudioscore.entity.custom;

import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.entity.variant.MushlingVariant;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.cozystudios.cozystudioscore.world.gen.ModEntitySpawns;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
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

public class MushlingEntity extends TameableEntity {

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(MushlingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public final AnimationState sitAnimationState = new AnimationState();

    @Nullable
    private BlockPos lastLightPos = null;
    private static final int NIGHT_GLOW_LEVEL = 12;

    public MushlingEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.10, 10F, 3f, false));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.15D));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(ModItems.COZY_CRUMBS), false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F) {
            @Override
            public boolean canStart() {
                return !MushlingEntity.this.isInSittingPose() && super.canStart();
            }
        });
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    private void setupAnimationStates() {
        if (this.isInSittingPose()) {
            idleAnimationState.stop();
            sitAnimationState.startIfNotRunning(this.age);
        } else {
            sitAnimationState.stop();
            if (this.idleAnimationTimeout-- <= 0) {
                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.start(this.age);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
            return;
        }

        if (this.isInSittingPose() && this.age % 120 == 0) {
            this.getWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ENTITY_FOX_SLEEP, this.getSoundCategory(), 0.5f, 1.0f);
        }

        if (this.isTamed() && this.age % 5 == 0) {
            updateTamedNightLight();
        }

        if (this.getWorld().getDimension().ultrawarm() && this.getY() > 200) {
            this.discard();
        }
    }

    public static DefaultAttributeContainer.Builder createMushlingAttributes() {
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
        MushlingEntity baby = ModEntities.MUSHLING.create(world);
        if (baby != null) {
            if (entity instanceof MushlingEntity other) {
                baby.setVariant(this.getVariant() == other.getVariant()
                        ? this.getVariant()
                        : Util.getRandom(MushlingVariant.values(), this.random));
            } else baby.setVariant(this.getVariant());
        }
        return baby;
    }

    // --- Sounds ---
    @Override protected @Nullable SoundEvent getAmbientSound() {
        return switch (this.random.nextInt(4)) {
            case 0 -> ModSounds.MUSHLING_IDLE_1;
            case 1 -> ModSounds.MUSHLING_IDLE_2;
            case 2 -> ModSounds.MUSHLING_IDLE_3;
            default -> ModSounds.MUSHLING_IDLE_4;
        };
    }

    @Override public int getMinAmbientSoundDelay() { return 200; }
    @Override protected @Nullable SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENTITY_PANDA_HURT; }
    @Override protected @Nullable SoundEvent getDeathSound() { return SoundEvents.ENTITY_PANDA_DEATH; }

    // --- Interaction ---
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        if (item == ModItems.COZY_CRUMBS) {
            if (!player.getAbilities().creativeMode) stack.decrement(1);

            if (!this.isTamed()) {
                if (!this.getWorld().isClient()) {
                    if (this.random.nextInt(3) == 0) {
                        this.setOwner(player);
                        this.navigation.recalculatePath();
                        this.setTarget(null);
                        this.getWorld().sendEntityStatus(this, (byte) 7);
                    } else this.getWorld().sendEntityStatus(this, (byte) 6);
                }
                return ActionResult.SUCCESS;
            }

            if (this.isTamed() && this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
                return ActionResult.SUCCESS;
            }
        }

        if (this.isTamed() && this.isOwner(player) && hand == Hand.MAIN_HAND && item != ModItems.COZY_CRUMBS) {
            boolean sitting = !this.isSitting();
            this.setSitting(sitting);
            this.setInSittingPose(sitting);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override public EntityView method_48926() { return this.getWorld(); }
    @Override public boolean isBreedingItem(ItemStack stack) { return stack.isOf(ModItems.COZY_CRUMBS); }

    // --- Night Light System ---
    private void updateTamedNightLight() {
        if (!this.isAlive() || !this.isTamed() || this.getWorld().isDay()) {
            removeLastLightIfPresent();
            return;
        }

        BlockPos target = this.getBlockPos();
        if (!isAirOrLight(target)) target = target.up();

        if (this.lastLightPos != null && !this.lastLightPos.equals(target)) {
            removeLastLightIfPresent();
        }

        if (isAirOrLight(target)) {
            var desired = Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, NIGHT_GLOW_LEVEL);
            var current = this.getWorld().getBlockState(target);
            if (!current.isOf(Blocks.LIGHT) || current.get(LightBlock.LEVEL_15) != NIGHT_GLOW_LEVEL) {
                this.getWorld().setBlockState(target, desired, 2);
            }
            this.lastLightPos = target;
        } else {
            this.lastLightPos = null;
        }
    }

    private void removeLastLightIfPresent() {
        if (this.lastLightPos == null) return;
        var state = this.getWorld().getBlockState(this.lastLightPos);
        if (state.isOf(Blocks.LIGHT)) {
            this.getWorld().setBlockState(this.lastLightPos, Blocks.AIR.getDefaultState(), 2);
        }
        this.lastLightPos = null;
    }

    private boolean isAirOrLight(BlockPos pos) {
        var state = this.getWorld().getBlockState(pos);
        return state.isAir() || state.isOf(Blocks.LIGHT);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient()) removeLastLightIfPresent();
        super.remove(reason);
    }

    // --- Variants ---
    public MushlingVariant getVariant() { return MushlingVariant.byId(this.dataTracker.get(DATA_ID_TYPE_VARIANT) & 255); }
    public void setVariant(MushlingVariant variant) { this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255); }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.dataTracker.get(DATA_ID_TYPE_VARIANT));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(MushlingVariant.byId(nbt.getInt("Variant")));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason reason,
                                 @Nullable EntityData data, @Nullable NbtCompound nbt) {
        MushlingVariant chosen = ModEntitySpawns.pickVariantForBiome(world, this.getBlockPos())
                .orElse(Util.getRandom(MushlingVariant.values(), this.random));
        setVariant(chosen);
        return super.initialize(world, difficulty, reason, data, nbt);
    }
}
package net.cozystudios.cozystudioscore.entity.custom;

import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import net.minecraft.block.BlockState;

public class MysticalElkEntity extends TameableEntity implements Mount, JumpingMount {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState runningAnimationState = new AnimationState();

    // Saddled flag
    private static final TrackedData<Boolean> SADDLED =
            DataTracker.registerData(MysticalElkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    // Track jump charge
    private float queuedJumpStrength;

    public MysticalElkEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Saddled", this.isSaddled());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSaddled(nbt.getBoolean("Saddled"));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            if (this.limbAnimator.getSpeed() < 0.15F && !this.idleAnimationState.isRunning()) {
                this.idleAnimationState.start(this.age);
            } else if (this.limbAnimator.getSpeed() >= 0.15F) {
                this.idleAnimationState.stop();
            }

            if (this.isSprinting()) {
                this.runningAnimationState.startIfNotRunning(this.age);
            } else {
                this.runningAnimationState.stop();
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        // REMOVED FollowOwnerGoal so they don’t teleport to owner
        this.goalSelector.add(1, new FollowParentGoal(this, 1.1D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.15D));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(ModItems.MYSTICAL_BERRIES), false));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    protected void updateLimbs(float v) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(v * 6.0F, 1.0F) : 0.0F;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    public static DefaultAttributeContainer.Builder createMysticalElkAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.5);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.MYSTICAL_ELK.create(world);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();
        Item itemForTaming = ModItems.MYSTICAL_BERRIES;

        // Tame
        if (item == itemForTaming && !isTamed()) {
            if (!player.getAbilities().creativeMode) {
                itemstack.decrement(1);
            }
            if (!this.getWorld().isClient()) {
                if (this.random.nextInt(3) == 0) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                } else {
                    this.getWorld().sendEntityStatus(this, (byte) 6);
                }
            }
            return ActionResult.SUCCESS;
        }

        // Heal
        if (isTamed() && item == itemForTaming && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().creativeMode) {
                itemstack.decrement(1);
            }
            if (!player.isSneaking() && this.isSaddled() && !this.hasPassengers()) {
                player.startRiding(this);
            }
            this.heal(4.0F);
            return ActionResult.SUCCESS;
        }

        // Saddle
        if (isTamed() && item == Items.SADDLE && !this.isSaddled()) {
            if (!player.getAbilities().creativeMode) {
                itemstack.decrement(1);
            }
            this.setSaddled(true);
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }

        // Mount (disallow babies)
        if (isTamed() && this.isSaddled() && !this.hasPassengers() && !this.isBaby()) {
            if (!this.getWorld().isClient()) {
                player.startRiding(this);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public boolean isSaddled() {
        return this.dataTracker.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.dataTracker.set(SADDLED, saddled);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.MYSTICAL_BERRIES);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return (LivingEntity) this.getFirstPassenger();
    }

    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.35D;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.hasPassengers() && getControllingPassenger() instanceof PlayerEntity player) {
            if (!isTamed() || !this.isSaddled() || this.isBaby()) {
                super.travel(Vec3d.ZERO);
                return;
            }

            this.setYaw(player.getYaw());
            this.prevYaw = this.getYaw();
            this.setPitch(player.getPitch() * 0.5F);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float strafe = player.sidewaysSpeed;
            float forward = player.forwardSpeed;

            if (this.isLogicalSideForUpdatingMovement()) {
                float speed = (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

                if (MinecraftClient.getInstance().options.sprintKey.isPressed()) {
                    speed *= 1.15F;
                    this.setSprinting(true);
                } else {
                    this.setSprinting(false);
                }

                if (this.queuedJumpStrength > 0.0F && this.isOnGround()) {
                    double jumpPower = this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * this.queuedJumpStrength;
                    Vec3d vel = this.getVelocity();
                    this.setVelocity(vel.x, jumpPower, vel.z);
                    this.velocityDirty = true;
                    this.queuedJumpStrength = 0.0F;
                }

                this.setMovementSpeed(speed);
                super.travel(new Vec3d(strafe, movementInput.y, forward));
            }
        } else {
            this.setSprinting(false);
            super.travel(movementInput);
        }
    }

    // --- JumpingMount implementation ---
    @Override
    public void startJumping(int charge) {
        this.queuedJumpStrength = (float)charge / 100.0F;
    }

    @Override
    public void stopJumping() {
        // reset handled in travel
    }

    @Override
    public void setJumpStrength(int strength) {
        this.queuedJumpStrength = (float)(this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH) * (strength / 100.0F));
    }

    @Override
    public boolean canJump() {
        return this.isSaddled() && this.isTamed() && this.hasPassengers() && !this.isBaby();
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Direction direction = this.getMovementDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.updatePassengerForDismount(passenger);
        }
        int[][] is = Dismounting.getDismountOffsets(direction);
        BlockPos blockPos = this.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (EntityPose pose : passenger.getPoses()) {
            Box box = passenger.getBoundingBox(pose);
            for (int[] js : is) {
                mutable.set(blockPos.getX() + js[0], blockPos.getY(), blockPos.getZ() + js[1]);
                double d = this.getWorld().getDismountHeight(mutable);
                if (!Dismounting.canDismountInBlock(d)) continue;
                Vec3d vec3d = Vec3d.ofCenter(mutable, d);
                if (!Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) continue;
                passenger.setPose(pose);
                return vec3d;
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    // --- Randomize stats on spawn ---
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        // Randomize like horses
        double health = 15.0D + (this.random.nextDouble() * 15.0D);   // 15–30 HP
        double speed = 0.18D + (this.random.nextDouble() * 0.12D);   // ~0.18–0.3
        double jump = 0.4D + (this.random.nextDouble() * 1.2D);      // 0.4-1.2

        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(health);
        this.setHealth((float)health);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH)).setBaseValue(jump);

        return data;
    }

    // --- Lead support like horses ---
    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    @Override
    public void detachLeash(boolean sendPacket, boolean dropItem) {
        super.detachLeash(sendPacket, dropItem);
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0D, this.getStandingEyeHeight() * 0.6D, this.getWidth() * 0.2D);
    }

    // --- Fix nameplate height ---
    @Override
    public float getNameLabelHeight() {
        // Lowered so it’s closer to the head, not way above
        return (float) (this.getHeight() - 0.5D);
    }

    // --- No fall damage ---
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false; // completely ignore fall damage
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }
}
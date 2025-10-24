package net.cozystudios.cozystudioscore.block.entity;

import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class TranquilLanternBlockEntity extends BlockEntity {

    public TranquilLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANQUIL_LANTERN, pos, state);
    }
    @SuppressWarnings("unused")
    public static void tick(World world, BlockPos pos, BlockState state, TranquilLanternBlockEntity _be) {
        if (world.isClient) return;

        int RADIUS = ModConfig.get().tranquilLanternRadius;
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        double r2 = RADIUS * RADIUS;

        for (PlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(cx, cy, cz) <= r2) {
                if (!player.hasStatusEffect(StatusEffects.REGENERATION) ||
                        player.getStatusEffect(StatusEffects.REGENERATION).getDuration() < 40) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION,
                            100,
                            0,
                            true,
                            false
                    ));
                }
            }
        }

        Box box = new Box(cx - RADIUS, cy - RADIUS, cz - RADIUS,
                cx + RADIUS, cy + RADIUS, cz + RADIUS);

        for (HostileEntity mob : world.getEntitiesByClass(HostileEntity.class, box, Entity::isAlive)) {
            double dx = mob.getX() - cx;
            double dy = mob.getY() - cy;
            double dz = mob.getZ() - cz;
            double d2 = dx * dx + dy * dy + dz * dz;

            if (d2 <= r2) {
                double d = Math.max(0.001, Math.sqrt(d2));
                double strength = 0.15 * (1.0 - (d / RADIUS));

                mob.addVelocity((dx / d) * strength, 0.05, (dz / d) * strength);
                mob.velocityModified = true;
            }
        }
    }
}
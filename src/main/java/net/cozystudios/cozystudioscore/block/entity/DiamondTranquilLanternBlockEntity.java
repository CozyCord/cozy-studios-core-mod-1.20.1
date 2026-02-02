package net.cozystudios.cozystudioscore.block.entity;

import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.config.TranquilLanternsConfig;
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

public class DiamondTranquilLanternBlockEntity extends BlockEntity {

    public DiamondTranquilLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIAMOND_TRANQUIL_LANTERN, pos, state);
    }

    @SuppressWarnings("unused")
    public static void tick(World world, BlockPos pos, BlockState state, DiamondTranquilLanternBlockEntity _be) {
        if (world.isClient) return;

        int radius = TranquilLanternsConfig.get().getDiamondTranquilLanternRadius();
        boolean bump = TranquilLanternsConfig.get().diamondTranquilLanternBump;
        boolean burn = TranquilLanternsConfig.get().diamondTranquilLanternBurn;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        double r2 = radius * radius;

        // Player regen effect
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

        // Mob suppression logic
        if (bump || burn) {
            Box box = new Box(cx - radius, cy - radius, cz - radius,
                    cx + radius, cy + radius, cz + radius);

            for (HostileEntity mob : world.getEntitiesByClass(HostileEntity.class, box, Entity::isAlive)) {
                double dx = mob.getX() - cx;
                double dy = mob.getY() - cy;
                double dz = mob.getZ() - cz;
                double d2 = dx * dx + dy * dy + dz * dz;

                if (bump && d2 <= r2 && d2 > 0.25) {
                    double d = Math.sqrt(d2);
                    double normalized = Math.max(0.0, 1.0 - (d / radius));
                    double strength = 0.25 * Math.pow(normalized, 2.0);

                    mob.addVelocity((dx / d) * strength, 0, (dz / d) * strength);
                    mob.velocityModified = true;
                }

                if (burn && d2 <= r2) {
                    mob.setOnFireFor(5);
                }
            }
        }
    }
}

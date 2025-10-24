package net.cozystudios.cozystudioscore.entity.ai;

import net.cozystudios.cozystudioscore.block.entity.TranquilLanternBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class LanternAvoidGoal extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final int radius;

    public LanternAvoidGoal(PathAwareEntity mob, double speed, int radius) {
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return isNearLantern();
    }

    @Override
    public boolean shouldContinue() {
        return isNearLantern();
    }

    @Override
    public void start() {
        BlockPos lanternPos = findNearbyLantern();
        if (lanternPos != null) {
            double dx = mob.getX() - lanternPos.getX();
            double dz = mob.getZ() - lanternPos.getZ();
            double dist = Math.max(0.1, Math.sqrt(dx*dx + dz*dz));

            double targetX = mob.getX() + (dx / dist) * radius;
            double targetZ = mob.getZ() + (dz / dist) * radius;

            mob.getNavigation().startMovingTo(targetX, mob.getY(), targetZ, speed);
        }
    }

    private boolean isNearLantern() {
        return findNearbyLantern() != null;
    }

    private BlockPos findNearbyLantern() {
        World world = mob.getWorld();
        BlockPos mobPos = mob.getBlockPos();

        int r = radius;
        for (BlockPos pos : BlockPos.iterateOutwards(mobPos, r, r, r)) {
            if (world.getBlockEntity(pos) instanceof TranquilLanternBlockEntity) {
                if (mobPos.getSquaredDistance(pos) <= (radius * radius)) {
                    return pos.toImmutable();
                }
            }
        }
        return null;
    }
}
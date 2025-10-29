package net.cozystudios.cozystudioscore.mixin;

import net.cozystudios.cozystudioscore.block.entity.TranquilLanternBlockEntity;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SpawnRestriction.class)
public class MobEntityTranquilSpawnMixin {

    @Unique
    private static Integer RADIUS = null;
    @Unique
    private static int getRadius() {
        if (RADIUS == null) {
            RADIUS = ModConfig.get().tranquilLanternRadius;
        }
        return RADIUS;
    }

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void onCanSpawn(EntityType<?> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (type.getSpawnGroup().isPeaceful() || spawnReason != SpawnReason.NATURAL) {
            cir.setReturnValue(true);
            return;
        }

        int radius = getRadius();

        for (BlockEntity be : getLanternsNearby((ServerWorld) world, pos, radius)) {
            double distSq = be.getPos().getSquaredDistance(pos);
            if (distSq <= radius * radius) {
                // Lantern in radius
                // System.out.println("Lantern found in range");
                // set spawn to false to prevent entity to spawn
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Unique
    private static List<TranquilLanternBlockEntity> getLanternsNearby(ServerWorld world, BlockPos center, int range) {
        List<TranquilLanternBlockEntity> lanterns = new ArrayList<>();
        int chunkRadius = (range >> 4) + 1;

        int chunkX = center.getX() >> 4;
        int chunkZ = center.getZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                WorldChunk chunk = world.getChunk(chunkX + dx, chunkZ + dz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof TranquilLanternBlockEntity) {
                        lanterns.add((TranquilLanternBlockEntity) be);
                    }
                }
            }
        }

        return lanterns;
    }


}
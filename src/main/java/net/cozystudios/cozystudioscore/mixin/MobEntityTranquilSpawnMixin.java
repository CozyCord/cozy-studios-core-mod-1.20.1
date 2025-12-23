package net.cozystudios.cozystudioscore.mixin;

import net.cozystudios.cozystudioscore.world.TranquilLanternSpawnBlocker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnRestriction.class)
public class MobEntityTranquilSpawnMixin {

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void cozystudios$tranquilLanternBlockSpawns(EntityType<?> type,
                                                               ServerWorldAccess world,
                                                               SpawnReason spawnReason,
                                                               BlockPos pos,
                                                               Random random,
                                                               CallbackInfoReturnable<Boolean> cir) {

        if (spawnReason != SpawnReason.NATURAL) return;
        if (type.getSpawnGroup() != SpawnGroup.MONSTER) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        if (!TranquilLanternSpawnBlocker.hasAnyLanterns(serverWorld)) return;

        if (TranquilLanternSpawnBlocker.isSpawnBlocked(serverWorld, pos)) {
            cir.setReturnValue(false);
        }
    }
}
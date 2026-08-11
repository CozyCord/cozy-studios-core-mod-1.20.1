package net.cozystudios.cozystudioscore.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.cozystudios.cozystudioscore.integration.farmandcharm.FarmAndCharmCompat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.potionstudios.biomeswevegone.world.level.block.custom.BWGFarmLandBlock")
public class BwgFarmLandBlockSprinklerMixin {

    @ModifyReturnValue(
            method = "isNearWater(Lnet/minecraft/class_4538;Lnet/minecraft/class_2338;)Z",
            at = @At("RETURN"),
            remap = false
    )
    private static boolean cozystudioscore$treatSprinklerAsWater(boolean original, WorldView world, BlockPos pos) {
        if (original) return true;
        return FarmAndCharmCompat.hasSprinklerNearby(world, pos);
    }
}

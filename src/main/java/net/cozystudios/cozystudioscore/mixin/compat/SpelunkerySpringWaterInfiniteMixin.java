package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.mehvahdjukaar.moonlight.api.fluids.ModFlowingFluid", remap = false)
public abstract class SpelunkerySpringWaterInfiniteMixin {

    @Inject(
            method = "method_15737(Lnet/minecraft/class_1937;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void cozystudioscore$springWaterInfinite(World world, CallbackInfoReturnable<Boolean> cir) {
        if (this.getClass().getName().startsWith("com.ordana.spelunkery.fluids.SpringWater")) {
            cir.setReturnValue(true);
        }
    }
}

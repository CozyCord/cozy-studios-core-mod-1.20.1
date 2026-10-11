package net.cozystudios.cozystudioscore.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.satisfy.vinery.core.registry.ArmorMaterialRegistry$1")
public class VineryWinemakerDurabilityMixin {

    @ModifyReturnValue(
            method = "method_48402(Lnet/minecraft/class_1738$class_8051;)I",
            at = @At("RETURN"),
            remap = false
    )
    private int cozystudioscore$bumpWinemakerDurability(int original) {
        return original * 5;
    }
}

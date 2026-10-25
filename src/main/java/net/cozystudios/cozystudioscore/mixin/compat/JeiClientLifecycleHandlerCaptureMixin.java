package net.cozystudios.cozystudioscore.mixin.compat;

import net.cozystudios.cozystudioscore.client.FairyLightsJeiReloadFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "mezz.jei.fabric.startup.ClientLifecycleHandler", remap = false)
public abstract class JeiClientLifecycleHandlerCaptureMixin {

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void cozystudioscore$captureHandler(CallbackInfo ci) {
        FairyLightsJeiReloadFix.setLifecycleHandler(this);
    }
}

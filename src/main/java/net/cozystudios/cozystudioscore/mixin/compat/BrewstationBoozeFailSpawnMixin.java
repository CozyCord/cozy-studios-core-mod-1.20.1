package net.cozystudios.cozystudioscore.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.satisfy.brewery.core.block.entity.BrewstationBlockEntity", remap = false)
public abstract class BrewstationBoozeFailSpawnMixin {

    @Shadow private int solved;
    @Shadow private int totalEvents;

    @Unique
    private int cozystudioscore$resolvedEventsThisBrew;

    @Unique
    private int cozystudioscore$solvedSnapshot;

    @Inject(method = "brew", at = @At("HEAD"), remap = false)
    private void cozystudioscore$captureResolvedEvents(CallbackInfo ci) {
        this.cozystudioscore$resolvedEventsThisBrew = this.solved;
    }

    @Inject(method = "spawnElementals", at = @At("HEAD"), remap = false)
    private void cozystudioscore$forceBoozeOnMiss(CallbackInfo ci) {
        this.cozystudioscore$solvedSnapshot = this.solved;
        if (this.cozystudioscore$resolvedEventsThisBrew < this.totalEvents) {
            this.solved = 0;
        }
    }

    @Inject(method = "spawnElementals", at = @At("RETURN"), remap = false)
    private void cozystudioscore$restoreSolved(CallbackInfo ci) {
        this.solved = this.cozystudioscore$solvedSnapshot;
    }
}

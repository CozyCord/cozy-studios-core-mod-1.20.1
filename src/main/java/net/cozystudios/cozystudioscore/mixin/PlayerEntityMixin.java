package net.cozystudios.cozystudioscore.mixin;

import net.cozystudios.cozystudioscore.config.GeneralConfig;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {


    @Redirect(
            method = "tickMovement()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;"
            )
    )
    private Difficulty redirectStarvation(World world) {
        if (!GeneralConfig.get().peacefulHunger) {
            return world.getDifficulty();
        }

        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            return Difficulty.NORMAL;
        }
        return world.getDifficulty();
    }
}
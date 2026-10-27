package net.cozystudios.cozystudioscore.mixin.compat;

import net.cozystudios.cozystudioscore.compat.spelunkery.ChiselVeinMining;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.ordana.spelunkery.items.HammerAndChiselItem", remap = false)
public abstract class SpelunkeryChiselVeinMiningMixin {

    @Inject(
            method = "method_7852(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;I)V",
            at = @At("HEAD"),
            remap = false
    )
    private void cozystudioscore$captureTarget(World world, LivingEntity user, ItemStack stack, int remainingTicks, CallbackInfo ci) {
        ChiselVeinMining.captureTarget(world, user, stack, remainingTicks);
    }

    @Inject(
            method = "method_7852(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;I)V",
            at = @At("RETURN"),
            remap = false
    )
    private void cozystudioscore$runVeinMining(World world, LivingEntity user, ItemStack stack, int remainingTicks, CallbackInfo ci) {
        ChiselVeinMining.runVeinMining(world, user, stack);
    }
}

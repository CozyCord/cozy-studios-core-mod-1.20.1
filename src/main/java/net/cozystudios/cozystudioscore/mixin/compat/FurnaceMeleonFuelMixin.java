package net.cozystudios.cozystudioscore.mixin.compat;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.util.MeleonEasterEgg;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceMeleonFuelMixin {

    @Inject(
            method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;decrement(I)V",
                    ordinal = 0
            )
    )
    private static void cozystudioscore$meleonFuelBurn(
            World world,
            BlockPos pos,
            BlockState state,
            AbstractFurnaceBlockEntity blockEntity,
            CallbackInfo ci
    ) {
        if (world.isClient()) return;
        if (!MeleonEasterEgg.matches(blockEntity.getStack(1))) return;
        CozyStudiosCore.LOGGER.info("[MeleonEasterEgg] furnace consuming meleon plushie at {}", pos);
        MeleonEasterEgg.trigger(world);
    }
}

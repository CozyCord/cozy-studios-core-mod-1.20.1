package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CropBlock.class)
public abstract class CropBlockFertilizedMoistureMixin {

    @Redirect(
        method = "getAvailableMoisture",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    private static boolean cozystudioscore$acceptModdedFarmland(BlockState state, Block target) {
        if (state.isOf(target)) return true;
        return target == Blocks.FARMLAND && state.getBlock() instanceof FarmlandBlock;
    }

    @Redirect(
        method = "getAvailableMoisture",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;")
    )
    private static Comparable<?> cozystudioscore$fertilizedAlwaysHydrated(BlockState state, Property<?> property) {
        if (property == FarmlandBlock.MOISTURE
                && !state.isOf(Blocks.FARMLAND)
                && state.getBlock() instanceof FarmlandBlock) {
            return 7;
        }
        return state.get(property);
    }
}

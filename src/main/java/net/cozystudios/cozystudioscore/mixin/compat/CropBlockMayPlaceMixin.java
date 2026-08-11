package net.cozystudios.cozystudioscore.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.cozystudios.cozystudioscore.tag.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CropBlock.class)
public class CropBlockMayPlaceMixin {

    @ModifyReturnValue(method = "canPlantOnTop", at = @At("RETURN"))
    private boolean cozystudioscore$acceptModdedFarmland(boolean original, BlockState floor, BlockView world, BlockPos pos) {
        if (original) return true;
        if (floor.getBlock() instanceof FarmlandBlock) return true;
        if (floor.isIn(ModBlockTags.CROP_FARMLAND)) return true;
        return false;
    }
}

package net.cozystudios.cozystudioscore.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.cozystudios.cozystudioscore.tag.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "vectorwing.farmersdelight.common.block.BuddingTomatoBlock")
public class BuddingTomatoBlockMayPlaceMixin {

    @ModifyReturnValue(
            method = "method_9695(Lnet/minecraft/class_2680;Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z",
            at = @At("RETURN")
    )
    private boolean cozystudioscore$acceptModdedFarmland(boolean original, BlockState floor, BlockView world, BlockPos pos) {
        if (original) return true;
        if (floor.getBlock() instanceof FarmlandBlock) return true;
        return floor.isIn(ModBlockTags.CROP_FARMLAND);
    }
}

package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "earth.terrarium.chipped.common.blocks.WorkbenchBlock")
public class ChippedWorkbenchDoubleDropMixin {

    @Inject(
            method = "method_9576(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1657;)V",
            at = @At("HEAD"),
            remap = false
    )
    private void cozystudioscore$dropOtherHalf(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (world.isClient) return;
        if (player.isCreative()) return;

        Property<?> modelProp = state.getBlock().getStateManager().getProperty("model");
        if (modelProp == null) return;
        String modelValue = state.get(modelProp).toString();

        Direction facing = state.get(HorizontalFacingBlock.FACING);
        BlockPos otherPos;
        boolean brokeSide;
        if ("main".equals(modelValue)) {
            otherPos = pos.offset(facing.rotateYClockwise());
            brokeSide = false;
        } else if ("side".equals(modelValue)) {
            otherPos = pos.offset(facing.rotateYCounterclockwise());
            brokeSide = true;
        } else {
            return;
        }

        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.getBlock() != state.getBlock()) return;

        Block.dropStacks(otherState, world, otherPos, null, player, player.getMainHandStack());

        if (brokeSide) {
            world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), 3);
        }
    }
}

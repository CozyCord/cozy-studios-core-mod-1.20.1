package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock", remap = false)
public abstract class SophisticatedChestDoubleOnPlacedMixin {

    @Inject(
            method = "method_9605(Lnet/minecraft/class_1750;)Lnet/minecraft/class_2680;",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void cozystudioscore$forceRightTypeForDoubleTapedItem(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null) return;
        ItemStack stack = ctx.getStack();
        if (!stack.hasNbt() || stack.getNbt() == null || !stack.getNbt().getBoolean("doubleChest")) return;

        EnumProperty<ChestType> typeProp = Properties.CHEST_TYPE;
        if (!state.contains(typeProp)) return;
        if (state.get(typeProp) != ChestType.SINGLE) return;

        cir.setReturnValue(state.with(typeProp, ChestType.RIGHT));
    }

    @Inject(
            method = "method_9567(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;)V",
            at = @At("HEAD"),
            remap = false
    )
    private void cozystudioscore$createPartnerBeforeSuper(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (world.isClient()) return;
        if (!stack.hasNbt() || stack.getNbt() == null || !stack.getNbt().getBoolean("doubleChest")) return;
        if (!state.contains(HorizontalFacingBlock.FACING)) return;

        EnumProperty<ChestType> typeProp = Properties.CHEST_TYPE;
        if (!state.contains(typeProp)) return;
        if (state.get(typeProp) == ChestType.SINGLE) return;

        Direction facing = state.get(HorizontalFacingBlock.FACING);
        Direction partnerDir = (state.get(typeProp) == ChestType.LEFT)
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise();
        BlockPos partnerPos = pos.offset(partnerDir);
        BlockState existing = world.getBlockState(partnerPos);
        if (!existing.isAir()) return;

        ChestType partnerType = (state.get(typeProp) == ChestType.LEFT) ? ChestType.RIGHT : ChestType.LEFT;
        BlockState partnerState = state.with(typeProp, partnerType);

        world.setBlockState(partnerPos, partnerState,
                Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
    }

    @Redirect(
            method = "method_9567(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/class_1937;method_8652(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)Z"),
            remap = false,
            require = 0
    )
    private boolean cozystudioscore$safePartnerSetBlockState(World world, BlockPos partnerPos, BlockState newState, int flags) {
        return world.setBlockState(partnerPos, newState, flags | Block.SKIP_DROPS);
    }
}

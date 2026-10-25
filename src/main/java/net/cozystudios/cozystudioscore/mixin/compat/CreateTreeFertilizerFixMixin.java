package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.equipment.TreeFertilizerItem", remap = false)
public abstract class CreateTreeFertilizerFixMixin {

    @Inject(
            method = "method_7884(Lnet/minecraft/class_1838;)Lnet/minecraft/class_1269;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void cozystudioscore$avoidServerDeadlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        if (world.isClient) return;

        BlockPos pos = context.getBlockPos();
        BlockState initial = world.getBlockState(pos);
        if (!initial.isIn(BlockTags.SAPLINGS)) return;

        ServerWorld sw = (ServerWorld) world;

        boolean grew = false;
        for (int i = 0; i < 24; i++) {
            BlockState current = world.getBlockState(pos);
            if (!current.isIn(BlockTags.SAPLINGS)) {
                grew = true;
                break;
            }
            Block block = current.getBlock();
            boolean advanced = false;

            if (block instanceof Fertilizable fertilizable
                    && fertilizable.isFertilizable(world, pos, current, false)) {
                fertilizable.grow(sw, world.random, pos, current);
                advanced = true;
            } else {
                try {
                    current.randomTick(sw, pos, world.random);
                    advanced = true;
                } catch (Throwable ignored) {}
            }

            if (!advanced) break;
            BlockState after = world.getBlockState(pos);
            if (!after.isIn(BlockTags.SAPLINGS) || !after.equals(current)) {
                grew = true;
                if (!after.isIn(BlockTags.SAPLINGS)) break;
            }
        }

        if (!grew) {
            cir.setReturnValue(ActionResult.PASS);
            return;
        }

        if (context.getPlayer() != null && !context.getPlayer().getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }

        cir.setReturnValue(ActionResult.SUCCESS);
    }
}

package net.cozystudios.cozystudioscore.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbacusItem extends Item {

    private static final String TAG_BOUND_X = "boundPosX";
    private static final String TAG_BOUND_Y = "boundPosY";
    private static final String TAG_BOUND_Z = "boundPosZ";
    private static final int UNBOUND_SENTINEL = -999;

    public AbacusItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        ItemStack stack = context.getStack();
        World world = context.getWorld();

        if (isBound(stack)) {
            unbind(stack);
            if (!world.isClient) {
                player.sendMessage(
                        Text.literal("Abacus unbound").formatted(Formatting.GOLD),
                        true
                );
            }
            return ActionResult.SUCCESS;
        } else {
            BlockPos pos = context.getBlockPos();
            bind(stack, pos);
            if (!world.isClient) {
                player.sendMessage(
                        Text.literal("Abacus bound to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                                .formatted(Formatting.GOLD),
                        true
                );
            }
            return ActionResult.SUCCESS;
        }
    }

    public static boolean isBound(ItemStack stack) {
        if (!stack.hasNbt()) return false;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(TAG_BOUND_Y)) return false;
        return nbt.getInt(TAG_BOUND_Y) != UNBOUND_SENTINEL;
    }

    @Nullable
    public static BlockPos getBoundPos(ItemStack stack) {
        if (!isBound(stack)) return null;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;
        return new BlockPos(
                nbt.getInt(TAG_BOUND_X),
                nbt.getInt(TAG_BOUND_Y),
                nbt.getInt(TAG_BOUND_Z)
        );
    }

    public static void bind(ItemStack stack, BlockPos pos) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(TAG_BOUND_X, pos.getX());
        nbt.putInt(TAG_BOUND_Y, pos.getY());
        nbt.putInt(TAG_BOUND_Z, pos.getZ());
    }

    public static void unbind(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(TAG_BOUND_Y, UNBOUND_SENTINEL);
    }

    public static int getDistance(ItemStack stack, @Nullable BlockPos target) {
        BlockPos bound = getBoundPos(stack);
        if (bound == null || target == null) return -1;
        return bound.getManhattanDistance(target);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {
        if (isBound(stack)) {
            BlockPos pos = getBoundPos(stack);
            if (pos != null) {
                tooltip.add(Text.literal("Bound to: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                        .formatted(Formatting.GOLD));
            }
        } else {
            tooltip.add(Text.literal("Right-click a block to bind").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal("Right-click again to unbind").formatted(Formatting.DARK_GRAY));
    }
}

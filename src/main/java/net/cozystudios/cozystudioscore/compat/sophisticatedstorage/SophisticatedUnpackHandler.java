package net.cozystudios.cozystudioscore.compat.sophisticatedstorage;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Method;

public final class SophisticatedUnpackHandler {

    private static volatile Method isPackedMethod;
    private static volatile Method setPackedMethod;
    private static volatile boolean resolved;

    private SophisticatedUnpackHandler() {}

    public static void register() {
        if (!FabricLoader.getInstance().isModLoaded("sophisticatedstorage")) return;

        UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockHitResult hit) -> {
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
            if (!player.isSneaking()) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() != Items.SHEARS) return ActionResult.PASS;

            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockEntity be = world.getBlockEntity(pos);
            if (be == null) return ActionResult.PASS;

            String cls = be.getClass().getName();
            if (!cls.startsWith("net.p3pp3rf1y.sophisticatedstorage.block.")) return ActionResult.PASS;

            resolveMethods(be.getClass());
            if (isPackedMethod == null || setPackedMethod == null) return ActionResult.PASS;

            boolean packed;
            try {
                packed = (Boolean) isPackedMethod.invoke(be);
            } catch (Throwable t) {
                return ActionResult.PASS;
            }
            if (!packed) return ActionResult.PASS;

            if (world.isClient()) return ActionResult.SUCCESS;

            try {
                setPackedMethod.invoke(be, false);
            } catch (Throwable t) {
                CozyStudiosCore.LOGGER.error("[SophisticatedUnpackHandler] failed to unpack {}: {}", pos, t.toString());
                return ActionResult.PASS;
            }
            be.markDirty();
            world.updateListeners(pos, state, state, 3);

            if (!player.getAbilities().creativeMode) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
            }
            world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);

            return ActionResult.SUCCESS;
        });

        CozyStudiosCore.LOGGER.info("[SophisticatedUnpackHandler] registered");
    }

    private static void resolveMethods(Class<?> beClass) {
        if (resolved) return;
        synchronized (SophisticatedUnpackHandler.class) {
            if (resolved) return;
            try {
                Class<?> cursor = beClass;
                while (cursor != null && cursor != Object.class) {
                    try {
                        isPackedMethod = cursor.getDeclaredMethod("isPacked");
                        setPackedMethod = cursor.getDeclaredMethod("setPacked", boolean.class);
                        isPackedMethod.setAccessible(true);
                        setPackedMethod.setAccessible(true);
                        break;
                    } catch (NoSuchMethodException ignored) {
                        cursor = cursor.getSuperclass();
                    }
                }
            } catch (Throwable t) {
                CozyStudiosCore.LOGGER.error("[SophisticatedUnpackHandler] reflection resolve failed: {}", t.toString());
            }
            resolved = true;
        }
    }
}

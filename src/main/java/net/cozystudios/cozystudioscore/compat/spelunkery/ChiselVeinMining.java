package net.cozystudios.cozystudioscore.compat.spelunkery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public final class ChiselVeinMining {

    private static final int MAX_BLOCKS = 64;
    private static final boolean DIAGONAL = true;

    private static final ThreadLocal<Pending> PENDING = new ThreadLocal<>();

    private static Enchantment veinMiningEnchant;
    private static boolean veinMiningEnchantResolved;

    private ChiselVeinMining() {}

    public static void captureTarget(World world, LivingEntity user, ItemStack stack, int remainingTicks) {
        if (world.isClient || !(user instanceof ServerPlayerEntity player)) {
            PENDING.remove();
            return;
        }
        float progress = (stack.getMaxUseTime() - remainingTicks) / 20.0F;
        if (progress < 1.0F) {
            PENDING.remove();
            return;
        }
        if (getEnchantLevel(stack) <= 0) {
            PENDING.remove();
            return;
        }

        BlockHitResult hit = raycast(player);
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            PENDING.remove();
            return;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PENDING.set(new Pending(pos, state, player));
    }

    public static void runVeinMining(World world, LivingEntity user, ItemStack stack) {
        Pending pending = PENDING.get();
        PENDING.remove();
        if (pending == null || world.isClient) return;

        BlockPos sourcePos = pending.pos;
        BlockState sourceState = pending.state;
        PlayerEntity player = pending.player;

        if (!world.getBlockState(sourcePos).isAir()) {
            return;
        }

        Block sourceBlock = sourceState.getBlock();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(sourcePos);
        Deque<BlockPos> queue = new ArrayDeque<>();
        enqueueNeighbors(queue, sourcePos);

        int broken = 0;
        while (!queue.isEmpty() && broken < MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            if (stack.isEmpty()) break;

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != sourceBlock) continue;

            ItemStack drop = sourceBlock.getPickStack(world, pos, state);
            if (drop == null || drop.isEmpty()) {
                drop = new ItemStack(sourceBlock.asItem());
            }

            world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            world.playSound(null, pos,
                    state.getSoundGroup().getBreakSound(),
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
            Block.dropStack(world, sourcePos, drop);

            stack.damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            broken++;

            enqueueNeighbors(queue, pos);
        }
    }

    private static void enqueueNeighbors(Deque<BlockPos> queue, BlockPos origin) {
        if (DIAGONAL) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        queue.add(origin.add(dx, dy, dz));
                    }
                }
            }
        } else {
            queue.add(origin.up());
            queue.add(origin.down());
            queue.add(origin.north());
            queue.add(origin.south());
            queue.add(origin.east());
            queue.add(origin.west());
        }
    }

    private static BlockHitResult raycast(ServerPlayerEntity player) {
        float reach = 5.0F;
        Vec3d eye = player.getCameraPosVec(1.0F);
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d end = eye.add(look.x * reach, look.y * reach, look.z * reach);
        return player.getWorld().raycast(new RaycastContext(
                eye, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player));
    }

    private static int getEnchantLevel(ItemStack stack) {
        Enchantment ench = resolveEnchant();
        if (ench == null) return 0;
        return EnchantmentHelper.getLevel(ench, stack);
    }

    private static Enchantment resolveEnchant() {
        if (!veinMiningEnchantResolved) {
            veinMiningEnchant = Registries.ENCHANTMENT.get(new Identifier("veinmining", "vein_mining"));
            veinMiningEnchantResolved = true;
        }
        return veinMiningEnchant;
    }

    private record Pending(BlockPos pos, BlockState state, ServerPlayerEntity player) {}
}

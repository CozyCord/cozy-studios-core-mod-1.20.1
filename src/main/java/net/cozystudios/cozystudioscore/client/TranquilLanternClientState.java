package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranquilLanternClientState {

    private static final Map<BlockPos, BlockState> CLIENT_LANTERNS = new HashMap<>();

    public static Set<BlockPos> getLanterns() {
        return Collections.unmodifiableSet(CLIENT_LANTERNS.keySet());
    }

    public static BlockState getLanternState(BlockPos pos) {
        return CLIENT_LANTERNS.get(pos);
    }

    public static void setAll(Set<BlockPos> positions) {
        CLIENT_LANTERNS.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            for (BlockPos p : positions) {
                BlockState state = client.world.getBlockState(p);
                CLIENT_LANTERNS.put(p.toImmutable(), state);
            }
        }
    }

    public static void add(BlockPos pos) {
        if (pos != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                BlockState state = client.world.getBlockState(pos);
                CLIENT_LANTERNS.put(pos.toImmutable(), state);
            }
        }
    }

    public static void remove(BlockPos pos) {
        if (pos != null) CLIENT_LANTERNS.remove(pos);
    }

    public static void clear() {
        CLIENT_LANTERNS.clear();
    }

    public static void rescanAroundPlayer(MinecraftClient client) {
        CLIENT_LANTERNS.clear();

        if (client.world == null || client.player == null) return;

        // Use the largest radius to ensure we find all lanterns
        int maxRadius = Math.max(
            Math.max(ModConfig.get().getTranquilLanternRadius(), ModConfig.get().getGoldenTranquilLanternRadius()),
            Math.max(ModConfig.get().getDiamondTranquilLanternRadius(), ModConfig.get().getNetheriteTranquilLanternRadius())
        );

        BlockPos center = client.player.getBlockPos();
        BlockPos min = center.add(-maxRadius, -maxRadius, -maxRadius);
        BlockPos max = center.add(maxRadius, maxRadius, maxRadius);

        BlockPos.iterate(min, max).forEach(pos -> {
            BlockState state = client.world.getBlockState(pos);
            if (state.isOf(ModBlocks.TRANQUIL_LANTERN) ||
                state.isOf(ModBlocks.GOLDEN_TRANQUIL_LANTERN) ||
                state.isOf(ModBlocks.DIAMOND_TRANQUIL_LANTERN) ||
                state.isOf(ModBlocks.NETHERITE_TRANQUIL_LANTERN)) {
                CLIENT_LANTERNS.put(pos.toImmutable(), state);
            }
        });
    }
}
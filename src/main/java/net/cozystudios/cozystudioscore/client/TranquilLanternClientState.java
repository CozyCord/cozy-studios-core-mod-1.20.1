package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.config.TranquilLanternsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranquilLanternClientState {

    private static final Map<BlockPos, BlockState> CLIENT_LANTERNS = new HashMap<>();

    // Server-side config values (null = not connected to server / use local config)
    private static Integer serverTranquilRadius = null;
    private static Integer serverGoldenRadius = null;
    private static Integer serverDiamondRadius = null;
    private static Integer serverNetheriteRadius = null;

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

    // Server config sync methods
    public static void setServerRadiusValues(int tranquil, int golden, int diamond, int netherite) {
        serverTranquilRadius = tranquil;
        serverGoldenRadius = golden;
        serverDiamondRadius = diamond;
        serverNetheriteRadius = netherite;
    }

    public static void clearServerRadiusValues() {
        serverTranquilRadius = null;
        serverGoldenRadius = null;
        serverDiamondRadius = null;
        serverNetheriteRadius = null;
    }

    // Get radius methods that prefer server values if available
    public static int getTranquilLanternRadius() {
        return serverTranquilRadius != null ? serverTranquilRadius : TranquilLanternsConfig.get().getTranquilLanternRadius();
    }

    public static int getGoldenTranquilLanternRadius() {
        return serverGoldenRadius != null ? serverGoldenRadius : TranquilLanternsConfig.get().getGoldenTranquilLanternRadius();
    }

    public static int getDiamondTranquilLanternRadius() {
        return serverDiamondRadius != null ? serverDiamondRadius : TranquilLanternsConfig.get().getDiamondTranquilLanternRadius();
    }

    public static int getNetheriteTranquilLanternRadius() {
        return serverNetheriteRadius != null ? serverNetheriteRadius : TranquilLanternsConfig.get().getNetheriteTranquilLanternRadius();
    }

    public static void rescanAroundPlayer(MinecraftClient client) {
        CLIENT_LANTERNS.clear();

        if (client.world == null || client.player == null) return;

        // Use the largest radius to ensure we find all lanterns
        int maxRadius = Math.max(
            Math.max(getTranquilLanternRadius(), getGoldenTranquilLanternRadius()),
            Math.max(getDiamondTranquilLanternRadius(), getNetheriteTranquilLanternRadius())
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
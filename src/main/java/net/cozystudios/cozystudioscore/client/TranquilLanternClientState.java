package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.config.TranquilLanternsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TranquilLanternClientState {

    private static final Set<BlockPos> CLIENT_LANTERNS = new HashSet<>();

    private static Integer serverTranquilRadius = null;
    private static Integer serverGoldenRadius = null;
    private static Integer serverDiamondRadius = null;
    private static Integer serverNetheriteRadius = null;

    public static Set<BlockPos> getLanterns() {
        return Collections.unmodifiableSet(CLIENT_LANTERNS);
    }

    public static BlockState getLanternState(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return null;
        BlockState state = client.world.getBlockState(pos);
        if (state.isOf(ModBlocks.TRANQUIL_LANTERN) ||
            state.isOf(ModBlocks.GOLDEN_TRANQUIL_LANTERN) ||
            state.isOf(ModBlocks.DIAMOND_TRANQUIL_LANTERN) ||
            state.isOf(ModBlocks.NETHERITE_TRANQUIL_LANTERN)) {
            return state;
        }
        return null;
    }

    public static void setAll(Set<BlockPos> positions) {
        CLIENT_LANTERNS.clear();
        for (BlockPos p : positions) {
            CLIENT_LANTERNS.add(p.toImmutable());
        }
    }

    public static void add(BlockPos pos) {
        if (pos != null) CLIENT_LANTERNS.add(pos.toImmutable());
    }

    public static void remove(BlockPos pos) {
        if (pos != null) CLIENT_LANTERNS.remove(pos);
    }

    public static void clear() {
        CLIENT_LANTERNS.clear();
    }

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
                CLIENT_LANTERNS.add(pos.toImmutable());
            }
        });
    }
}

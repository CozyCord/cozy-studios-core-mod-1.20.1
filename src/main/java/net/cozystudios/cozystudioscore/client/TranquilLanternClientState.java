package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TranquilLanternClientState {

    private static final Set<BlockPos> CLIENT_LANTERNS = new HashSet<>();

    public static Set<BlockPos> getLanterns() {
        return Collections.unmodifiableSet(CLIENT_LANTERNS);
    }

    public static void setAll(Set<BlockPos> positions) {
        CLIENT_LANTERNS.clear();
        for (BlockPos p : positions) CLIENT_LANTERNS.add(p.toImmutable());
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

    public static void rescanAroundPlayer(MinecraftClient client) {
        CLIENT_LANTERNS.clear();

        if (client.world == null || client.player == null) return;

        int radius = ModConfig.get().tranquilLanternRadius;

        BlockPos center = client.player.getBlockPos();
        BlockPos min = center.add(-radius, -radius, -radius);
        BlockPos max = center.add(radius, radius, radius);

        BlockPos.iterate(min, max).forEach(pos -> {
            if (client.world.getBlockState(pos).isOf(ModBlocks.TRANQUIL_LANTERN)) {
                CLIENT_LANTERNS.add(pos.toImmutable());
            }
        });
    }
}
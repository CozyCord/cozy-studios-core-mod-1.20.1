package net.cozystudios.cozystudioscore.client;

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

    @Deprecated
    public static void rescanAroundPlayer(MinecraftClient client) {
        CLIENT_LANTERNS.clear();
    }
}
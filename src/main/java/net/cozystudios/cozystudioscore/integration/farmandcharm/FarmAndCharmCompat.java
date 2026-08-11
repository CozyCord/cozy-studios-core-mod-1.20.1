package net.cozystudios.cozystudioscore.integration.farmandcharm;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public final class FarmAndCharmCompat {
    private static final Identifier SPRINKLER_ID = new Identifier("farm_and_charm", "water_sprinkler");
    private static final boolean ENABLED = FabricLoader.getInstance().isModLoaded("farm_and_charm");
    private static final int RANGE = 8;

    private static volatile Block sprinklerBlock;
    private static volatile boolean sprinklerLookupDone;

    private FarmAndCharmCompat() {}

    public static boolean hasSprinklerNearby(WorldView world, BlockPos pos) {
        if (!ENABLED) return false;
        Block sprinkler = getSprinklerBlock();
        if (sprinkler == null) return false;

        int px = pos.getX();
        int py = pos.getY();
        int pz = pos.getZ();
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    cursor.set(px + dx, py + dy, pz + dz);
                    if (world.getBlockState(cursor).isOf(sprinkler)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Block getSprinklerBlock() {
        if (sprinklerLookupDone) return sprinklerBlock;
        synchronized (FarmAndCharmCompat.class) {
            if (sprinklerLookupDone) return sprinklerBlock;
            sprinklerBlock = Registries.BLOCK.containsId(SPRINKLER_ID)
                    ? Registries.BLOCK.get(SPRINKLER_ID)
                    : null;
            sprinklerLookupDone = true;
            return sprinklerBlock;
        }
    }
}

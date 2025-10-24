package net.cozystudios.cozystudioscore.world;

import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

public final class ModTradersSpawner {
    private static final Random RAND = new Random();
    private static int cooldown = 20 * 60 * 5;
    private static final int DESPAWN_TIME = 20 * 60 * 10;
    private static final double SPAWN_CHANCE = 0.25;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModTradersSpawner::tick);
    }

    private static void tick(MinecraftServer server) {
        if (--cooldown > 0) return;
        cooldown = 20 * 60 * 5;

        for (ServerWorld world : server.getWorlds()) {

            if (!world.getRegistryKey().equals(World.OVERWORLD)) continue;


            if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) continue;


            if (RAND.nextDouble() > SPAWN_CHANCE) continue;


            boolean hasTrader = !world.getEntitiesByType(ModEntities.MYSTICAL_TRADER, entity -> true).isEmpty();
            if (hasTrader) continue;


            var player = world.getRandomAlivePlayer();
            if (player == null) continue;


            BlockPos base = player.getBlockPos().add(RAND.nextInt(32) - 16, 0, RAND.nextInt(32) - 16);
            BlockPos surface = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, base);


            var trader = ModEntities.MYSTICAL_TRADER.create(world);
            if (trader != null) {
                trader.refreshPositionAndAngles(
                        surface.getX() + 0.5,
                        surface.getY(),
                        surface.getZ() + 0.5,
                        RAND.nextFloat() * 360f,
                        0
                );


                trader.setDespawnTimer(DESPAWN_TIME);

                world.spawnEntity(trader);
                break;
            }
        }
    }
}
package net.cozystudios.cozystudioscore.world;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.block.entity.ModBlockEntities;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

public class TranquilLanternSpawnBlocker {

    private static final Map<ServerWorld, Set<BlockPos>> ACTIVE_LANTERNS = new HashMap<>();
    private static int tickTimer = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TranquilLanternSpawnBlocker::tick);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            ModConfig.reload();
            refreshAllLanterns(server);
        });
    }

    private static void tick(MinecraftServer server) {
        if (++tickTimer < 10) return;
        tickTimer = 0;

        if (ACTIVE_LANTERNS.isEmpty()) return;

        int radius = ModConfig.get().tranquilLanternRadius;
        double radiusSq = radius * radius;

        for (ServerWorld world : server.getWorlds()) {
            Set<BlockPos> lanterns = ACTIVE_LANTERNS.get(world);
            if (lanterns == null || lanterns.isEmpty()) continue;

            for (Iterator<BlockPos> it = lanterns.iterator(); it.hasNext();) {
                BlockPos lanternPos = it.next();

                if (!world.isChunkLoaded(lanternPos)) continue;

                BlockState state = world.getBlockState(lanternPos);
                if (!state.isOf(ModBlocks.TRANQUIL_LANTERN)) {
                    it.remove();
                    continue;
                }

                Box box = new Box(
                        lanternPos.getX() - radius, lanternPos.getY() - radius, lanternPos.getZ() - radius,
                        lanternPos.getX() + radius, lanternPos.getY() + radius, lanternPos.getZ() + radius
                );

                List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, box,
                        mob -> mob.isAlive() && isHostileMob(mob));
                if (mobs.isEmpty()) continue;

                double cx = lanternPos.getX() + 0.5;
                double cy = lanternPos.getY() + 0.5;
                double cz = lanternPos.getZ() + 0.5;

                for (MobEntity mob : mobs) {
                    double dx = mob.getX() - cx;
                    double dy = mob.getY() - cy;
                    double dz = mob.getZ() - cz;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (mob instanceof PhantomEntity) {
                        if (distSq <= radiusSq) {
                            mob.kill();
                            continue;
                        }
                    }

                    // Push other mobs away
                    if (distSq < radiusSq && distSq > 0.001) {
                        double dist = Math.sqrt(distSq);
                        double pushStrength = 0.25 * (1.0 - (dist / radius));

                        mob.addVelocity((dx / dist) * pushStrength, 0.05, (dz / dist) * pushStrength);
                        mob.velocityModified = true;

                        // Clamp them to outer edge
                        if (dist < radius * 0.95) {
                            double edgeDist = (radius - dist) * 0.25;
                            mob.requestTeleport(
                                    mob.getX() + (dx / dist) * edgeDist,
                                    mob.getY(),
                                    mob.getZ() + (dz / dist) * edgeDist
                            );
                        }
                    }
                }
            }
        }
    }


    private static boolean isHostileMob(MobEntity mob) {
        return mob instanceof HostileEntity
                || mob instanceof SlimeEntity
                || mob instanceof MagmaCubeEntity
                || mob instanceof PhantomEntity;
    }


    public static void addLantern(ServerWorld world, BlockPos pos) {
        ACTIVE_LANTERNS.computeIfAbsent(world, k -> new HashSet<>()).add(pos.toImmutable());
    }


    public static void removeLantern(ServerWorld world, BlockPos pos) {
        Set<BlockPos> set = ACTIVE_LANTERNS.get(world);
        if (set != null) set.remove(pos.toImmutable());
    }

    public static void refreshAllLanterns(MinecraftServer server) {
        ACTIVE_LANTERNS.clear();

        for (ServerWorld world : server.getWorlds()) {
            Set<BlockPos> lanterns = new HashSet<>();

            world.getPlayers().forEach(player -> {
                BlockPos playerPos = player.getBlockPos();
                int scanRadius = 8;

                int chunkX = playerPos.getX() >> 4;
                int chunkZ = playerPos.getZ() >> 4;

                for (int dx = -scanRadius; dx <= scanRadius; dx++) {
                    for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                        if (world.isChunkLoaded(chunkX + dx, chunkZ + dz)) {
                            var chunk = world.getChunk(chunkX + dx, chunkZ + dz);
                            for (BlockEntity be : chunk.getBlockEntities().values()) {
                                if (be.getType() == ModBlockEntities.TRANQUIL_LANTERN) {
                                    lanterns.add(be.getPos().toImmutable());
                                }
                            }
                        }
                    }
                }
            });

            if (!lanterns.isEmpty()) {
                ACTIVE_LANTERNS.put(world, lanterns);
            }
        }
    }
}
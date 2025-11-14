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
import net.cozystudios.cozystudioscore.network.ModNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class TranquilLanternSpawnBlocker {

    private static final Map<ServerWorld, Set<BlockPos>> ACTIVE_LANTERNS = new HashMap<>();
    private static int tickTimer = 0;

    private static final Map<UUID, Integer> MOB_LINGER_TICKS = new HashMap<>();
    private static final int LINGER_TICKS_THRESHOLD = 60;
    private static final int TICK_INTERVAL = 10;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TranquilLanternSpawnBlocker::tick);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            ModConfig.reload();
            refreshAllLanterns(server);
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                syncLanternsToPlayer(p);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(TranquilLanternSpawnBlocker::refreshAllLanterns);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            syncLanternsToPlayer(handler.player);
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

                    if (mob instanceof PhantomEntity && distSq <= radiusSq) {
                        mob.kill();
                        MOB_LINGER_TICKS.remove(mob.getUuid());
                        continue;
                    }

                    boolean insideRadius = distSq <= radiusSq;

                    if (insideRadius) {
                        UUID id = mob.getUuid();
                        int current = MOB_LINGER_TICKS.getOrDefault(id, 0) + TICK_INTERVAL;

                        if (current >= LINGER_TICKS_THRESHOLD) {
                            mob.kill();
                            MOB_LINGER_TICKS.remove(id);
                            continue;
                        } else {
                            MOB_LINGER_TICKS.put(id, current);
                        }
                    } else {
                        MOB_LINGER_TICKS.remove(mob.getUuid());
                    }

                    if (insideRadius && distSq > 0.001) {
                        double dist = Math.sqrt(distSq);

                        double pushStrength = 0.15;

                        mob.addVelocity(
                                (dx / dist) * pushStrength,
                                0.12,
                                (dz / dist) * pushStrength
                        );

                        mob.velocityModified = true;
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

    private static TranquilLanternState getState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                TranquilLanternState::readNbt,
                TranquilLanternState::create,
                "tranquil_lanterns"
        );
    }



    public static void addLantern(ServerWorld world, BlockPos pos) {
        ACTIVE_LANTERNS.computeIfAbsent(world, w -> new HashSet<>()).add(pos.toImmutable());

        var state = getState(world);
        state.lanterns.add(pos.toImmutable());
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_ADD, buf);
        }
    }



    public static void removeLantern(ServerWorld world, BlockPos pos) {
        Set<BlockPos> set = ACTIVE_LANTERNS.get(world);
        if (set != null) set.remove(pos.toImmutable());

        var state = getState(world);
        state.lanterns.remove(pos.toImmutable());
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_REMOVE, buf);
        }
    }



    public static void refreshAllLanterns(MinecraftServer server) {
        ACTIVE_LANTERNS.clear();

        for (ServerWorld world : server.getWorlds()) {

            TranquilLanternState state = getState(world);

            Set<BlockPos> savedLanterns = new HashSet<>(state.lanterns);

            if (!savedLanterns.isEmpty()) {
                ACTIVE_LANTERNS.put(world, savedLanterns);
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            syncLanternsToPlayer(player);
        }
    }

    private static void syncLanternsToPlayer(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Set<BlockPos> lanterns = ACTIVE_LANTERNS.getOrDefault(world, Collections.emptySet());

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(lanterns.size());

        for (BlockPos pos : lanterns) {
            buf.writeBlockPos(pos);
        }

        ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_SYNC, buf);
    }

}
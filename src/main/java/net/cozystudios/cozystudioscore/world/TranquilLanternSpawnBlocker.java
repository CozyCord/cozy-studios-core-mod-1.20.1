package net.cozystudios.cozystudioscore.world;

import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.network.ModNetworking;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.entity.mob.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;

public class TranquilLanternSpawnBlocker {

    private static final Map<ServerWorld, Set<BlockPos>> ACTIVE_LANTERNS = new HashMap<>();

    private static final int LINGER_TICKS_THRESHOLD = 60;
    private static final int TICK_INTERVAL = 10;

    private static int tickTimer = 0;

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(TranquilLanternSpawnBlocker::tick);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            refreshAllLanterns(server);
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
                syncLanternsToPlayer(p);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(TranquilLanternSpawnBlocker::refreshAllLanterns);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                syncLanternsToPlayer(handler.player)
        );
    }

    private static Map<UUID, Integer> getLingerMap(ServerWorld world) {
        return TranquilLingerState.get(world).lingerTicks;
    }

    private static void tick(MinecraftServer server) {
        if (++tickTimer < TICK_INTERVAL) return;
        tickTimer = 0;

        if (ACTIVE_LANTERNS.isEmpty()) {
            for (ServerWorld w : server.getWorlds()) {
                getLingerMap(w).clear();
                TranquilLingerState.get(w).markDirty();
            }
            return;
        }

        int radius = ModConfig.get().tranquilLanternRadius;
        double radiusSq = radius * radius;

        for (ServerWorld world : server.getWorlds()) {

            Set<BlockPos> lanterns = ACTIVE_LANTERNS.get(world);
            if (lanterns == null || lanterns.isEmpty()) continue;

            lanterns.removeIf(pos -> !world.isChunkLoaded(pos));
            if (lanterns.isEmpty()) continue;

            Map<UUID, Integer> linger = getLingerMap(world);

            for (BlockPos lanternPos : lanterns) {

                Box box = new Box(
                        lanternPos.getX() - radius, lanternPos.getY() - radius, lanternPos.getZ() - radius,
                        lanternPos.getX() + radius, lanternPos.getY() + radius, lanternPos.getZ() + radius
                );

                List<MobEntity> mobs = world.getEntitiesByClass(MobEntity.class, box, mob ->
                        mob != null && !mob.isRemoved() && isHostileMob(mob)
                );

                if (mobs.isEmpty()) continue;

                double cx = lanternPos.getX() + 0.5;
                double cy = lanternPos.getY() + 0.5;
                double cz = lanternPos.getZ() + 0.5;

                for (MobEntity mob : mobs) {

                    UUID id = mob.getUuid();

                    double dx = mob.getX() - cx;
                    double dy = mob.getY() - cy;
                    double dz = mob.getZ() - cz;

                    double distSq2 = dx * dx + dy * dy + dz * dz;

                    if (mob instanceof PhantomEntity && distSq2 <= radiusSq) {
                        mob.kill();
                        linger.remove(id);
                        TranquilLingerState.get(world).markDirty();
                        continue;
                    }

                    boolean insideRadius = distSq2 <= radiusSq;

                    if (insideRadius) {

                        int current = linger.getOrDefault(id, 0) + TICK_INTERVAL;

                        if (current >= LINGER_TICKS_THRESHOLD) {
                            mob.kill();
                            linger.remove(id);
                            TranquilLingerState.get(world).markDirty();
                            continue;
                        } else {
                            linger.put(id, current);
                            TranquilLingerState.get(world).markDirty();
                        }

                        if (distSq2 > 0.001) {
                            double dist = Math.sqrt(distSq2);
                            double push = 0.15;

                            mob.addVelocity(
                                    (dx / dist) * push,
                                    0.12,
                                    (dz / dist) * push
                            );
                            mob.velocityModified = true;
                        }

                    } else {
                        if (linger.remove(id) != null) {
                            TranquilLingerState.get(world).markDirty();
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

    private static TranquilLanternState getState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                TranquilLanternState::readNbt,
                TranquilLanternState::create,
                "tranquil_lanterns"
        );
    }

    public static void addLantern(ServerWorld world, BlockPos pos) {
        ACTIVE_LANTERNS.computeIfAbsent(world, w -> new HashSet<>()).add(pos.toImmutable());

        TranquilLanternState state = getState(world);
        state.lanterns.add(pos.toImmutable());
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        for (ServerPlayerEntity p : world.getPlayers())
            ServerPlayNetworking.send(p, ModNetworking.TRANQUIL_LANTERN_ADD, buf);
    }

    public static void removeLantern(ServerWorld world, BlockPos pos) {
        Set<BlockPos> set = ACTIVE_LANTERNS.get(world);
        if (set != null) set.remove(pos.toImmutable());

        TranquilLanternState state = getState(world);
        state.lanterns.remove(pos.toImmutable());
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        for (ServerPlayerEntity p : world.getPlayers())
            ServerPlayNetworking.send(p, ModNetworking.TRANQUIL_LANTERN_REMOVE, buf);
    }

    public static void refreshAllLanterns(MinecraftServer server) {
        ACTIVE_LANTERNS.clear();

        for (ServerWorld world : server.getWorlds()) {
            TranquilLanternState state = getState(world);
            if (!state.lanterns.isEmpty())
                ACTIVE_LANTERNS.put(world, new HashSet<>(state.lanterns));
        }

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
            syncLanternsToPlayer(p);
    }

    private static void syncLanternsToPlayer(ServerPlayerEntity player) {
        Set<BlockPos> lanterns =
                ACTIVE_LANTERNS.getOrDefault(player.getServerWorld(), Collections.emptySet());

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(lanterns.size());
        for (BlockPos pos : lanterns) buf.writeBlockPos(pos);

        ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_SYNC, buf);
    }
}
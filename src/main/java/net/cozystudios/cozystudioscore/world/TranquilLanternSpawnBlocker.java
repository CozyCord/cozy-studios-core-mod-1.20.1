package net.cozystudios.cozystudioscore.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.network.ModNetworking;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class TranquilLanternSpawnBlocker {

    private static final Map<ServerWorld, Set<BlockPos>> ACTIVE_LANTERNS = new HashMap<>();
    private static final Map<ServerWorld, Long2ObjectOpenHashMap<ObjectOpenHashSet<BlockPos>>> LANTERNS_BY_CHUNK = new HashMap<>();

    private static final int LINGER_TICKS_THRESHOLD = 60;
    private static final int TICK_INTERVAL = 10;

    private static int tickTimer = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TranquilLanternSpawnBlocker::tick);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            refreshAllLanterns(server);
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                syncLanternsToPlayer(p);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(TranquilLanternSpawnBlocker::refreshAllLanterns);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                syncLanternsToPlayer(handler.player)
        );
    }


    public static boolean hasAnyLanterns(ServerWorld world) {
        Set<BlockPos> set = ACTIVE_LANTERNS.get(world);
        return set != null && !set.isEmpty();
    }

    public static boolean isSpawnBlocked(ServerWorld world, BlockPos spawnPos) {
        if (!hasAnyLanterns(world)) return false;

        // Use the maximum possible radius to check all chunks
        int maxRadius = Math.max(
            Math.max(ModConfig.get().getTranquilLanternRadius(), ModConfig.get().getGoldenTranquilLanternRadius()),
            Math.max(ModConfig.get().getDiamondTranquilLanternRadius(), ModConfig.get().getNetheriteTranquilLanternRadius())
        );

        Long2ObjectOpenHashMap<ObjectOpenHashSet<BlockPos>> index = LANTERNS_BY_CHUNK.get(world);
        if (index == null || index.isEmpty()) return false;

        int chunkRadius = (maxRadius >> 4) + 1;

        int cx = spawnPos.getX() >> 4;
        int cz = spawnPos.getZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                long key = ChunkPos.toLong(cx + dx, cz + dz);
                ObjectOpenHashSet<BlockPos> inChunk = index.get(key);
                if (inChunk == null || inChunk.isEmpty()) continue;

                for (BlockPos lanternPos : inChunk) {
                    // Get the actual radius for this specific lantern
                    int radius = getRadiusForLantern(world, lanternPos);
                    int radiusSq = radius * radius;

                    if (lanternPos.getSquaredDistance(spawnPos) <= radiusSq) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static int getRadiusForLantern(ServerWorld world, BlockPos pos) {
        if (!world.isChunkLoaded(pos)) return ModConfig.get().getTranquilLanternRadius();

        net.minecraft.block.BlockState state = world.getBlockState(pos);
        if (state.isOf(net.cozystudios.cozystudioscore.block.ModBlocks.NETHERITE_TRANQUIL_LANTERN)) {
            return ModConfig.get().getNetheriteTranquilLanternRadius();
        } else if (state.isOf(net.cozystudios.cozystudioscore.block.ModBlocks.DIAMOND_TRANQUIL_LANTERN)) {
            return ModConfig.get().getDiamondTranquilLanternRadius();
        } else if (state.isOf(net.cozystudios.cozystudioscore.block.ModBlocks.GOLDEN_TRANQUIL_LANTERN)) {
            return ModConfig.get().getGoldenTranquilLanternRadius();
        } else {
            return ModConfig.get().getTranquilLanternRadius();
        }
    }


    private static Map<UUID, Integer> getLingerMap(ServerWorld world) {
        return TranquilLingerState.get(world).lingerTicks;
    }

    private static void tick(MinecraftServer server) {
        if (++tickTimer < TICK_INTERVAL) return;
        tickTimer = 0;

        if (ACTIVE_LANTERNS.isEmpty()) return;

        for (ServerWorld world : server.getWorlds()) {

            Set<BlockPos> lanterns = ACTIVE_LANTERNS.get(world);
            if (lanterns == null || lanterns.isEmpty()) continue;

            Map<UUID, Integer> linger = getLingerMap(world);

            ObjectOpenHashSet<UUID> seen = new ObjectOpenHashSet<>();
            boolean lingerChanged = false;

            for (BlockPos lanternPos : lanterns) {

                if (!world.isChunkLoaded(lanternPos)) continue;

                // Get the radius for this specific lantern type
                int radius = getRadiusForLantern(world, lanternPos);

                Box cube = new Box(
                        lanternPos.getX() - radius, lanternPos.getY() - radius, lanternPos.getZ() - radius,
                        lanternPos.getX() + radius, lanternPos.getY() + radius, lanternPos.getZ() + radius
                );

                List<MobEntity> mobs = world.getEntitiesByClass(
                        MobEntity.class,
                        cube,
                        mob -> mob != null && !mob.isRemoved() && isHostileMob(mob)
                );

                if (mobs.isEmpty()) continue;

                double cx = lanternPos.getX() + 0.5;
                double cy = lanternPos.getY() + 0.5;
                double cz = lanternPos.getZ() + 0.5;

                for (MobEntity mob : mobs) {
                    UUID id = mob.getUuid();
                    seen.add(id);

                    if (mob instanceof PhantomEntity phantom) {
                        phantom.kill();
                        if (linger.remove(id) != null) lingerChanged = true;
                        continue;
                    }

                    int current = linger.getOrDefault(id, 0) + TICK_INTERVAL;
                    if (current >= LINGER_TICKS_THRESHOLD) {
                        mob.kill();
                        if (linger.remove(id) != null) lingerChanged = true;
                        continue;
                    } else {
                        linger.put(id, current);
                        lingerChanged = true;
                    }

                    double dx = mob.getX() - cx;
                    double dy = mob.getY() - cy;
                    double dz = mob.getZ() - cz;
                    double d2 = dx * dx + dy * dy + dz * dz;

                    if (d2 > 0.001) {
                        double d = Math.sqrt(d2);
                        double push = 0.15;

                        mob.addVelocity(
                                (dx / d) * push,
                                0.12,
                                (dz / d) * push
                        );
                        mob.velocityModified = true;
                    }
                }
            }

            if (!linger.isEmpty()) {
                Iterator<UUID> it = linger.keySet().iterator();
                while (it.hasNext()) {
                    UUID id = it.next();

                    if (!seen.contains(id)) {
                        it.remove();
                        lingerChanged = true;
                        continue;
                    }

                    Entity e = world.getEntity(id);
                    if (e == null || e.isRemoved()) {
                        it.remove();
                        lingerChanged = true;
                    }
                }
            }

            if (lingerChanged) {
                TranquilLingerState.get(world).markDirty();
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

    private static void indexAdd(ServerWorld world, BlockPos pos) {
        LANTERNS_BY_CHUNK
                .computeIfAbsent(world, w -> new Long2ObjectOpenHashMap<>())
                .computeIfAbsent(new ChunkPos(pos).toLong(), k -> new ObjectOpenHashSet<>())
                .add(pos);
    }

    private static void indexRemove(ServerWorld world, BlockPos pos) {
        Long2ObjectOpenHashMap<ObjectOpenHashSet<BlockPos>> map = LANTERNS_BY_CHUNK.get(world);
        if (map == null) return;

        long key = new ChunkPos(pos).toLong();
        ObjectOpenHashSet<BlockPos> set = map.get(key);
        if (set == null) return;

        set.remove(pos);
        if (set.isEmpty()) map.remove(key);
    }

    public static void addLantern(ServerWorld world, BlockPos pos) {
        BlockPos p = pos.toImmutable();

        ACTIVE_LANTERNS.computeIfAbsent(world, w -> new HashSet<>()).add(p);
        indexAdd(world, p);

        TranquilLanternState state = getState(world);
        state.lanterns.add(p);
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(p);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_ADD, buf);
        }
    }

    public static void removeLantern(ServerWorld world, BlockPos pos) {
        BlockPos p = pos.toImmutable();

        Set<BlockPos> set = ACTIVE_LANTERNS.get(world);
        if (set != null) set.remove(p);
        indexRemove(world, p);

        TranquilLanternState state = getState(world);
        state.lanterns.remove(p);
        state.markDirty();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(p);

        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_REMOVE, buf);
        }
    }

    public static void refreshAllLanterns(MinecraftServer server) {
        ACTIVE_LANTERNS.clear();
        LANTERNS_BY_CHUNK.clear();

        for (ServerWorld world : server.getWorlds()) {
            TranquilLanternState state = getState(world);
            if (!state.lanterns.isEmpty()) {
                HashSet<BlockPos> set = new HashSet<>(state.lanterns);
                ACTIVE_LANTERNS.put(world, set);

                for (BlockPos p : set) {
                    indexAdd(world, p);
                }
            } else {
                Map<UUID, Integer> linger = getLingerMap(world);
                if (!linger.isEmpty()) {
                    linger.clear();
                    TranquilLingerState.get(world).markDirty();
                }
            }
        }

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            syncLanternsToPlayer(p);
        }
    }

    private static void syncLanternsToPlayer(ServerPlayerEntity player) {
        Set<BlockPos> lanterns = ACTIVE_LANTERNS.getOrDefault(player.getServerWorld(), Collections.emptySet());

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(lanterns.size());
        for (BlockPos pos : lanterns) {
            buf.writeBlockPos(pos);
        }

        ServerPlayNetworking.send(player, ModNetworking.TRANQUIL_LANTERN_SYNC, buf);
    }
}
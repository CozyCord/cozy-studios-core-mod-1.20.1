package net.cozystudios.cozystudioscore.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MeleonSpectacle {

    public static final String SPECTACLE_NBT_KEY = "CozyMeleonSpectacle";

    private enum Phase { RISING, STARING_BEFORE_JOIN, STARING_BEFORE_CHAT, STARING_AFTER_CHAT, ASCENDING }

    private static final ConcurrentHashMap<UUID, State> ACTIVE = new ConcurrentHashMap<>();
    private static final double HOVER_DISTANCE = 2.5;
    private static final double RISE_SPEED = 0.20;

    private MeleonSpectacle() {}

    public static boolean isSpectacleStack(ItemStack stack) {
        return stack != null && stack.hasNbt() && stack.getNbt().getBoolean(SPECTACLE_NBT_KEY);
    }

    private static final class State {
        Phase phase = Phase.RISING;
        int ticks = 0;
        UUID targetUuid;
    }

    public static boolean isActive(ItemEntity entity) {
        return ACTIVE.containsKey(entity.getUuid());
    }

    public static boolean anyActive() {
        return !ACTIVE.isEmpty();
    }

    public static boolean start(ItemEntity entity) {
        World world = entity.getWorld();
        if (!MeleonEasterEgg.tryClaimCooldown(world)) return false;

        PlayerEntity target = world.getClosestPlayer(entity, 64.0);
        if (target == null) return false;

        entity.setInvulnerable(true);
        entity.setNoGravity(true);
        entity.extinguish();
        entity.setPickupDelay(Integer.MAX_VALUE);
        entity.setNeverDespawn();
        entity.setVelocity(0, RISE_SPEED, 0);

        playSound(entity, SoundEvents.ENTITY_WARDEN_EMERGE, 1.2f, 0.7f);

        ItemStack tagged = entity.getStack().copy();
        NbtCompound nbt = tagged.getOrCreateNbt();
        nbt.putBoolean(SPECTACLE_NBT_KEY, true);
        entity.setStack(tagged);

        State s = new State();
        s.targetUuid = target.getUuid();
        ACTIVE.put(entity.getUuid(), s);
        return true;
    }

    public static void tick(ItemEntity entity) {
        State s = ACTIVE.get(entity.getUuid());
        if (s == null) return;

        World world = entity.getWorld();
        PlayerEntity target = world.getPlayerByUuid(s.targetUuid);
        if (target == null || target.isRemoved()) {
            ACTIVE.remove(entity.getUuid());
            entity.discard();
            return;
        }

        s.ticks++;
        entity.extinguish();

        switch (s.phase) {
            case RISING -> {
                if (entity.getY() >= target.getEyeY()) {
                    s.phase = Phase.STARING_BEFORE_JOIN;
                    s.ticks = 0;
                    hoverInFront(entity, target);
                    playSound(entity, SoundEvents.ENTITY_ENDERMAN_STARE, 1.5f, 0.6f);
                } else {
                    entity.setVelocity(0, RISE_SPEED, 0);
                }
            }
            case STARING_BEFORE_JOIN -> {
                hoverInFront(entity, target);
                if (s.ticks >= 20) {
                    MeleonEasterEgg.broadcastJoin(world.getServer());
                    s.phase = Phase.STARING_BEFORE_CHAT;
                    s.ticks = 0;
                }
            }
            case STARING_BEFORE_CHAT -> {
                hoverInFront(entity, target);
                if (s.ticks >= 55) {
                    MeleonEasterEgg.broadcastChat(world.getServer());
                    s.phase = Phase.STARING_AFTER_CHAT;
                    s.ticks = 0;
                }
            }
            case STARING_AFTER_CHAT -> {
                hoverInFront(entity, target);
                if (s.ticks >= 25) {
                    s.phase = Phase.ASCENDING;
                    s.ticks = 0;
                    playSound(entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.6f);
                    playSound(entity, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.7f);
                }
            }
            case ASCENDING -> {
                if (world instanceof ServerWorld sw) {
                    double x = entity.getX();
                    double baseY = entity.getY();
                    double z = entity.getZ();
                    sw.spawnParticles(ParticleTypes.END_ROD, x, baseY + 15, z, 60, 0.35, 15.0, 0.35, 0.4);
                    sw.spawnParticles(ParticleTypes.FLAME,   x, baseY + 10, z, 30, 0.15, 10.0, 0.15, 0.15);
                }
                entity.setPos(entity.getX(), entity.getY() + 6.0, entity.getZ());
                if (s.ticks >= 10) {
                    if (world instanceof ServerWorld sw) {
                        sw.spawnParticles(ParticleTypes.FLASH,   entity.getX(), entity.getY(), entity.getZ(), 4, 0, 0, 0, 0);
                        sw.spawnParticles(ParticleTypes.END_ROD, entity.getX(), entity.getY(), entity.getZ(), 80, 2.0, 2.0, 2.0, 0.6);
                    }
                    ACTIVE.remove(entity.getUuid());
                    entity.discard();
                }
            }
        }
    }

    private static void playSound(ItemEntity entity, SoundEvent sound, float volume, float pitch) {
        if (entity.getWorld() instanceof ServerWorld sw) {
            sw.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    sound, SoundCategory.HOSTILE, volume, pitch);
        }
    }

    private static void hoverInFront(ItemEntity entity, PlayerEntity target) {
        double yawRad = Math.toRadians(target.getYaw());
        double dx = -Math.sin(yawRad) * HOVER_DISTANCE;
        double dz = Math.cos(yawRad) * HOVER_DISTANCE;
        entity.setPosition(target.getX() + dx, target.getEyeY(), target.getZ() + dz);
        entity.setVelocity(Vec3d.ZERO);
    }
}

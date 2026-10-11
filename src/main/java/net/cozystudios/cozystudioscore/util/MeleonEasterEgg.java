package net.cozystudios.cozystudioscore.util;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MeleonEasterEgg {
    public static final Identifier MELEON_ID = new Identifier("perfectplushies", "sizableshrimp_plushie");
    static final String FAKE_NAME = "Codename_Meleon";
    private static final long COOLDOWN_MILLIS = 10L * 60L * 1000L;
    private static final long CHAT_DELAY_TICKS = 45L;

    private static long lastTriggerMillis = Long.MIN_VALUE / 2;
    private static final Deque<Scheduled> QUEUE = new ArrayDeque<>();

    private static volatile Item cachedMeleonItem;

    private MeleonEasterEgg() {}

    public static boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Item meleon = cachedMeleonItem;
        if (meleon == null) return false;
        return stack.getItem() == meleon;
    }

    public static boolean tryClaimCooldown(World world) {
        if (world == null || world.isClient()) return false;
        long now = System.currentTimeMillis();
        if (now - lastTriggerMillis < COOLDOWN_MILLIS) return false;
        lastTriggerMillis = now;
        return true;
    }

    public static void broadcastJoin(MinecraftServer server) {
        server.execute(() -> server.getPlayerManager().broadcast(
                Text.translatable("multiplayer.player.joined", FAKE_NAME).formatted(Formatting.YELLOW),
                false
        ));
    }

    public static void broadcastChat(MinecraftServer server) {
        server.execute(() -> server.getPlayerManager().broadcast(
                Text.literal("<" + FAKE_NAME + "> You'll regret that."),
                false
        ));
    }

    public static void trigger(World world) {
        if (!tryClaimCooldown(world)) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;

        broadcastJoin(server);
        schedule(server.getOverworld().getTime() + CHAT_DELAY_TICKS, () -> broadcastChat(server));
    }

    static synchronized void schedule(long tick, Runnable action) {
        QUEUE.add(new Scheduled(tick, action));
    }

    public static void register() {
        if (!FabricLoader.getInstance().isModLoaded("perfectplushies")) {
            CozyStudiosCore.LOGGER.info("[MeleonEasterEgg] perfectplushies not loaded, skipping register");
            return;
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Item plushie = Registries.ITEM.get(MELEON_ID);
            if (plushie != Items.AIR) {
                FuelRegistry.INSTANCE.add(plushie, 200);
                cachedMeleonItem = plushie;
                CozyStudiosCore.LOGGER.info("[MeleonEasterEgg] registered plushie as fuel + cached item ref");
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long now = server.getOverworld().getTime();
            synchronized (QUEUE) {
                while (!QUEUE.isEmpty() && QUEUE.peek().tick <= now) {
                    Scheduled s = QUEUE.poll();
                    try {
                        s.action.run();
                    } catch (Throwable t) {
                    }
                }
            }
        });
    }

    private record Scheduled(long tick, Runnable action) {}
}

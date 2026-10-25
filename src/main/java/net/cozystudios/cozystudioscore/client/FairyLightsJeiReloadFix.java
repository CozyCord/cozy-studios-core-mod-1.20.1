package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public final class FairyLightsJeiReloadFix {

    private static final int DELAY_TICKS = 60;
    private static int countdown = -1;
    private static boolean restartedThisConnection = false;
    private static volatile Object lifecycleHandler;

    private FairyLightsJeiReloadFix() {}

    public static void setLifecycleHandler(Object handler) {
        lifecycleHandler = handler;
    }

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("fairylights") || !loader.isModLoaded("jei")) return;

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isIntegratedServerRunning()) return;
            countdown = DELAY_TICKS;
            restartedThisConnection = false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            countdown = -1;
            restartedThisConnection = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (countdown < 0 || restartedThisConnection) return;
            if (countdown-- > 0) return;
            if (client.world == null) {
                countdown = -1;
                return;
            }
            restartedThisConnection = true;
            restartJei();
        });
    }

    private static void restartJei() {
        Object clh = lifecycleHandler;
        if (clh == null) {
            CozyStudiosCore.LOGGER.warn("[FL-FIX] lifecycle handler not captured, skipping JEI restart");
            return;
        }
        try {
            Method stopJei = findMethod(clh.getClass(), "stopJei");
            Method startJei = findMethod(clh.getClass(), "startJei");
            if (stopJei == null || startJei == null) {
                CozyStudiosCore.LOGGER.warn("[FL-FIX] stopJei={} startJei={} on {}",
                        stopJei != null, startJei != null, clh.getClass().getName());
                return;
            }
            stopJei.setAccessible(true);
            startJei.setAccessible(true);

            stopJei.invoke(clh);
            startJei.invoke(clh);
        } catch (Throwable t) {
            CozyStudiosCore.LOGGER.error("[FL-FIX] Failed to restart JEI: {}", t.toString());
        }
    }

    private static Method findMethod(Class<?> cls, String name) {
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) return m;
        }
        return null;
    }
}

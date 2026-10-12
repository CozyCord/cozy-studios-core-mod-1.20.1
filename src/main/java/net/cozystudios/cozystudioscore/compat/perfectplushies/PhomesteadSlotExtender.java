package net.cozystudios.cozystudioscore.compat.perfectplushies;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public final class PhomesteadSlotExtender {

    private static final String PLUSHIE_BLOCK_INIT = "com.nyfaria.perfectplushieapi.init.PlushieBlockInit";

    private PhomesteadSlotExtender() {}

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("phomesteadplushies") || !loader.isModLoaded("perfectplushies")) {
            return;
        }

        try {
            Class.forName("com.nyfaria.phomesteadplushies.init.BlockInit");
        } catch (ClassNotFoundException e) {
            CozyStudiosCore.LOGGER.warn("[PhomesteadSlotExtender] phomestead BlockInit not found, aborting");
            return;
        }

        addCommon(51);
        for (int slot = 52; slot <= 63; slot++) {
            addCommon(slot);
        }
    }

    private static void addCommon(int slot) {
        register("registerCommonPlayerPlushie", slot);
    }

    @SuppressWarnings("unused")
    private static void addRare(int slot) {
        register("registerRarePlayerPlushie", slot);
    }

    @SuppressWarnings("unused")
    private static void addEpic(int slot) {
        register("registerEpicPlayerPlushie", slot);
    }

    private static void register(String methodName, int slot) {
        String name = "homestead_player_" + slot;
        try {
            Class<?> cls = Class.forName(PLUSHIE_BLOCK_INIT);
            Method m = cls.getMethod(methodName, String.class);
            Object result = m.invoke(null, name);
            if (result == null) {
                CozyStudiosCore.LOGGER.warn("[PhomesteadSlotExtender] {} returned null for {}", methodName, name);
            } else {
                CozyStudiosCore.LOGGER.info("[PhomesteadSlotExtender] registered {} via {}", name, methodName);
            }
        } catch (Throwable t) {
            CozyStudiosCore.LOGGER.error("[PhomesteadSlotExtender] failed to register {}: {}", name, t.toString());
        }
    }
}

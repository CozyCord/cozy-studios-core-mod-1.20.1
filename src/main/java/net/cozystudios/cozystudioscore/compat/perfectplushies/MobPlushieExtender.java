package net.cozystudios.cozystudioscore.compat.perfectplushies;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Rarity;

import java.lang.reflect.Method;

public final class MobPlushieExtender {

    private static final String PLUSHIE_BLOCK_INIT = "com.nyfaria.perfectplushieapi.init.PlushieBlockInit";

    private MobPlushieExtender() {}

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("phomesteadplushies") || !loader.isModLoaded("perfectplushies")) {
            return;
        }

        try {
            Class.forName("com.nyfaria.phomesteadplushies.init.BlockInit");
        } catch (ClassNotFoundException e) {
            CozyStudiosCore.LOGGER.warn("[MobPlushieExtender] phomestead BlockInit not found, aborting");
            return;
        }

        addGeo("desert_fernling_plushie",  Rarity.UNCOMMON);
        addGeo("swamp_fernling_plushie",   Rarity.UNCOMMON);
        addGeo("ice_fernling_plushie",     Rarity.RARE);
        addGeo("ice_mushling_plushie",     Rarity.RARE);
        addGeo("lush_mushling_plushie",    Rarity.RARE);
        addGeo("crimson_mushling_plushie", Rarity.EPIC);
        addGeo("warped_mushling_plushie",  Rarity.EPIC);
    }

    private static void addGeo(String name, Rarity rarity) {
        try {
            Class<?> cls = Class.forName(PLUSHIE_BLOCK_INIT);
            Method m = cls.getMethod("registerGeoPlushie", String.class, Rarity.class);
            Object result = m.invoke(null, name, rarity);
            if (result == null) {
                CozyStudiosCore.LOGGER.warn("[MobPlushieExtender] registerGeoPlushie returned null for {}", name);
            } else {
                CozyStudiosCore.LOGGER.info("[MobPlushieExtender] registered {} ({})", name, rarity);
            }
        } catch (Throwable t) {
            CozyStudiosCore.LOGGER.error("[MobPlushieExtender] failed to register {}: {}", name, t.toString());
        }
    }
}

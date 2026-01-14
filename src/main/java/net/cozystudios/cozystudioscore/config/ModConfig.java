package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import java.util.ArrayList;
import java.util.List;

@Config(name = "cozystudioscore")
public class ModConfig implements ConfigData {

    // ======================
    // General
    // ======================

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int tranquilLanternRadiusPercent = 0;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean tranquilLanternBump = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean tranquilLanternBurn = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int goldenTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean goldenTranquilLanternBump = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean goldenTranquilLanternBurn = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int diamondTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean diamondTranquilLanternBump = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean diamondTranquilLanternBurn = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int netheriteTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean netheriteTranquilLanternBump = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean netheriteTranquilLanternBurn = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean showTitleScreenCredits = true;

    // ======================
    // Kiln
    // ======================
    @ConfigEntry.Category("kiln")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public List<String> extraKilnRecipes = new ArrayList<>(List.of(
            "minecraft:cobblestone -> minecraft:stone",
            "minecraft:sand -> minecraft:glass"
    ));

    @ConfigEntry.Category("kiln")
    @ConfigEntry.Gui.Tooltip
    public double kilnConfigDefaultXp = 0.1;

    @ConfigEntry.Category("kiln")
    @ConfigEntry.Gui.Tooltip
    public int kilnConfigDefaultTime = 100;

    // ======================
    // Gameplay
    // ======================
    @ConfigEntry.Category("gameplay")
    @ConfigEntry.Gui.Tooltip
    public boolean peacefulHunger = true;

    @ConfigEntry.Category("gameplay")
    @ConfigEntry.Gui.Tooltip
    public boolean enableMasonTrades = true;

    @ConfigEntry.Category("gameplay")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 16, max = 64)
    public int maxStackSizeOverride = 64;

    // ======================
    // Recolor-on-use
    // ======================
    @ConfigEntry.Category("recolor_on_use")
    @ConfigEntry.Gui.Tooltip
    public boolean enableRightClickRecolor = true;

    @ConfigEntry.Category("recolor_on_use")
    @ConfigEntry.Gui.Tooltip
    public boolean recolorWool = true;

    @ConfigEntry.Category("recolor_on_use")
    @ConfigEntry.Gui.Tooltip
    public boolean recolorBeds = true;

    @ConfigEntry.Category("recolor_on_use")
    @ConfigEntry.Gui.Tooltip
    public boolean recolorBanners = true;

    // ======================
    // Fernling
    // ======================
    @ConfigEntry.Category("fernling")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 1, max = 8)
    public int fernlingBonemealRadius = 3;

    @ConfigEntry.Category("fernling")
    @ConfigEntry.Gui.Tooltip
    public int fernlingBonemealCooldownMin = 600; // 30s

    @ConfigEntry.Category("fernling")
    @ConfigEntry.Gui.Tooltip
    public int fernlingBonemealCooldownMax = 2400; // 2 mins

    @ConfigEntry.Category("fernling")
    @ConfigEntry.Gui.Tooltip
    public double fernlingBonemealChance = 0.15; // 15%



    // ======================
    // Helpers
    // ======================
    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void reload() {
        AutoConfig.getConfigHolder(ModConfig.class).load();
    }

    // ======================
    // Tranquil Lantern Radius Calculation
    // ======================
    // Base radius values (constants - not exposed to config)
    @ConfigEntry.Gui.Excluded
    private static final int BASE_TRANQUIL_LANTERN_RADIUS = 13;

    @ConfigEntry.Gui.Excluded
    private static final int BASE_GOLDEN_TRANQUIL_LANTERN_RADIUS = 25;

    @ConfigEntry.Gui.Excluded
    private static final int BASE_DIAMOND_TRANQUIL_LANTERN_RADIUS = 50;

    @ConfigEntry.Gui.Excluded
    private static final int BASE_NETHERITE_TRANQUIL_LANTERN_RADIUS = 100;

    // Helper methods to calculate actual radius based on percentage
    public int getTranquilLanternRadius() {
        return BASE_TRANQUIL_LANTERN_RADIUS + (int)(BASE_TRANQUIL_LANTERN_RADIUS * (tranquilLanternRadiusPercent / 100.0));
    }

    public int getGoldenTranquilLanternRadius() {
        return BASE_GOLDEN_TRANQUIL_LANTERN_RADIUS + (int)(BASE_GOLDEN_TRANQUIL_LANTERN_RADIUS * (goldenTranquilLanternRadiusPercent / 100.0));
    }

    public int getDiamondTranquilLanternRadius() {
        return BASE_DIAMOND_TRANQUIL_LANTERN_RADIUS + (int)(BASE_DIAMOND_TRANQUIL_LANTERN_RADIUS * (diamondTranquilLanternRadiusPercent / 100.0));
    }

    public int getNetheriteTranquilLanternRadius() {
        return BASE_NETHERITE_TRANQUIL_LANTERN_RADIUS + (int)(BASE_NETHERITE_TRANQUIL_LANTERN_RADIUS * (netheriteTranquilLanternRadiusPercent / 100.0));
    }
}
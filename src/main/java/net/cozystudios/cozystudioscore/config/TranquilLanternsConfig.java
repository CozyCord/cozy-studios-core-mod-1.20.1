package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "tranquil_lanterns")
public class TranquilLanternsConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int tranquilLanternRadiusPercent = 0;

    @ConfigEntry.Gui.Tooltip
    public boolean tranquilLanternBump = true;

    @ConfigEntry.Gui.Tooltip
    public boolean tranquilLanternBurn = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int goldenTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Gui.Tooltip
    public boolean goldenTranquilLanternBump = true;

    @ConfigEntry.Gui.Tooltip
    public boolean goldenTranquilLanternBurn = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int diamondTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Gui.Tooltip
    public boolean diamondTranquilLanternBump = true;

    @ConfigEntry.Gui.Tooltip
    public boolean diamondTranquilLanternBurn = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int netheriteTranquilLanternRadiusPercent = 0;

    @ConfigEntry.Gui.Tooltip
    public boolean netheriteTranquilLanternBump = true;

    @ConfigEntry.Gui.Tooltip
    public boolean netheriteTranquilLanternBurn = false;

    // ======================
    // Radius Calculation
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

    public static TranquilLanternsConfig get() {
        return ModConfig.get().tranquilLanternsConfig;
    }
}

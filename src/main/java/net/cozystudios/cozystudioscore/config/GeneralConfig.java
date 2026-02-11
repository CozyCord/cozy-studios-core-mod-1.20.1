package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "general")
public class GeneralConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean showTitleScreenCredits = true;

    @ConfigEntry.Gui.Tooltip
    public boolean peacefulHunger = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableMasonTrades = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 16, max = 64)
    public int maxStackSizeOverride = 64;

    public static GeneralConfig get() {
        return ModConfig.get().generalConfig;
    }
}

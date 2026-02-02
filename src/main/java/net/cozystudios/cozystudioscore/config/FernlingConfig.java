package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "fernling")
public class FernlingConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public int fernlingBonemealCooldownMin = 600; // 30s

    @ConfigEntry.Gui.Tooltip
    public int fernlingBonemealCooldownMax = 2400; // 2 mins

    @ConfigEntry.Gui.Tooltip
    public double fernlingBonemealChance = 0.15; // 15%

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 1, max = 8)
    public int fernlingBonemealRadius = 3;

    public static FernlingConfig get() {
        return ModConfig.get().fernlingConfig;
    }
}

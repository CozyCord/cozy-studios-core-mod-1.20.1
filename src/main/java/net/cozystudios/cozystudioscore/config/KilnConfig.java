package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "kiln")
public class KilnConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public double kilnConfigDefaultXp = 0.1;

    @ConfigEntry.Gui.Tooltip
    public int kilnConfigDefaultTime = 100;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public List<String> extraKilnRecipes = new ArrayList<>(List.of(
            "minecraft:cobblestone -> minecraft:stone",
            "minecraft:sand -> minecraft:glass"
    ));

    public static KilnConfig get() {
        return ModConfig.get().kilnConfig;
    }
}

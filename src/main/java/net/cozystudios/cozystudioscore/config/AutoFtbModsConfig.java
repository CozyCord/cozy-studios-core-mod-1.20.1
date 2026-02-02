package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "auto_ftb_mods")
public class AutoFtbModsConfig {
    @ConfigEntry.Gui.Tooltip
    public boolean enableAutoFtbMods = true;

    @ConfigEntry.Gui.Tooltip
    public String ftbTeamsHash = "";

    @ConfigEntry.Gui.Tooltip
    public String ftbLibraryHash = "";

    @ConfigEntry.Gui.Tooltip
    public String ftbEssentialsHash = "";

    @ConfigEntry.Gui.Tooltip
    public String ftbFilterHash = "";

    @ConfigEntry.Gui.Tooltip
    public String ftbQuestsHash = "";

    @ConfigEntry.Gui.Tooltip
    public String ftbXmodHash = "";

    @ConfigEntry.Gui.Tooltip
    public String questAdditionsHash = "";

    public static AutoFtbModsConfig get() {
        return ModConfig.get().autoFtbModsConfig;
    }
}

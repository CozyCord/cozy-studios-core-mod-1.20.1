package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "auto_ftb_mods")
public class AutoFtbModsConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableAutoFtbMods = true;

    @ConfigEntry.Gui.Tooltip
    public String ftbTeamsHash = "515a933d22c60d0f9c249a6eef02402afc2695ac";

    @ConfigEntry.Gui.Tooltip
    public String ftbLibraryHash = "c7813af51352624f56b4604f2e431684f1e2c563";

    @ConfigEntry.Gui.Tooltip
    public String ftbEssentialsHash = "460ec092156d6b457bd91dc463ac1ee49e22442d";

    @ConfigEntry.Gui.Tooltip
    public String ftbFilterHash = "6e96cdbc84065682eb9920a023313aaffa857dff";

    @ConfigEntry.Gui.Tooltip
    public String ftbQuestsHash = "4e960a89918056dd24d45393a8aec43e9e90d345";

    @ConfigEntry.Gui.Tooltip
    public String ftbXmodHash = "20ff2644eb3580563e9b47758487fed2686682f3";

    @ConfigEntry.Gui.Tooltip
    public String questAdditionsHash = "a15613625cccf31508230399cffa7674bdaaf74f";

    public static AutoFtbModsConfig get() {
        return ModConfig.get().autoFtbModsConfig;
    }
}

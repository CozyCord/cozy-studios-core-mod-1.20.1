package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "recolor_on_use")
public class RecolorOnUseConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableRightClickRecolor = true;

    @ConfigEntry.Gui.Tooltip
    public boolean recolorWool = true;

    @ConfigEntry.Gui.Tooltip
    public boolean recolorBeds = true;

    @ConfigEntry.Gui.Tooltip
    public boolean recolorBanners = true;

    public static RecolorOnUseConfig get() {
        return ModConfig.get().recolorOnUseConfig;
    }
}

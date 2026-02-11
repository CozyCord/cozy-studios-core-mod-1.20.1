package net.cozystudios.cozystudioscore.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "cozystudioscore")
public class ModConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    GeneralConfig generalConfig = new GeneralConfig();

    @ConfigEntry.Category("default")
    @ConfigEntry.Gui.TransitiveObject
    TranquilLanternsConfig tranquilLanternsConfig = new TranquilLanternsConfig();

    @ConfigEntry.Category("recolor_on_use")
    @ConfigEntry.Gui.TransitiveObject
    RecolorOnUseConfig recolorOnUseConfig = new RecolorOnUseConfig();

    @ConfigEntry.Category("kiln")
    @ConfigEntry.Gui.TransitiveObject
    KilnConfig kilnConfig = new KilnConfig();

    @ConfigEntry.Category("fernling")
    @ConfigEntry.Gui.TransitiveObject
    FernlingConfig fernlingConfig = new FernlingConfig();

    @ConfigEntry.Category("auto_ftb_mods")
    @ConfigEntry.Gui.TransitiveObject
    AutoFtbModsConfig autoFtbModsConfig = new AutoFtbModsConfig();

    // ======================
    // Helpers
    // ======================
    public static void register() {
        AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
    }

    public static void reload() {
        AutoConfig.getConfigHolder(ModConfig.class).load();
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
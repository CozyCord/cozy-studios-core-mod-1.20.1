package net.cozystudios.cozystudioscore.screen;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<KilnScreenHandler> KILN_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        KILN_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                new Identifier(CozyStudiosCore.MOD_ID, "kiln"),
                new ScreenHandlerType<>(KilnScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );

    }
}
package net.cozystudios.cozystudioscore.world.gen;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public final class ModFeatures {

    public static final Feature<DefaultFeatureConfig> MYSTICAL_CAVE_VINES_FEATURE =
            Registry.register(Registries.FEATURE,
                    new Identifier(CozyStudiosCore.MOD_ID, "mystical_cave_vine"),
                    new MysticalCaveVinesFeature(DefaultFeatureConfig.CODEC));

    private ModFeatures() {}

    public static void register() {
    }
}

package net.cozystudios.cozystudioscore.worldgen;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class MysticalCaveVinesBiomeInjector {

    private static final RegistryKey<PlacedFeature> MYSTICAL_CAVE_VINE_PLACED = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            new Identifier(CozyStudiosCore.MOD_ID, "mystical_cave_vine")
    );

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES),
                GenerationStep.Feature.VEGETAL_DECORATION,
                MYSTICAL_CAVE_VINE_PLACED
        );
        CozyStudiosCore.LOGGER.info("Registered mystical cave vine placement into lush_caves");
    }
}

package net.cozystudios.cozystudioscore.worldgen;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.function.Predicate;

public class KiwiTreeBiomeInjector {

    private static final RegistryKey<PlacedFeature> KIWI_TREE_PLACED = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            new Identifier("clutter", "kiwi_tree")
    );

    private static final TagKey<Biome> C_IS_JUNGLE = TagKey.of(
            RegistryKeys.BIOME,
            new Identifier("c", "is_jungle")
    );

    public static void register() {
        if (!FabricLoader.getInstance().isModLoaded("clutter")) return;

        Predicate<BiomeSelectionContext> jungles = BiomeSelectors.tag(BiomeTags.IS_JUNGLE)
                .or(BiomeSelectors.tag(C_IS_JUNGLE));

        BiomeModifications.addFeature(
                jungles,
                GenerationStep.Feature.VEGETAL_DECORATION,
                KIWI_TREE_PLACED
        );

        CozyStudiosCore.LOGGER.info("Registered kiwi tree placement into jungle biomes");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var registry = server.getRegistryManager().get(RegistryKeys.PLACED_FEATURE);
            boolean present = registry.contains(KIWI_TREE_PLACED);
            if (present) {
                CozyStudiosCore.LOGGER.info("Placed feature {} is loaded in the registry", KIWI_TREE_PLACED.getValue());
            } else {
                CozyStudiosCore.LOGGER.error("Placed feature {} is MISSING from the registry — datapack file was not loaded", KIWI_TREE_PLACED.getValue());
            }
        });
    }
}

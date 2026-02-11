package net.cozystudios.cozystudioscore.world.gen;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.entity.variant.FernlingVariant;
import net.cozystudios.cozystudioscore.entity.variant.MushlingVariant;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawns {

    public static final Map<MushlingVariant, Set<RegistryKey<Biome>>> VARIANT_BIOMES =
            new EnumMap<>(MushlingVariant.class);

    public static final Map<FernlingVariant, Set<RegistryKey<Biome>>> FERNLING_VARIANT_BIOMES =
            new EnumMap<>(FernlingVariant.class);

    static {
        // =========================
        //  MUSHLINGS
        // =========================
        VARIANT_BIOMES.put(MushlingVariant.DEFAULT, Set.of(
                BiomeKeys.FOREST,
                BiomeKeys.DARK_FOREST,
                BiomeKeys.MEADOW,
                BiomeKeys.MUSHROOM_FIELDS
        ));

        VARIANT_BIOMES.put(MushlingVariant.ICE, Set.of(
                BiomeKeys.GROVE,
                BiomeKeys.SNOWY_TAIGA,
                BiomeKeys.SNOWY_SLOPES,
                BiomeKeys.ICE_SPIKES,
                BiomeKeys.FROZEN_PEAKS,
                BiomeKeys.SNOWY_BEACH,
                BiomeKeys.SNOWY_PLAINS
        ));

        VARIANT_BIOMES.put(MushlingVariant.CRIMSON, Set.of(
                BiomeKeys.CRIMSON_FOREST
        ));

        VARIANT_BIOMES.put(MushlingVariant.LUSH, Set.of(
                BiomeKeys.LUSH_CAVES
        ));

        VARIANT_BIOMES.put(MushlingVariant.WARPED, Set.of(
                BiomeKeys.WARPED_FOREST
        ));

        // =========================
        //  FERNLINGS
        // =========================
        FERNLING_VARIANT_BIOMES.put(FernlingVariant.DEFAULT, Set.of(
                BiomeKeys.FOREST,
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.FLOWER_FOREST,
                BiomeKeys.DARK_FOREST
        ));

        FERNLING_VARIANT_BIOMES.put(FernlingVariant.DESERT, Set.of(
                BiomeKeys.DESERT,
                BiomeKeys.BADLANDS,
                BiomeKeys.ERODED_BADLANDS,
                BiomeKeys.WOODED_BADLANDS
        ));

        FERNLING_VARIANT_BIOMES.put(FernlingVariant.ICE, Set.of(
                BiomeKeys.GROVE,
                BiomeKeys.SNOWY_TAIGA,
                BiomeKeys.SNOWY_SLOPES,
                BiomeKeys.ICE_SPIKES,
                BiomeKeys.FROZEN_PEAKS,
                BiomeKeys.SNOWY_BEACH,
                BiomeKeys.SNOWY_PLAINS
        ));

        FERNLING_VARIANT_BIOMES.put(FernlingVariant.SWAMP, Set.of(
                BiomeKeys.SWAMP,
                BiomeKeys.MANGROVE_SWAMP
        ));
    }

    public static void addSpawns() {
        // --- MUSHLING per-variant biome spawns ---
        for (Map.Entry<MushlingVariant, Set<RegistryKey<Biome>>> e : VARIANT_BIOMES.entrySet()) {
            if (e.getValue().isEmpty()) continue;

            @SuppressWarnings("unchecked")
            RegistryKey<Biome>[] keys = e.getValue().toArray(new RegistryKey[0]);

            SpawnGroup group;
            int weight;
            int min;
            int max;

            switch (e.getKey()) {
                case CRIMSON -> {
                    group = SpawnGroup.MONSTER;
                    weight = 15;
                    min = 1;
                    max = 2;
                }
                case WARPED -> {
                    group = SpawnGroup.MONSTER;
                    weight = 10;
                    min = 1;
                    max = 2;
                }
                case LUSH -> {
                    group = SpawnGroup.CREATURE;
                    weight = 160;
                    min = 2;
                    max = 5;
                }
                default -> {
                    group = SpawnGroup.CREATURE;
                    weight = 35;
                    min = 1;
                    max = 3;
                }
            }

            BiomeModifications.addSpawn(
                    BiomeSelectors.includeByKey(keys),
                    group,
                    ModEntities.MUSHLING,
                    weight,
                    min,
                    max
            );
        }

        // --- MUSHLING spawn restriction ---
        SpawnRestriction.register(
                ModEntities.MUSHLING,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                (type, world, reason, pos, random) -> {
                    RegistryEntry<Biome> entry = world.getBiome(pos);

                    if (entry.matchesKey(BiomeKeys.CRIMSON_FOREST)) {
                        return world.getBlockState(pos.down()).isOf(Blocks.CRIMSON_NYLIUM);
                    }
                    if (entry.matchesKey(BiomeKeys.WARPED_FOREST)) {
                        return world.getBlockState(pos.down()).isOf(Blocks.WARPED_NYLIUM);
                    }

                    if (entry.matchesKey(BiomeKeys.LUSH_CAVES)) {
                        Block blockBelow = world.getBlockState(pos.down()).getBlock();
                        boolean validBlock = blockBelow == Blocks.CLAY
                                || blockBelow == Blocks.GRASS_BLOCK
                                || blockBelow == Blocks.MOSS_BLOCK
                                || blockBelow == Blocks.MOSS_CARPET
                                || blockBelow == Blocks.AZALEA
                                || blockBelow == Blocks.FLOWERING_AZALEA
                                || blockBelow == Blocks.ROOTED_DIRT
                                || blockBelow == Blocks.STONE
                                || blockBelow == Blocks.DEEPSLATE;
                        boolean inWater = world.getBlockState(pos).isOf(Blocks.WATER);

                        return (world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) || inWater || validBlock;
                    }

                    return AnimalEntity.isValidNaturalSpawn(type, world, reason, pos, random);
                }
        );

        // =========================
        //  MYSTICAL ELK
        // =========================
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST),
                SpawnGroup.CREATURE,
                ModEntities.MYSTICAL_ELK,
                30, 1, 2
        );

        SpawnRestriction.register(
                ModEntities.MYSTICAL_ELK,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                AnimalEntity::isValidNaturalSpawn
        );

        // =========================
        //  FERNLING per-variant biome spawns
        // =========================
        for (Map.Entry<FernlingVariant, Set<RegistryKey<Biome>>> e : FERNLING_VARIANT_BIOMES.entrySet()) {
            if (e.getValue().isEmpty()) continue;

            @SuppressWarnings("unchecked")
            RegistryKey<Biome>[] keys = e.getValue().toArray(new RegistryKey[0]);

            BiomeModifications.addSpawn(
                    BiomeSelectors.includeByKey(keys),
                    SpawnGroup.CREATURE,
                    ModEntities.FERNLING,
                    50,
                    1,
                    2
            );
        }

        SpawnRestriction.register(
                ModEntities.FERNLING,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                AnimalEntity::isValidNaturalSpawn
        );
    }

    public static Optional<MushlingVariant> pickVariantForBiome(ServerWorldAccess world, BlockPos pos) {
        RegistryEntry<Biome> entry = world.getBiome(pos);
        Optional<RegistryKey<Biome>> keyOpt = entry.getKey();
        if (keyOpt.isEmpty()) return Optional.empty();
        RegistryKey<Biome> key = keyOpt.get();

        for (MushlingVariant variant : MushlingVariant.values()) {
            Set<RegistryKey<Biome>> biomes = VARIANT_BIOMES.getOrDefault(variant, Collections.emptySet());
            if (biomes.contains(key)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }

    public static Optional<FernlingVariant> pickFernlingVariantForBiome(ServerWorldAccess world, BlockPos pos) {
        RegistryEntry<Biome> entry = world.getBiome(pos);
        Optional<RegistryKey<Biome>> keyOpt = entry.getKey();
        if (keyOpt.isEmpty()) return Optional.empty();
        RegistryKey<Biome> key = keyOpt.get();

        for (FernlingVariant variant : FernlingVariant.values()) {
            Set<RegistryKey<Biome>> biomes = FERNLING_VARIANT_BIOMES.getOrDefault(variant, Collections.emptySet());
            if (biomes.contains(key)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}
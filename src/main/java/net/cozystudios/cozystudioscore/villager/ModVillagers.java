package net.cozystudios.cozystudioscore.villager;

import com.google.common.collect.ImmutableSet;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModVillagers {
    public static final RegistryKey<PointOfInterestType> SAPLING_POI_KEY = registerPoiKey();
    public static final PointOfInterestType SAPLING_POI = registerPoi();

    public static final VillagerProfession ARBORIST = registerProfession();

    private static VillagerProfession registerProfession() {
        return Registry.register(
                Registries.VILLAGER_PROFESSION,
                new Identifier(CozyStudiosCore.MOD_ID, "arborist"),
                new VillagerProfession(
                        "arborist",
                        entry -> entry.matchesKey(ModVillagers.SAPLING_POI_KEY),
                        entry -> entry.matchesKey(ModVillagers.SAPLING_POI_KEY),
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.ENTITY_VILLAGER_WORK_FARMER
                )
        );
    }

    private static PointOfInterestType registerPoi() {
        return PointOfInterestHelper.register(
                new Identifier(CozyStudiosCore.MOD_ID, "saplingpoi"),
                1, 12, // ticket count, search distance
                ModBlocks.ARBORIST_TABLE
        );
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey() {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(CozyStudiosCore.MOD_ID, "saplingpoi"));
    }

    public static void registerVillagers() {
        CozyStudiosCore.LOGGER.info("Registering Villagers for {}", CozyStudiosCore.MOD_ID);
        CozyStudiosCore.LOGGER.info("POI id = {}", Registries.POINT_OF_INTEREST_TYPE.getId(SAPLING_POI));
        CozyStudiosCore.LOGGER.info("Profession id = {}", Registries.VILLAGER_PROFESSION.getId(ARBORIST));
    }
}
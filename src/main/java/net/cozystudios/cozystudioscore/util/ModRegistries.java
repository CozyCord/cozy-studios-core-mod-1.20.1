package net.cozystudios.cozystudioscore.util;

import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.entity.custom.FernlingEntity;
import net.cozystudios.cozystudioscore.entity.custom.MushlingEntity;
import net.cozystudios.cozystudioscore.entity.custom.MysticalElkEntity;
import net.cozystudios.cozystudioscore.villager.ModVillagers;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.VillagerProfession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ModRegistries {
    public static void registerModStuffs() {
        registerAttributes();
        registerCustomTrades();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.MUSHLING, MushlingEntity.createMushlingAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.FERNLING, FernlingEntity.createFernlingAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.MYSTICAL_ELK, MysticalElkEntity.createMysticalElkAttributes());
    }

    private static final int PRICE_EMERALDS = 1;
    private static final int OUT_COUNT = 2;
    private static final int MAX_USES = 6;
    private static final int VILLAGER_XP = 6;
    private static final float PRICE_MULT = 0.08f;

    private static final int NOVICE_COUNT = 3;
    private static final int APPRENTICE_COUNT = 3;
    private static final int JOURNEYMAN_COUNT = 2;
    private static final int EXPERT_COUNT = 2;
    private static final int MASTER_COUNT = 2;

    private static final long SHUFFLE_SALT = 0xC0FFEEBEEFL;

    private static void registerCustomTrades() {
        registerSaplingLevel(ModVillagers.ARBORIST, 1, NOVICE_COUNT);
        registerSaplingLevel(ModVillagers.ARBORIST, 2, APPRENTICE_COUNT);
        registerSaplingLevel(ModVillagers.ARBORIST, 3, JOURNEYMAN_COUNT);
        registerSaplingLevel(ModVillagers.ARBORIST, 4, EXPERT_COUNT);
        registerSaplingLevel(ModVillagers.ARBORIST, 5, MASTER_COUNT);

        TradeOfferHelper.registerVillagerOffers(ModVillagers.ARBORIST, 5, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ModItems.COZY_CRUMBS, 3),
                    8,
                    30,
                    0.05f
            ));
        });
    }

    private static void registerSaplingLevel(VillagerProfession profession, int level, int countForLevel) {
        TradeOfferHelper.registerVillagerOffers(profession, level, factories -> {
            for (int i = 0; i < countForLevel; i++) {
                final int indexWithinLevel = i;
                factories.add((entity, random) -> {
                    var optList = Registries.ITEM.getEntryList(net.minecraft.registry.tag.ItemTags.SAPLINGS);
                    List<RegistryEntry<Item>> entries = optList.map(l -> l.stream().toList()).orElse(List.of());
                    if (entries.isEmpty()) {
                        return null;
                    }

                    int baseOffset = cumulativeOffsetForLevel(level);
                    Item chosen = pickStableUnique(entries, entity.getUuid(), baseOffset + indexWithinLevel);
                    if (chosen == null) {
                        return null;
                    }

                    return new TradeOffer(
                            new ItemStack(Items.EMERALD, PRICE_EMERALDS),
                            new ItemStack(chosen, OUT_COUNT),
                            MAX_USES,
                            VILLAGER_XP,
                            PRICE_MULT
                    );
                });
            }
        });
    }

    private static int cumulativeOffsetForLevel(int level) {
        return switch (level) {
            case 2 -> NOVICE_COUNT;
            case 3 -> NOVICE_COUNT + APPRENTICE_COUNT;
            case 4 -> NOVICE_COUNT + APPRENTICE_COUNT + JOURNEYMAN_COUNT;
            case 5 -> NOVICE_COUNT + APPRENTICE_COUNT + JOURNEYMAN_COUNT + EXPERT_COUNT;
            default -> 0;
        };
    }

    private static Item pickStableUnique(List<RegistryEntry<Item>> entries, UUID villagerUuid, int globalIndex) {
        int size = entries.size();
        if (globalIndex >= size) return null;

        List<Integer> order = new ArrayList<>(size);
        for (int i = 0; i < size; i++) order.add(i);

        long seed = villagerUuid.getMostSignificantBits() ^ villagerUuid.getLeastSignificantBits() ^ SHUFFLE_SALT;
        Collections.shuffle(order, new Random(seed));

        int pickedIdx = order.get(globalIndex);
        return entries.get(pickedIdx).value();
    }
}
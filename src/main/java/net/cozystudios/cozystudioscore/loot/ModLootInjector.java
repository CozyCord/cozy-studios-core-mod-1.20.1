package net.cozystudios.cozystudioscore.loot;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public class ModLootInjector {

    private static final Set<Identifier> MUSIC_DISC_LOOT_TABLES = Set.of(
            new Identifier("minecraft", "chests/abandoned_mineshaft"),
            new Identifier("minecraft", "chests/buried_treasure"),
            new Identifier("minecraft", "chests/desert_pyramid"),
            new Identifier("minecraft", "chests/end_city_treasure"),
            new Identifier("minecraft", "chests/igloo_chest"),
            new Identifier("minecraft", "chests/jungle_temple"),
            new Identifier("minecraft", "chests/jungle_temple_dispenser"),
            new Identifier("minecraft", "chests/nether_bridge"),
            new Identifier("minecraft", "chests/pillager_outpost"),
            new Identifier("minecraft", "chests/shipwreck_map"),
            new Identifier("minecraft", "chests/shipwreck_supply"),
            new Identifier("minecraft", "chests/shipwreck_treasure"),
            new Identifier("minecraft", "chests/simple_dungeon"),
            new Identifier("minecraft", "chests/spawn_bonus_chest"),
            new Identifier("minecraft", "chests/stronghold_corridor"),
            new Identifier("minecraft", "chests/stronghold_crossing"),
            new Identifier("minecraft", "chests/stronghold_library"),
            new Identifier("minecraft", "chests/underwater_ruin_big"),
            new Identifier("minecraft", "chests/underwater_ruin_small"),
            new Identifier("minecraft", "chests/village/village_armorer"),
            new Identifier("minecraft", "chests/village/village_butcher"),
            new Identifier("minecraft", "chests/village/village_cartographer"),
            new Identifier("minecraft", "chests/village/village_desert_house"),
            new Identifier("minecraft", "chests/village/village_fisher"),
            new Identifier("minecraft", "chests/village/village_fletcher"),
            new Identifier("minecraft", "chests/village/village_mason"),
            new Identifier("minecraft", "chests/village/village_plains_house"),
            new Identifier("minecraft", "chests/village/village_savanna_house"),
            new Identifier("minecraft", "chests/village/village_shepherd"),
            new Identifier("minecraft", "chests/village/village_snowy_house"),
            new Identifier("minecraft", "chests/village/village_taiga_house"),
            new Identifier("minecraft", "chests/village/village_tannery"),
            new Identifier("minecraft", "chests/village/village_temple"),
            new Identifier("minecraft", "chests/village/village_toolsmith"),
            new Identifier("minecraft", "chests/village/village_weaponsmith"),
            new Identifier("minecraft", "chests/woodland_mansion")
    );

    private static final Identifier JUNGLE_TEMPLE =
            new Identifier("minecraft", "chests/jungle_temple");

    private static final Identifier HEXALIA_ACCESSORY_TABLE =
            new Identifier("hexalia", "accessories/accessory");

    private static final Map<Identifier, Float> BISECT_COOKIE_INJECTIONS = Map.ofEntries(
            Map.entry(new Identifier("minecraft", "chests/village/village_butcher"),            0.12f),
            Map.entry(new Identifier("minecraft", "chests/village/village_plains_house"),       0.08f),
            Map.entry(new Identifier("minecraft", "chests/village/village_desert_house"),       0.08f),
            Map.entry(new Identifier("minecraft", "chests/village/village_savanna_house"),      0.08f),
            Map.entry(new Identifier("minecraft", "chests/village/village_snowy_house"),        0.08f),
            Map.entry(new Identifier("minecraft", "chests/village/village_taiga_house"),        0.08f),
            Map.entry(new Identifier("minecraft", "chests/woodland_mansion"),                   0.08f),
            Map.entry(new Identifier("minecraft", "chests/desert_pyramid"),                     0.05f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_butcher"),            0.12f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_plains_house"),       0.08f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_desert_house"),       0.08f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_savanna_house"),      0.08f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_snowy_house"),        0.08f),
            Map.entry(new Identifier("farmersdelight", "chests/fd_village_taiga_house"),        0.08f)
    );

    private static final Map<Identifier, Float> HEXALIA_ACCESSORY_INJECTIONS = Map.ofEntries(
            Map.entry(new Identifier("minecraft", "chests/simple_dungeon"),       0.15f),
            Map.entry(new Identifier("minecraft", "chests/abandoned_mineshaft"),  0.10f),
            Map.entry(new Identifier("minecraft", "chests/stronghold_corridor"),  0.12f),
            Map.entry(new Identifier("minecraft", "chests/stronghold_crossing"),  0.15f),
            Map.entry(new Identifier("minecraft", "chests/stronghold_library"),   0.18f),
            Map.entry(new Identifier("minecraft", "chests/ancient_city"),         0.25f),
            Map.entry(new Identifier("minecraft", "chests/ruined_portal"),        0.10f),
            Map.entry(new Identifier("minecraft", "chests/woodland_mansion"),     0.22f),
            Map.entry(new Identifier("minecraft", "chests/jungle_temple"),        0.15f),
            Map.entry(new Identifier("minecraft", "chests/desert_pyramid"),       0.15f)
    );

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

            if (id.equals(JUNGLE_TEMPLE)) {
                LootPool.Builder jungleHornPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.JUNGLE_HORN).weight(5));

                tableBuilder.pool(jungleHornPool.build());

                if (FabricLoader.getInstance().isModLoaded("hexalia")) {
                    var ancientSeed = Registries.ITEM.get(new Identifier("hexalia", "ancient_seed"));
                    if (ancientSeed != null && ancientSeed != net.minecraft.item.Items.AIR) {
                        LootPool.Builder ancientSeedPool = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.35f))
                                .with(ItemEntry.builder(ancientSeed));
                        tableBuilder.pool(ancientSeedPool.build());
                    }
                }
            }

            Float bisectCookieChance = BISECT_COOKIE_INJECTIONS.get(id);
            if (bisectCookieChance != null) {
                LootPool.Builder bisectCookiePool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(bisectCookieChance))
                        .with(ItemEntry.builder(ModItems.BISECT_COOKIE));
                tableBuilder.pool(bisectCookiePool.build());
            }

            Float accessoryChance = HEXALIA_ACCESSORY_INJECTIONS.get(id);
            if (accessoryChance != null && FabricLoader.getInstance().isModLoaded("hexalia")) {
                LootPool.Builder accessoryPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(accessoryChance))
                        .with(LootTableEntry.builder(HEXALIA_ACCESSORY_TABLE));
                tableBuilder.pool(accessoryPool.build());
            }

            if (MUSIC_DISC_LOOT_TABLES.contains(id)) {
                LootPool.Builder musicDiscPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.10f))
                        .with(ItemEntry.builder(ModItems.AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC).weight(1))
                        .with(ItemEntry.builder(ModItems.DAYSPRING_MUSIC_DISC).weight(1));

                tableBuilder.pool(musicDiscPool.build());
            }
        });
    }
}
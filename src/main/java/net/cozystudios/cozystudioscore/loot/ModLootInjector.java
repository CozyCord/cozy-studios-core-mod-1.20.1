package net.cozystudios.cozystudioscore.loot;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

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

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

            if (id.equals(JUNGLE_TEMPLE)) {
                LootPool.Builder jungleHornPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.JUNGLE_HORN).weight(5));

                tableBuilder.pool(jungleHornPool.build());
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
package net.cozystudios.cozystudioscore.loot;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootInjector {

    public static void register() {
        CozyStudiosCore.LOGGER.info("Registering loot table injections for " + CozyStudiosCore.MOD_ID);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.equals(new Identifier("minecraft", "chests/jungle_temple"))) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.JUNGLE_HORN).weight(5));

                tableBuilder.pool(pool.build());
                CozyStudiosCore.LOGGER.info("Added Jungle Horn to jungle temple loot!");
            }
        });
    }
}
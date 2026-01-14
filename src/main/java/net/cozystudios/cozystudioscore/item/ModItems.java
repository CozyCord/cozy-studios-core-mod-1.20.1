package net.cozystudios.cozystudioscore.item;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.item.custom.CozyCrumbsItem;
import net.cozystudios.cozystudioscore.item.custom.GoldenLeafItem;
import net.cozystudios.cozystudioscore.item.custom.JungleHornItem;
import net.cozystudios.cozystudioscore.item.custom.MysticalBerriesItem;
import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {


    public static final Item COZY_CRUMBS = registerItem("cozy_crumbs",
            new CozyCrumbsItem(new FabricItemSettings()));

    public static final Item MYSTICAL_BERRIES = registerItem("mystical_berries",
            new MysticalBerriesItem(new FabricItemSettings()));

    public static final Item GOLDEN_LEAF = registerItem("golden_leaf",
            new GoldenLeafItem(new FabricItemSettings()));

    public static final Item MUSHLING_SPAWN_EGG = registerItem("mushling_spawn_egg",
            new SpawnEggItem(ModEntities.MUSHLING, 0xe61000, 0xf9dad2, new FabricItemSettings()));

    public static final Item FERNLING_SPAWN_EGG = registerItem("fernling_spawn_egg",
            new SpawnEggItem(ModEntities.FERNLING, 0x38D68F, 0x016B36, new FabricItemSettings()));

    public static final Item MYSTICAL_ELK_SPAWN_EGG = registerItem("mystical_elk_spawn_egg",
            new SpawnEggItem(ModEntities.MYSTICAL_ELK, 0xDFF4F5, 0x74F5F7, new FabricItemSettings()));

    public static final Item MYSTICAL_TRADER_SPAWN_EGG = registerItem("mystical_trader_spawn_egg",
            new SpawnEggItem(ModEntities.MYSTICAL_TRADER, 0x283420, 0xc39100, new FabricItemSettings()));

    public static final Item AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC = registerItem("at_original_soundtrack_music_disc",
            new MusicDiscItem(9, ModSounds.AT_ORIGINAL_SOUNDTRACK, new FabricItemSettings().maxCount(1), 139));

    public static final Item DAYSPRING_MUSIC_DISC = registerItem("dayspring_music_disc",
            new MusicDiscItem(9, ModSounds.DAYSPRING, new FabricItemSettings().maxCount(1), 270));

    public static final Item WINDSWEPT_MUSIC_DISC = registerItem("windswept_music_disc",
            new MusicDiscItem(9, ModSounds.WINDSWEPT, new FabricItemSettings().maxCount(1), 180));

    public static final Item HOMEWARD_MUSIC_DISC = registerItem("homeward_music_disc",
            new MusicDiscItem(9, ModSounds.HOMEWARD, new FabricItemSettings().maxCount(1), 157));

    public static final Item JUNGLE_HORN = registerItem("jungle_horn",
            new JungleHornItem(new FabricItemSettings().maxCount(1)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name), item);
    }

    public static void registerModItems() {
    }
}
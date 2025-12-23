package net.cozystudios.cozystudioscore.item;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
@SuppressWarnings("unused")
public class ModItemGroups {
    public static final ItemGroup CORE_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(CozyStudiosCore.MOD_ID, "core"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.core"))
                    .icon(() -> new ItemStack(ModItems.GOLDEN_LEAF)).entries((displayContext, entries) -> {
                        entries.add(ModItems.COZY_CRUMBS);
                        entries.add(ModItems.GOLDEN_LEAF);
                        entries.add(ModItems.MYSTICAL_BERRIES);
                        entries.add(ModItems.JUNGLE_HORN);

                        entries.add(ModBlocks.TRANQUIL_LANTERN);
                        entries.add(ModBlocks.KILN);
                        entries.add(ModBlocks.ARBORIST_TABLE);

                        entries.add(ModItems.MUSHLING_SPAWN_EGG);
                        entries.add(ModItems.FERNLING_SPAWN_EGG);
                        entries.add(ModItems.MYSTICAL_ELK_SPAWN_EGG);
                        entries.add(ModItems.MYSTICAL_TRADER_SPAWN_EGG);
                        entries.add(ModItems.AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC);
                        entries.add(ModItems.DAYSPRING_MUSIC_DISC);




                    }).build());

    public static void registerItemGroups() {
    }
}

package net.cozystudios.cozystudioscore.tag;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static final TagKey<Item> KILN_INPUTS =
            TagKey.of(RegistryKeys.ITEM, new Identifier(CozyStudiosCore.MOD_ID, "kiln_inputs"));
}
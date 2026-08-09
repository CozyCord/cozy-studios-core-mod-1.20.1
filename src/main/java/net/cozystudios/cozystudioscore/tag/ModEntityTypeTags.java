package net.cozystudios.cozystudioscore.tag;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> TRANQUIL_LANTERN_IMMUNE =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern_immune"));
}

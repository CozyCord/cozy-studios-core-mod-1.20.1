package net.cozystudios.cozystudioscore.tag;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {
    public static final TagKey<Block> CROP_FARMLAND =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(CozyStudiosCore.MOD_ID, "crop_farmland"));
}

package net.cozystudios.cozystudioscore.datagen;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    private static RegistryKey<Block> key(Block block) {
        return Registries.BLOCK.getKey(block).orElseThrow(() ->
                new IllegalStateException("Block not registered: " + block));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        // --- Kiln Smeltable Blocks ---
        getOrCreateTagBuilder(ModTags.Blocks.KILN_SMELTABLE_BLOCKS)
                .add(
                        key(Blocks.COBBLESTONE),
                        key(Blocks.STONE),
                        key(Blocks.STONE_BRICKS),
                        key(Blocks.INFESTED_STONE_BRICKS),
                        key(Blocks.SAND),
                        key(Blocks.RED_SAND),
                        key(Blocks.CLAY),
                        key(Blocks.TERRACOTTA),
                        key(Blocks.WHITE_TERRACOTTA),
                        key(Blocks.ORANGE_TERRACOTTA),
                        key(Blocks.MAGENTA_TERRACOTTA),
                        key(Blocks.LIGHT_BLUE_TERRACOTTA),
                        key(Blocks.YELLOW_TERRACOTTA),
                        key(Blocks.LIME_TERRACOTTA),
                        key(Blocks.PINK_TERRACOTTA),
                        key(Blocks.GRAY_TERRACOTTA),
                        key(Blocks.LIGHT_GRAY_TERRACOTTA),
                        key(Blocks.CYAN_TERRACOTTA),
                        key(Blocks.PURPLE_TERRACOTTA),
                        key(Blocks.BLUE_TERRACOTTA),
                        key(Blocks.BROWN_TERRACOTTA),
                        key(Blocks.GREEN_TERRACOTTA),
                        key(Blocks.RED_TERRACOTTA),
                        key(Blocks.BLACK_TERRACOTTA),
                        key(Blocks.NETHERRACK),
                        key(Blocks.WET_SPONGE),
                        key(Blocks.COBBLED_DEEPSLATE),
                        key(Blocks.BLACKSTONE),
                        key(Blocks.SMOOTH_BASALT)
                );

        // --- Pickaxe Mineable ---
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(
                        key(ModBlocks.KILN),
                        key(ModBlocks.TRANQUIL_LANTERN));

        // --- Needs Stone Tool ---
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        key(ModBlocks.KILN),
                        key(ModBlocks.TRANQUIL_LANTERN)
                );
    }
}
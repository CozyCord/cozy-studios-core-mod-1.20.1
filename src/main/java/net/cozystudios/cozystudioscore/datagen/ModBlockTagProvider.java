package net.cozystudios.cozystudioscore.datagen;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
                        key(Blocks.SMOOTH_BASALT),
                        key(Blocks.CACTUS),
                        key(Blocks.COAL_ORE),
                        key(Blocks.COPPER_ORE),
                        key(Blocks.DEEPSLATE_COAL_ORE),
                        key(Blocks.DEEPSLATE_COPPER_ORE),
                        key(Blocks.DEEPSLATE_DIAMOND_ORE),
                        key(Blocks.DEEPSLATE_EMERALD_ORE),
                        key(Blocks.DEEPSLATE_GOLD_ORE),
                        key(Blocks.DEEPSLATE_IRON_ORE),
                        key(Blocks.DEEPSLATE_LAPIS_ORE),
                        key(Blocks.DEEPSLATE_REDSTONE_ORE),
                        key(Blocks.DIAMOND_ORE),
                        key(Blocks.EMERALD_ORE),
                        key(Blocks.GOLD_ORE),
                        key(Blocks.IRON_ORE),
                        key(Blocks.LAPIS_ORE),
                        key(Blocks.REDSTONE_ORE),
                        key(Blocks.NETHER_GOLD_ORE),
                        key(Blocks.NETHER_QUARTZ_ORE),
                        key(Blocks.KELP),
                        key(Blocks.DEEPSLATE_BRICKS),
                        key(Blocks.DEEPSLATE_TILES),
                        key(Blocks.SANDSTONE),
                        key(Blocks.RED_SANDSTONE),
                        key(Blocks.NETHER_BRICKS),
                        key(Blocks.BASALT),
                        key(Blocks.POLISHED_BLACKSTONE_BRICKS),
                        key(Blocks.QUARTZ_BLOCK),
                        key(Blocks.SEA_PICKLE)
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
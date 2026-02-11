package net.cozystudios.cozystudioscore.datagen;

import java.util.function.Consumer;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // Tranquil Lantern upgrades
        generateTranquilLanternRecipes(exporter);

        // Base Tranquil Lantern
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.TRANQUIL_LANTERN, 1)
                .pattern("LLL")
                .pattern("LGL")
                .pattern("LLL")
                .input('L', Items.LANTERN)
                .input('G', ModItems.GOLDEN_LEAF)
                .criterion(hasItem(ModItems.GOLDEN_LEAF), conditionsFromItem(ModItems.GOLDEN_LEAF))
                .group("tranquil_lantern")
                .offerTo(exporter);

        // Kiln
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.KILN, 1)
                .pattern("BBB")
                .pattern("BFB")
                .pattern("CCC")
                .input('B', Items.MUD_BRICKS)
                .input('F', Items.BLAST_FURNACE)
                .input('C', Items.COBBLESTONE)
                .criterion(hasItem(Items.BLAST_FURNACE), conditionsFromItem(Items.BLAST_FURNACE))
                .group("kiln")
                .offerTo(exporter);

        // Arborist Table
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.ARBORIST_TABLE, 1)
                .pattern("OHO")
                .pattern("SCS")
                .pattern("BBB")
                .input('O', Items.ORANGE_WOOL)
                .input('H', Items.IRON_SHOVEL)
                .input('C', Items.CRAFTING_TABLE)
                .input('B', Items.BIRCH_PLANKS)
                .input('S', ItemTags.SAPLINGS)
                .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.CRAFTING_TABLE))
                .group("arborist_table")
                .offerTo(exporter);

        // Crafted items
        generateCraftedItemRecipes(exporter);

        // Stonecutting recipes
        generateStonecuttingRecipes(exporter);

        // Smelting/Blasting/Smoking recipes
        generateCookingRecipes(exporter);

        // Concrete powder recipes
        generateConcretePowderRecipes(exporter);

        // Deepslate stonecutting
        generateDeepslateStonecuttingRecipes(exporter);

        // Wool recolor recipes
        generateWoolRecolorRecipes(exporter);

        // Banner recolor recipes
        generateBannerRecolorRecipes(exporter);

        // Peaceful Mob Drops
        generatePeacefulMobDropRecipes(exporter);
    }

    private void generateTranquilLanternRecipes(Consumer<RecipeJsonProvider> exporter) {
        // Golden Tranquil Lantern - Tranquil Lantern surrounded by Gold Blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.GOLDEN_TRANQUIL_LANTERN, 1)
                .pattern("GGG")
                .pattern("GTG")
                .pattern("GGG")
                .input('G', Items.GOLD_BLOCK)
                .input('T', ModBlocks.TRANQUIL_LANTERN)
                .criterion(hasItem(ModBlocks.TRANQUIL_LANTERN), conditionsFromItem(ModBlocks.TRANQUIL_LANTERN))
                .offerTo(exporter, new Identifier("cozystudioscore", "golden_tranquil_lantern"));

        // Diamond Tranquil Lantern - Golden Tranquil Lantern surrounded by Diamond Blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.DIAMOND_TRANQUIL_LANTERN, 1)
                .pattern("DDD")
                .pattern("DTD")
                .pattern("DDD")
                .input('D', Items.DIAMOND_BLOCK)
                .input('T', ModBlocks.GOLDEN_TRANQUIL_LANTERN)
                .criterion(hasItem(ModBlocks.GOLDEN_TRANQUIL_LANTERN), conditionsFromItem(ModBlocks.GOLDEN_TRANQUIL_LANTERN))
                .offerTo(exporter, new Identifier("cozystudioscore", "diamond_tranquil_lantern"));

        // Netherite Tranquil Lantern - Diamond Tranquil Lantern surrounded by Netherite Ingots
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.NETHERITE_TRANQUIL_LANTERN, 1)
                .pattern("NNN")
                .pattern("NTN")
                .pattern("NNN")
                .input('N', Items.NETHERITE_INGOT)
                .input('T', ModBlocks.DIAMOND_TRANQUIL_LANTERN)
                .criterion(hasItem(ModBlocks.DIAMOND_TRANQUIL_LANTERN), conditionsFromItem(ModBlocks.DIAMOND_TRANQUIL_LANTERN))
                .offerTo(exporter, new Identifier("cozystudioscore", "netherite_tranquil_lantern"));
    }

    private void generateCraftedItemRecipes(Consumer<RecipeJsonProvider> exporter) {
        // Bell
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.BELL, 1)
                .pattern("SSS")
                .pattern(" I ")
                .pattern("INI")
                .input('S', Items.STICK)
                .input('I', Items.GOLD_INGOT)
                .input('N', Items.GOLD_NUGGET)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, new Identifier("cozystudioscore", "bell"));

        // Saddle
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.SADDLE, 1)
                .pattern("LLL")
                .pattern(" S ")
                .pattern(" I ")
                .input('L', Items.LEATHER)
                .input('S', Items.STRING)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                .offerTo(exporter, new Identifier("cozystudioscore", "saddle"));

        // Name Tag
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.NAME_TAG, 1)
                .pattern("S")
                .pattern("I")
                .pattern("N")
                .input('S', Items.STRING)
                .input('I', Items.IRON_INGOT)
                .input('N', Items.INK_SAC)
                .criterion(hasItem(Items.INK_SAC), conditionsFromItem(Items.INK_SAC))
                .offerTo(exporter, new Identifier("cozystudioscore", "name_tag"));

        // Horse Armors
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.IRON_HORSE_ARMOR, 1)
                .pattern("  I")
                .pattern("ILI")
                .pattern("I I")
                .input('I', Items.IRON_INGOT)
                .input('L', Items.LEATHER)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, new Identifier("cozystudioscore", "iron_horse_armor"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.GOLDEN_HORSE_ARMOR, 1)
                .pattern("  G")
                .pattern("GLG")
                .pattern("G G")
                .input('G', Items.GOLD_INGOT)
                .input('L', Items.LEATHER)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, new Identifier("cozystudioscore", "gold_horse_armor"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.DIAMOND_HORSE_ARMOR, 1)
                .pattern("  D")
                .pattern("DLD")
                .pattern("D D")
                .input('D', Items.DIAMOND)
                .input('L', Items.LEATHER)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, new Identifier("cozystudioscore", "diamond_horse_armor"));

        // Mushroom blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.BROWN_MUSHROOM_BLOCK, 1)
                .pattern("MM")
                .pattern("MM")
                .input('M', Items.BROWN_MUSHROOM)
                .criterion(hasItem(Items.BROWN_MUSHROOM), conditionsFromItem(Items.BROWN_MUSHROOM))
                .offerTo(exporter, new Identifier("cozystudioscore", "brown_mushroom_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.RED_MUSHROOM_BLOCK, 1)
                .pattern("MM")
                .pattern("MM")
                .input('M', Items.RED_MUSHROOM)
                .criterion(hasItem(Items.RED_MUSHROOM), conditionsFromItem(Items.RED_MUSHROOM))
                .offerTo(exporter, new Identifier("cozystudioscore", "red_mushroom_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.MUSHROOM_STEM, 1)
                .pattern("RB")
                .pattern("BR")
                .input('R', Items.RED_MUSHROOM)
                .input('B', Items.BROWN_MUSHROOM)
                .criterion(hasItem(Items.BROWN_MUSHROOM), conditionsFromItem(Items.BROWN_MUSHROOM))
                .offerTo(exporter, new Identifier("cozystudioscore", "mushroom_stem"));

        // Other items
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.SHROOMLIGHT, 1)
                .input(Items.MUSHROOM_STEM)
                .input(Items.GLOWSTONE)
                .criterion(hasItem(Items.MUSHROOM_STEM), conditionsFromItem(Items.MUSHROOM_STEM))
                .offerTo(exporter, new Identifier("cozystudioscore", "shroomlight"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.COBWEB, 1)
                .pattern("S S")
                .pattern(" S ")
                .pattern("S S")
                .input('S', Items.STRING)
                .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                .offerTo(exporter, new Identifier("cozystudioscore", "cobweb"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING, 1)
                .input(Ingredient.fromTag(ItemTags.WOOL))
                .criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
                .offerTo(exporter, new Identifier("cozystudioscore", "string"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.GLOW_LICHEN, 1)
                .input(Items.GLOWSTONE_DUST)
                .input(Items.VINE)
                .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                .offerTo(exporter, new Identifier("cozystudioscore", "glow_lichen"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GLOW_INK_SAC, 1)
                .input(Items.GLOWSTONE_DUST)
                .input(Items.INK_SAC)
                .criterion(hasItem(Items.INK_SAC), conditionsFromItem(Items.INK_SAC))
                .offerTo(exporter, new Identifier("cozystudioscore", "glow_ink_sac"));

        // Shapeless recipes
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PAPER, 3)
                .input(Items.SUGAR_CANE)
                .input(Items.SUGAR_CANE)
                .input(Items.SUGAR_CANE)
                .criterion(hasItem(Items.SUGAR_CANE), conditionsFromItem(Items.SUGAR_CANE))
                .offerTo(exporter, new Identifier("cozystudioscore", "shapeless_paper"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.BREAD, 1)
                .input(Items.WHEAT)
                .input(Items.WHEAT)
                .input(Items.WHEAT)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter, new Identifier("cozystudioscore", "shapless_bread"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.COZY_CRUMBS, 3)
                .input(Items.RED_MUSHROOM)
                .input(Items.HONEY_BOTTLE)
                .input(Items.WHEAT)
                .criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
                .offerTo(exporter, new Identifier("cozystudioscore", "cozy_crumbs"));

        // Abacus
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.ABACUS, 1)
                .pattern(" I ")
                .pattern("PSP")
                .pattern("PSP")
                .input('I', Items.IRON_INGOT)
                .input('P', ItemTags.PLANKS)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .group("abacus")
                .offerTo(exporter, new Identifier("cozystudioscore", "abacus"));
    }

    private void generateStonecuttingRecipes(Consumer<RecipeJsonProvider> exporter) {
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.GRAVEL, Items.COBBLESTONE, 1);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.GRAVEL, Items.STONE, 1);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.COBBLESTONE, Items.STONE, 1);
        offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Items.POINTED_DRIPSTONE, Items.DRIPSTONE_BLOCK, 4);
    }

    private void generateCookingRecipes(Consumer<RecipeJsonProvider> exporter) {
        // Leather from rotten flesh
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.ROTTEN_FLESH), RecipeCategory.MISC,
                Items.LEATHER, 0.35f, 200)
                .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
                .offerTo(exporter, new Identifier("cozystudioscore", "leather_from_smelting_rotten_flesh"));

        CookingRecipeJsonBuilder.createSmoking(Ingredient.ofItems(Items.ROTTEN_FLESH), RecipeCategory.MISC,
                Items.LEATHER, 0.35f, 100)
                .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
                .offerTo(exporter, new Identifier("cozystudioscore", "leather_from_smoking_rotten_flesh"));

        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.ROTTEN_FLESH), RecipeCategory.MISC,
                Items.LEATHER, 0.35f, 600)
                .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
                .offerTo(exporter, new Identifier("cozystudioscore", "leather_from_campfire_rotten_flesh"));
    }

    private void generateConcretePowderRecipes(Consumer<RecipeJsonProvider> exporter) {
        String[] colors = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};

        for (String color : colors) {
            Item concrete = getConcreteByColor(color);
            Item powder = getConcretePowderByColor(color);

            // Smelting
            CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(concrete), RecipeCategory.BUILDING_BLOCKS,
                    powder, 0.1f, 200)
                    .criterion(hasItem(concrete), conditionsFromItem(concrete))
                    .offerTo(exporter, new Identifier("cozystudioscore", color + "_concrete_powder_smelting"));

            // Blasting
            CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(concrete), RecipeCategory.BUILDING_BLOCKS,
                    powder, 0.1f, 100)
                    .criterion(hasItem(concrete), conditionsFromItem(concrete))
                    .offerTo(exporter, new Identifier("cozystudioscore", color + "_concrete_powder_blasting"));
        }
    }

    private void generateDeepslateStonecuttingRecipes(Consumer<RecipeJsonProvider> exporter) {
        // From deepslate
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.COBBLED_DEEPSLATE, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/cobbled_deepslate_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.POLISHED_DEEPSLATE, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/polished_deepslate_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.POLISHED_DEEPSLATE_SLAB, Items.DEEPSLATE, 2,
                "deepslate_stonecutting/polished_deepslate_slab_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.POLISHED_DEEPSLATE_STAIRS, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/polished_deepslate_stairs_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.POLISHED_DEEPSLATE_WALL, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/polished_deepslate_wall_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_BRICKS, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_bricks_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_BRICK_SLAB, Items.DEEPSLATE, 2,
                "deepslate_stonecutting/deepslate_brick_slab_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_BRICK_STAIRS, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_brick_stairs_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_BRICK_WALL, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_brick_wall_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_TILES, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_tiles_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_TILE_SLAB, Items.DEEPSLATE, 2,
                "deepslate_stonecutting/deepslate_tile_slab_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_TILE_STAIRS, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_tile_stairs_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.DEEPSLATE_TILE_WALL, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/deepslate_tile_wall_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.CHISELED_DEEPSLATE, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/chiseled_deepslate_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.COBBLED_DEEPSLATE_SLAB, Items.DEEPSLATE, 2,
                "deepslate_stonecutting/cobbled_deepslate_slab_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.COBBLED_DEEPSLATE_STAIRS, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/cobbled_deepslate_stairs_from_deepslate_stonecutting");
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Items.COBBLED_DEEPSLATE_WALL, Items.DEEPSLATE, 1,
                "deepslate_stonecutting/cobbled_deepslate_wall_from_deepslate_stonecutting");
    }

    private void generateWoolRecolorRecipes(Consumer<RecipeJsonProvider> exporter) {
        addWoolRecolor(exporter, Items.WHITE_WOOL, Items.WHITE_DYE);
        addWoolRecolor(exporter, Items.ORANGE_WOOL, Items.ORANGE_DYE);
        addWoolRecolor(exporter, Items.MAGENTA_WOOL, Items.MAGENTA_DYE);
        addWoolRecolor(exporter, Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
        addWoolRecolor(exporter, Items.YELLOW_WOOL, Items.YELLOW_DYE);
        addWoolRecolor(exporter, Items.LIME_WOOL, Items.LIME_DYE);
        addWoolRecolor(exporter, Items.PINK_WOOL, Items.PINK_DYE);
        addWoolRecolor(exporter, Items.GRAY_WOOL, Items.GRAY_DYE);
        addWoolRecolor(exporter, Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
        addWoolRecolor(exporter, Items.CYAN_WOOL, Items.CYAN_DYE);
        addWoolRecolor(exporter, Items.PURPLE_WOOL, Items.PURPLE_DYE);
        addWoolRecolor(exporter, Items.BLUE_WOOL, Items.BLUE_DYE);
        addWoolRecolor(exporter, Items.BROWN_WOOL, Items.BROWN_DYE);
        addWoolRecolor(exporter, Items.GREEN_WOOL, Items.GREEN_DYE);
        addWoolRecolor(exporter, Items.RED_WOOL, Items.RED_DYE);
        addWoolRecolor(exporter, Items.BLACK_WOOL, Items.BLACK_DYE);
    }

    private void generateBannerRecolorRecipes(Consumer<RecipeJsonProvider> exporter) {
        addBannerRecolor(exporter, Items.WHITE_BANNER, Items.WHITE_DYE);
        addBannerRecolor(exporter, Items.ORANGE_BANNER, Items.ORANGE_DYE);
        addBannerRecolor(exporter, Items.MAGENTA_BANNER, Items.MAGENTA_DYE);
        addBannerRecolor(exporter, Items.LIGHT_BLUE_BANNER, Items.LIGHT_BLUE_DYE);
        addBannerRecolor(exporter, Items.YELLOW_BANNER, Items.YELLOW_DYE);
        addBannerRecolor(exporter, Items.LIME_BANNER, Items.LIME_DYE);
        addBannerRecolor(exporter, Items.PINK_BANNER, Items.PINK_DYE);
        addBannerRecolor(exporter, Items.GRAY_BANNER, Items.GRAY_DYE);
        addBannerRecolor(exporter, Items.LIGHT_GRAY_BANNER, Items.LIGHT_GRAY_DYE);
        addBannerRecolor(exporter, Items.CYAN_BANNER, Items.CYAN_DYE);
        addBannerRecolor(exporter, Items.PURPLE_BANNER, Items.PURPLE_DYE);
        addBannerRecolor(exporter, Items.BLUE_BANNER, Items.BLUE_DYE);
        addBannerRecolor(exporter, Items.BROWN_BANNER, Items.BROWN_DYE);
        addBannerRecolor(exporter, Items.GREEN_BANNER, Items.GREEN_DYE);
        addBannerRecolor(exporter, Items.RED_BANNER, Items.RED_DYE);
        addBannerRecolor(exporter, Items.BLACK_BANNER, Items.BLACK_DYE);
    }

    private void addWoolRecolor(Consumer<RecipeJsonProvider> exporter, Item result, Item dye) {
        String color = result.toString().replace("_wool", "");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, result, 1)
                .input(ItemTags.WOOL)
                .input(dye)
                .criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
                .group("cozystudioscore_recolor_wool")
                .offerTo(exporter, new Identifier("cozystudioscore", "recolor/wool_recolor_" + color));
    }

    private void addBannerRecolor(Consumer<RecipeJsonProvider> exporter, Item result, Item dye) {
        String color = result.toString().replace("_banner", "");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, result, 1)
                .input(ItemTags.BANNERS)
                .input(dye)
                .criterion("has_banner", conditionsFromTag(ItemTags.BANNERS))
                .group("cozystudioscore_recolor_banner")
                .offerTo(exporter, new Identifier("cozystudioscore", "recolor/banner_recolor_" + color));
    }

    private Item getConcreteByColor(String color) {
        return switch (color) {
            case "white" -> Items.WHITE_CONCRETE;
            case "orange" -> Items.ORANGE_CONCRETE;
            case "magenta" -> Items.MAGENTA_CONCRETE;
            case "light_blue" -> Items.LIGHT_BLUE_CONCRETE;
            case "yellow" -> Items.YELLOW_CONCRETE;
            case "lime" -> Items.LIME_CONCRETE;
            case "pink" -> Items.PINK_CONCRETE;
            case "gray" -> Items.GRAY_CONCRETE;
            case "light_gray" -> Items.LIGHT_GRAY_CONCRETE;
            case "cyan" -> Items.CYAN_CONCRETE;
            case "purple" -> Items.PURPLE_CONCRETE;
            case "blue" -> Items.BLUE_CONCRETE;
            case "brown" -> Items.BROWN_CONCRETE;
            case "green" -> Items.GREEN_CONCRETE;
            case "red" -> Items.RED_CONCRETE;
            case "black" -> Items.BLACK_CONCRETE;
            default -> Items.WHITE_CONCRETE;
        };
    }

    private Item getConcretePowderByColor(String color) {
        return switch (color) {
            case "white" -> Items.WHITE_CONCRETE_POWDER;
            case "orange" -> Items.ORANGE_CONCRETE_POWDER;
            case "magenta" -> Items.MAGENTA_CONCRETE_POWDER;
            case "light_blue" -> Items.LIGHT_BLUE_CONCRETE_POWDER;
            case "yellow" -> Items.YELLOW_CONCRETE_POWDER;
            case "lime" -> Items.LIME_CONCRETE_POWDER;
            case "pink" -> Items.PINK_CONCRETE_POWDER;
            case "gray" -> Items.GRAY_CONCRETE_POWDER;
            case "light_gray" -> Items.LIGHT_GRAY_CONCRETE_POWDER;
            case "cyan" -> Items.CYAN_CONCRETE_POWDER;
            case "purple" -> Items.PURPLE_CONCRETE_POWDER;
            case "blue" -> Items.BLUE_CONCRETE_POWDER;
            case "brown" -> Items.BROWN_CONCRETE_POWDER;
            case "green" -> Items.GREEN_CONCRETE_POWDER;
            case "red" -> Items.RED_CONCRETE_POWDER;
            case "black" -> Items.BLACK_CONCRETE_POWDER;
            default -> Items.WHITE_CONCRETE_POWDER;
        };
    }

    private void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category,
                                          Item output, Item input, int count) {
        String inputName = input.toString();
        String outputName = output.toString();
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, new Identifier("cozystudioscore", outputName + "_from_" + inputName + "_stonecutting"));
    }

    private void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category,
                                          Item output, Item input, int count, String recipeName) {
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, new Identifier("cozystudioscore", recipeName));
    }

    private void generatePeacefulMobDropRecipes(Consumer<RecipeJsonProvider> exporter) {
        // Beef
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.BEEF, 1)
                .pattern("###")
                .pattern("## ")
                .pattern("###")
                .input('#', Items.WHEAT)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/beef"));

        // Blaze Rod
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BLAZE_ROD, 2)
                .pattern("# #")
                .pattern("#R#")
                .pattern("# #")
                .input('#', Items.QUARTZ)
                .input('R', Items.LAVA_BUCKET)
                .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/blaze_rod"));

        // Bone from Bonemeal
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BONE, 1)
                .pattern("###")
                .input('#', Items.BONE_MEAL)
                .criterion(hasItem(Items.BONE_MEAL), conditionsFromItem(Items.BONE_MEAL))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/bone_from_bonemeal"));

        // Chicken
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.CHICKEN, 1)
                .pattern("###")
                .pattern("#  ")
                .pattern("###")
                .input('#', Items.WHEAT_SEEDS)
                .criterion(hasItem(Items.WHEAT_SEEDS), conditionsFromItem(Items.WHEAT_SEEDS))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/chicken"));

        // Cod
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.COD, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.KELP)
                .input('R', Items.WHITE_DYE)
                .criterion(hasItem(Items.KELP), conditionsFromItem(Items.KELP))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/cod"));

        // Creeper Head
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.CREEPER_HEAD, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.GUNPOWDER)
                .input('R', Items.LIME_DYE)
                .input('L', Items.JACK_O_LANTERN)
                .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/creeper_head"));

        // Dragon Egg
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.DRAGON_EGG, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.CHORUS_FRUIT)
                .input('R', Items.BLACK_DYE)
                .input('L', Items.DIAMOND_BLOCK)
                .criterion(hasItem(Items.CHORUS_FRUIT), conditionsFromItem(Items.CHORUS_FRUIT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/dragon_egg"));

        // Dragon Head
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.DRAGON_HEAD, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.CHORUS_FRUIT)
                .input('R', Items.BLACK_DYE)
                .input('L', Items.JACK_O_LANTERN)
                .criterion(hasItem(Items.CHORUS_FRUIT), conditionsFromItem(Items.CHORUS_FRUIT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/dragon_head"));

        // Ender Pearl
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENDER_PEARL, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.OBSIDIAN)
                .input('R', Items.GOLD_INGOT)
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/ender_pearl"));

        // Ghast Tear
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GHAST_TEAR, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.QUARTZ)
                .input('R', Items.WATER_BUCKET)
                .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/ghast_tear"));

        // Ink Sac
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.INK_SAC, 1)
                .pattern("#R ")
                .input('#', Items.COAL)
                .input('R', Items.WATER_BUCKET)
                .criterion(hasItem(Items.COAL), conditionsFromItem(Items.COAL))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/ink_sac"));

        // Mutton
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.MUTTON, 1)
                .pattern("# #")
                .pattern("###")
                .pattern("# #")
                .input('#', Items.WHEAT)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/mutton"));

        // Nether Star
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.NETHER_STAR, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', Items.WITHER_SKELETON_SKULL)
                .criterion(hasItem(Items.WITHER_SKELETON_SKULL), conditionsFromItem(Items.WITHER_SKELETON_SKULL))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/nether_star"));

        // Porkchop
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.PORKCHOP, 1)
                .pattern("#RL")
                .pattern("#RL")
                .pattern("#RL")
                .input('#', Items.BEETROOT)
                .input('R', Items.CARROT)
                .input('L', Items.POTATO)
                .criterion(hasItem(Items.BEETROOT), conditionsFromItem(Items.BEETROOT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/porkchop"));

        // Prismarine Crystals
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PRISMARINE_CRYSTALS, 1)
                .pattern("#R#")
                .input('#', Items.GLASS)
                .input('R', Items.KELP)
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/prismarine_crystals"));

        // Prismarine Shard
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PRISMARINE_SHARD, 1)
                .pattern("#R#")
                .input('#', Items.GRAVEL)
                .input('R', Items.KELP)
                .criterion(hasItem(Items.GRAVEL), conditionsFromItem(Items.GRAVEL))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/prismarine_shard"));

        // Rabbit
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.RABBIT, 1)
                .pattern("###")
                .pattern("## ")
                .pattern("# #")
                .input('#', Items.CARROT)
                .criterion(hasItem(Items.CARROT), conditionsFromItem(Items.CARROT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rabbit"));

        // Rabbit Foot
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.RABBIT_FOOT, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.CARROT)
                .input('R', Items.RABBIT_HIDE)
                .input('L', Items.RABBIT)
                .criterion(hasItem(Items.RABBIT), conditionsFromItem(Items.RABBIT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rabbit_foot"));

        // Salmon
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.SALMON, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.KELP)
                .input('R', Items.RED_DYE)
                .criterion(hasItem(Items.KELP), conditionsFromItem(Items.KELP))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/salmon"));

        // Shulker Shell
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SHULKER_SHELL, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.END_STONE)
                .input('R', Items.CHORUS_FLOWER)
                .criterion(hasItem(Items.END_STONE), conditionsFromItem(Items.END_STONE))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/shulker_shell"));

        // Skeleton Skull
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.SKELETON_SKULL, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.BONE)
                .input('R', Items.WHITE_DYE)
                .input('L', Items.JACK_O_LANTERN)
                .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/skeleton_skull"));

        // Slime Ball
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SLIME_BALL, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.LILY_PAD)
                .input('R', Items.CACTUS)
                .input('L', Items.WATER_BUCKET)
                .criterion(hasItem(Items.LILY_PAD), conditionsFromItem(Items.LILY_PAD))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/slime"));

        // Spider Eye
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.SPIDER_EYE, 1)
                .pattern("#R")
                .pattern("R#")
                .input('#', Items.BROWN_MUSHROOM)
                .input('R', Items.RED_MUSHROOM)
                .criterion(hasItem(Items.BROWN_MUSHROOM), conditionsFromItem(Items.BROWN_MUSHROOM))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/spider_eye"));

        // Sponge
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.SPONGE, 1)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .input('#', Items.YELLOW_WOOL)
                .criterion(hasItem(Items.YELLOW_WOOL), conditionsFromItem(Items.YELLOW_WOOL))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/sponge"));

        // Trident
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.TRIDENT, 1)
                .pattern("###")
                .pattern("RLR")
                .pattern("RLR")
                .input('#', Items.DIAMOND)
                .input('R', Items.PRISMARINE_SHARD)
                .input('L', Items.PRISMARINE_CRYSTALS)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/trident"));

        // Tropical Fish
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.TROPICAL_FISH, 1)
                .pattern("###")
                .pattern("#R#")
                .pattern("###")
                .input('#', Items.KELP)
                .input('R', Items.ORANGE_DYE)
                .criterion(hasItem(Items.KELP), conditionsFromItem(Items.KELP))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/tropical_fish"));

        // Wither Skeleton Skull
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.WITHER_SKELETON_SKULL, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.CRYING_OBSIDIAN)
                .input('R', Items.GILDED_BLACKSTONE)
                .input('L', Items.DIAMOND)
                .criterion(hasItem(Items.CRYING_OBSIDIAN), conditionsFromItem(Items.CRYING_OBSIDIAN))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/wither_skeleton_skull"));

        // Zombie Head
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.ZOMBIE_HEAD, 1)
                .pattern("#R#")
                .pattern("RLR")
                .pattern("#R#")
                .input('#', Items.ROTTEN_FLESH)
                .input('R', Items.GREEN_DYE)
                .input('L', Items.JACK_O_LANTERN)
                .criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/zombie_head"));

        // Shapeless Recipes

        // Feather from Egg
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FEATHER, 1)
                .input(Items.EGG)
                .criterion(hasItem(Items.EGG), conditionsFromItem(Items.EGG))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/feather_from_egg"));

        // Gunpowder
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GUNPOWDER, 1)
                .input(Items.FLINT)
                .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/gunpowder"));

        // Rabbit Hide
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.RABBIT_HIDE, 4)
                .input(Items.LEATHER)
                .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rabbit_hide"));

        // Rotten Flesh from Beef
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.BEEF)
                .criterion(hasItem(Items.BEEF), conditionsFromItem(Items.BEEF))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_beef"));

        // Rotten Flesh from Chicken
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.CHICKEN)
                .criterion(hasItem(Items.CHICKEN), conditionsFromItem(Items.CHICKEN))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_chicken"));

        // Rotten Flesh from Cod
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.COD)
                .criterion(hasItem(Items.COD), conditionsFromItem(Items.COD))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_cod"));

        // Rotten Flesh from Mutton
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.MUTTON)
                .criterion(hasItem(Items.MUTTON), conditionsFromItem(Items.MUTTON))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_mutton"));

        // Rotten Flesh from Porkchop
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.PORKCHOP)
                .criterion(hasItem(Items.PORKCHOP), conditionsFromItem(Items.PORKCHOP))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_porkchop"));

        // Rotten Flesh from Rabbit
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.RABBIT)
                .criterion(hasItem(Items.RABBIT), conditionsFromItem(Items.RABBIT))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_rabbit"));

        // Rotten Flesh from Salmon
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ROTTEN_FLESH, 1)
                .input(Items.SALMON)
                .criterion(hasItem(Items.SALMON), conditionsFromItem(Items.SALMON))
                .offerTo(exporter, new Identifier("cozystudioscore", "peaceful_mob_drops/rotten_flesh_from_salmon"));

    }
}

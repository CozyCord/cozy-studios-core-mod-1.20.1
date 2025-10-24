package net.cozystudios.cozystudioscore.recipe;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipeTypes {
    public static RecipeType<KilnRecipe> KILN;
    public static RecipeSerializer<KilnRecipe> KILN_SERIALIZER;

    public static void register() {
        KILN = Registry.register(
                Registries.RECIPE_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "kiln"),
                new RecipeType<>() {
                    @Override
                    public String toString() {
                        return "cozystudioscore:kiln";
                    }
                }
        );

        KILN_SERIALIZER = Registry.register(
                Registries.RECIPE_SERIALIZER,
                new Identifier(CozyStudiosCore.MOD_ID, "kiln"),
                new CookingRecipeSerializer<>(
                        KilnRecipe::new,
                        100
                )
        );

        CozyStudiosCore.LOGGER.info("Registered Kiln recipe type & serializer");
    }
}
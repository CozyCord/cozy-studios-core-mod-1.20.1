package net.cozystudios.cozystudioscore.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;

public class KilnRecipe extends AbstractCookingRecipe {
    public KilnRecipe(Identifier id, String group, CookingRecipeCategory category,
                      Ingredient input, ItemStack output, float experience, int cookTime) {
        super(ModRecipeTypes.KILN, id, group, category, input, output, experience, cookTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.KILN_SERIALIZER;
    }
}
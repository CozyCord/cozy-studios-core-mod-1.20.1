package net.cozystudios.cozystudioscore.integration.ftbquests;

import dev.ftb.mods.ftbquests.integration.RecipeModHelper;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.item.ItemStack;

public class JeiRecipeModHelper implements RecipeModHelper {
    @Override
    public boolean isRecipeModAvailable() {
        return JeiRuntimeHolder.get() != null;
    }

    @Override
    public void showRecipes(ItemStack stack) {
        IJeiRuntime runtime = JeiRuntimeHolder.get();
        if (runtime == null || stack == null || stack.isEmpty()) {
            return;
        }
        IJeiHelpers helpers = runtime.getJeiHelpers();
        IFocusFactory focusFactory = helpers.getFocusFactory();
        IFocus<ItemStack> focus = focusFactory.createFocus(
                RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
        IRecipesGui gui = runtime.getRecipesGui();
        gui.show(focus);
    }

    @Override
    public void refreshAll(RecipeModHelper.Components components) {
    }

    @Override
    public void refreshRecipes(QuestObjectBase questObject) {
    }

    @Override
    public String getHelperName() {
        return "JEI";
    }
}

package net.cozystudios.cozystudioscore.screen;

import net.cozystudios.cozystudioscore.recipe.ModRecipeTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;

public class KilnScreenHandler extends AbstractFurnaceScreenHandler {
    public KilnScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.KILN_SCREEN_HANDLER,
                ModRecipeTypes.KILN,
                RecipeBookCategory.FURNACE,
                syncId, playerInventory);
    }

    public KilnScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.KILN_SCREEN_HANDLER,
                ModRecipeTypes.KILN,
                RecipeBookCategory.FURNACE,
                syncId, playerInventory, inventory, propertyDelegate);
    }
}
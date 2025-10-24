package net.cozystudios.cozystudioscore.screen;

import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KilnScreen extends AbstractFurnaceScreen<KilnScreenHandler> {
    private static final Identifier FURNACE_GUI = new Identifier("minecraft", "textures/gui/container/furnace.png");

    public KilnScreen(KilnScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, new FurnaceRecipeBookScreen(), playerInventory, title, FURNACE_GUI);
    }
}
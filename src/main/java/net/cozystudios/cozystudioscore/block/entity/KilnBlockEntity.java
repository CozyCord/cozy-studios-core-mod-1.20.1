package net.cozystudios.cozystudioscore.block.entity;

import net.cozystudios.cozystudioscore.config.KilnConfig;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.recipe.ModRecipeTypes;
import net.cozystudios.cozystudioscore.screen.KilnScreenHandler;
import net.cozystudios.cozystudioscore.tag.ModItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class KilnBlockEntity extends AbstractFurnaceBlockEntity {
    public KilnBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KILN, pos, state, ModRecipeTypes.KILN);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            if (stack.isIn(ModItemTags.KILN_INPUTS)) {
                return true;
            }

            for (String recipe : KilnConfig.get().extraKilnRecipes) {
                String[] parts = recipe.split("->");
                if (parts.length == 2) {
                    String inputId = parts[0].trim();
                    Identifier id = Identifier.tryParse(inputId);
                    if (id != null) {
                        Item inputItem = Registries.ITEM.get(id);
                        if (stack.isOf(inputItem)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return super.isValid(slot, stack);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.cozystudioscore.kiln");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new KilnScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
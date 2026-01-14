package net.cozystudios.cozystudioscore.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiamondTranquilLanternItem extends BlockItem {
    public DiamondTranquilLanternItem(net.minecraft.block.Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("✨ Pushes hostile mobs away within 50 blocks").formatted(Formatting.AQUA));
    }
}

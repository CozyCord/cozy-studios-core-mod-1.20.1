package net.cozystudios.cozystudioscore.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetheriteTranquilLanternItem extends BlockItem {
    public NetheriteTranquilLanternItem(net.minecraft.block.Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("✨ Pushes hostile mobs away within 100 blocks").formatted(Formatting.DARK_PURPLE));
    }
}

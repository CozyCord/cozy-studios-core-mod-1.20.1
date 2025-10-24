package net.cozystudios.cozystudioscore.item.custom;

import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class JungleHornItem extends Item {

    public JungleHornItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        user.setCurrentHand(hand);

        if (!world.isClient) {
            world.playSound(
                    null,
                    user.getBlockPos(),
                    ModSounds.JUNGLE_HORN,
                    SoundCategory.RECORDS,
                    1.5f,
                    1.0f
            );

            user.getItemCooldownManager().set(this, 64);
        }

        return TypedActionResult.consume(stack);
    }
}
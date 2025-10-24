package net.cozystudios.cozystudioscore.item.custom;

import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldenLeafItem extends Item {
    private static final String USES_KEY = "GoldenLeafUses"; // NBT tag
    private static final int MAX_USES = 3;

    public GoldenLeafItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder()
                        .hunger(0)
                        .saturationModifier(0.0f)
                        .alwaysEdible()
                        .build())
                .maxCount(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            StatusEffectInstance current = player.getStatusEffect(StatusEffects.REGENERATION);

            if (current == null || current.getDuration() <= 40) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        100, // 5 seconds
                        0,
                        true,
                        false
                ));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            world.playSound(
                    null,
                    user.getX(), user.getY(), user.getZ(),
                    ModSounds.GOLDEN_LEAF_USE,
                    SoundCategory.PLAYERS,
                    0.65f,
                    0.95f + world.getRandom().nextFloat() * 0.1f
            );
        }

        if (user instanceof PlayerEntity player && !world.isClient) {
            player.heal(8.0f);
            player.getHungerManager().add(20, 1.0f);

            int usesBefore = stack.getOrCreateNbt().getInt(USES_KEY);
            int usesAfter = usesBefore + 1;
            int remaining = MAX_USES - usesAfter;

            if (remaining > 0) {
                Text msg = Text.literal("Uses left: ").formatted(Formatting.GOLD)
                        .append(Text.literal(String.valueOf(remaining)).formatted(Formatting.GREEN));
                player.sendMessage(msg, true);
            } else {
                Text msg = Text.literal("You wasted something beautiful.")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC);
                player.sendMessage(msg, true);
            }

            if (usesAfter >= MAX_USES) {
                stack.decrement(1);
            } else {
                stack.getOrCreateNbt().putInt(USES_KEY, usesAfter);
                stack.getOrCreateNbt().putInt("CustomModelData", usesAfter);
            }
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int uses = stack.getOrCreateNbt().getInt(USES_KEY);
        int remaining = MAX_USES - uses;

        tooltip.add(Text.literal("Grants infinite regeneration").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Can be eaten up to 3 times").formatted(Formatting.GRAY));

        if (stack.hasNbt() && remaining > 0) {
            tooltip.add(Text.literal("Uses left: " + remaining).formatted(Formatting.GREEN));
        } else if (stack.hasNbt() && remaining <= 0) {
            tooltip.add(Text.literal("No uses left").formatted(Formatting.RED));
        } else {
            tooltip.add(Text.literal("Uses left: 3").formatted(Formatting.GREEN));
        }
    }
}
package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Pseudo
@Mixin(targets = "net.satisfy.herbalbrews.core.items.DrinkBlockItem", remap = false)
public abstract class HerbalbrewsDrinkTooltipMixin {

    @Unique
    private static final Map<String, String> COZYSTUDIOSCORE$DEFAULT_EFFECT = Map.of(
            "herbalbrews:green_tea",      "herbalbrews:balanced",
            "herbalbrews:black_tea",      "herbalbrews:tough",
            "herbalbrews:oolong_tea",     "herbalbrews:fortune",
            "herbalbrews:coffee",         "herbalbrews:deeprush",
            "herbalbrews:milk_coffee",    "herbalbrews:deeprush",
            "herbalbrews:hibiscus_tea",   "herbalbrews:lifeleech",
            "herbalbrews:lavender_tea",   "herbalbrews:feral",
            "herbalbrews:rooibos_tea",    "minecraft:regeneration",
            "herbalbrews:yerba_mate_tea", "herbalbrews:bonding"
    );

    @Unique
    private static final Map<String, Integer> COZYSTUDIOSCORE$DEFAULT_DURATION = Map.of(
            "herbalbrews:green_tea",      1800,
            "herbalbrews:black_tea",      1800,
            "herbalbrews:oolong_tea",     1800,
            "herbalbrews:coffee",         1800,
            "herbalbrews:milk_coffee",    2600,
            "herbalbrews:hibiscus_tea",   1800,
            "herbalbrews:lavender_tea",   1800,
            "herbalbrews:rooibos_tea",    200,
            "herbalbrews:yerba_mate_tea", 1800
    );

    @Redirect(
            method = "method_7851",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;",
                    remap = true
            ),
            remap = false
    )
    private NbtCompound cozystudioscore$fakeTooltipNbt(ItemStack stack) {
        return cozystudioscore$withDefaultEffect(stack);
    }

    @Redirect(
            method = "method_7861",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;",
                    remap = true
            ),
            remap = false
    )
    private NbtCompound cozystudioscore$fakeDrinkNbt(ItemStack stack) {
        return cozystudioscore$withDefaultEffect(stack);
    }

    @Unique
    private static NbtCompound cozystudioscore$withDefaultEffect(ItemStack stack) {
        NbtCompound real = stack.getNbt();
        if (real != null && real.contains("Effect") && real.contains("EffectDuration")) {
            return real;
        }
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        if (itemId == null) return real;
        String key = itemId.toString();
        String effectId = COZYSTUDIOSCORE$DEFAULT_EFFECT.get(key);
        Integer duration = COZYSTUDIOSCORE$DEFAULT_DURATION.get(key);
        if (effectId == null || duration == null) return real;

        NbtCompound synth = real != null ? real.copy() : new NbtCompound();
        synth.putString("Effect", effectId);
        synth.putInt("EffectDuration", duration);
        return synth;
    }
}

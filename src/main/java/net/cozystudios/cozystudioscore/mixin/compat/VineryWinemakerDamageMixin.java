package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class VineryWinemakerDamageMixin {

    @ModifyVariable(
            method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int cozystudioscore$halveWinemakerDamage(int amount) {
        ItemStack self = (ItemStack) (Object) this;
        Identifier id = Registries.ITEM.getId(self.getItem());
        if (!"vinery".equals(id.getNamespace())) return amount;
        String path = id.getPath();
        if (path.startsWith("winemaker_") || path.equals("straw_hat")) {
            return Math.max(1, amount / 2);
        }
        return amount;
    }
}

package net.cozystudios.cozystudioscore.mixin.compat;

import net.cozystudios.cozystudioscore.util.MeleonEasterEgg;
import net.cozystudios.cozystudioscore.util.MeleonSpectacle;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMeleonBurnMixin {

    @Shadow int itemAge;
    @Shadow @Final @Mutable public float uniqueOffset;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cozystudioscore$meleonBurnTrigger(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        World world = self.getWorld();
        ItemStack stack = self.getStack();

        if (world.isClient()) {
            if (!MeleonSpectacle.isSpectacleStack(stack)) return;
            itemAge = 0;
            PlayerEntity viewer = world.getClosestPlayer(self, 64.0);
            if (viewer != null) {
                double dx = viewer.getX() - self.getX();
                double dz = viewer.getZ() - self.getZ();
                uniqueOffset = (float) (Math.atan2(dx, dz) + Math.PI);
            }
            return;
        }

        if (!MeleonSpectacle.anyActive() && !MeleonEasterEgg.matches(stack)) return;

        if (MeleonSpectacle.isActive(self)) {
            MeleonSpectacle.tick(self);
            if (self.isRemoved()) ci.cancel();
            return;
        }

        if (!MeleonEasterEgg.matches(stack)) return;
        if (self.isFireImmune()) return;
        if (!(self.isInLava() || self.isOnFire())) return;

        MeleonSpectacle.start(self);
    }
}

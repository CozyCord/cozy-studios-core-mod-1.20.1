package net.cozystudios.cozystudioscore.mixin.compat;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class ChippedWorkbenchDropFilterMixin {

    private static volatile Class<?> cozystudioscore$workbenchClass;
    private static volatile boolean cozystudioscore$workbenchResolved;

    @Inject(method = "getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true)
    private void cozystudioscore$filterChippedSide(LootContextParameterSet.Builder builder,
                                                   CallbackInfoReturnable<List<ItemStack>> cir) {
        Class<?> wb = cozystudioscore$workbenchClass;
        if (!cozystudioscore$workbenchResolved) {
            synchronized (ChippedWorkbenchDropFilterMixin.class) {
                if (!cozystudioscore$workbenchResolved) {
                    try {
                        cozystudioscore$workbenchClass = Class.forName(
                                "earth.terrarium.chipped.common.blocks.WorkbenchBlock");
                    } catch (ClassNotFoundException e) {
                        cozystudioscore$workbenchClass = null;
                    }
                    cozystudioscore$workbenchResolved = true;
                }
            }
            wb = cozystudioscore$workbenchClass;
        }
        if (wb == null) return;
        BlockState self = (BlockState) (Object) this;
        if (!wb.isInstance(self.getBlock())) return;
        Property<?> modelProp = self.getBlock().getStateManager().getProperty("model");
        if (modelProp == null) return;
        if (!"main".equals(self.get(modelProp).toString())) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}

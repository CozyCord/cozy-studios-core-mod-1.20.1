package net.cozystudios.cozystudioscore.mixin.compat;

import com.google.gson.JsonElement;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerStripMixin {

    private static final List<Identifier> COZYSTUDIOSCORE$STRIPPED = List.of(
            new Identifier("farm_and_charm", "mincer/raw_iron_from_iron_ore"),
            new Identifier("farm_and_charm", "mincer/raw_iron_from_deepslate_iron_ore"),
            new Identifier("farm_and_charm", "mincer/raw_gold_from_gold_ore"),
            new Identifier("farm_and_charm", "mincer/raw_gold_from_deepslate_gold_ore"),
            new Identifier("farm_and_charm", "mincer/raw_copper_from_copper_ore"),
            new Identifier("farm_and_charm", "mincer/raw_copper_from_deepslate_copper_ore"),
            new Identifier("farm_and_charm", "mincer/emerald_from_emerald_ore"),
            new Identifier("farm_and_charm", "mincer/emerald_from_deepslate_emerald_ore"),
            new Identifier("farm_and_charm", "mincer/lapis_from_lapis_ore"),
            new Identifier("farm_and_charm", "mincer/lapis_from_deepslate_lapis_ore"),
            new Identifier("farm_and_charm", "mincer/redstone_dust_from_redstone_ore"),
            new Identifier("farm_and_charm", "mincer/redstone_dust_from_deepslate_redstone_ore"),
            new Identifier("conditional", "farm_and_charm/mincer/coal_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/diamond_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/emerald_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/gold_nugget_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/iron_nugget_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/lapis_lazuli_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/raw_copper_from_mincer"),
            new Identifier("conditional", "farm_and_charm/mincer/redstone_from_mincer")
    );

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"))
    private void cozystudioscore$stripFncStockOreRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        for (Identifier id : COZYSTUDIOSCORE$STRIPPED) {
            map.remove(id);
        }
    }
}

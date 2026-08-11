package net.cozystudios.cozystudioscore.mixin.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        FabricLoader loader = FabricLoader.getInstance();
        String simple = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        return switch (simple) {
            case "BuddingTomatoBlockMayPlaceMixin" -> loader.isModLoaded("farmersdelight");
            case "BwgFarmLandBlockSprinklerMixin"  -> loader.isModLoaded("biomeswevegone")     && loader.isModLoaded("farm_and_charm");
            case "PeatFarmBlockSprinklerMixin"     -> loader.isModLoaded("regions_unexplored") && loader.isModLoaded("farm_and_charm");
            case "SiltFarmBlockSprinklerMixin"     -> loader.isModLoaded("regions_unexplored") && loader.isModLoaded("farm_and_charm");
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}

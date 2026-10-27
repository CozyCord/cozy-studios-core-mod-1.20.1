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
            case "ChippedWorkbenchDoubleDropMixin" -> loader.isModLoaded("chipped");
            case "ChippedWorkbenchDropFilterMixin" -> loader.isModLoaded("chipped");
            case "VineryWinemakerDurabilityMixin"  -> loader.isModLoaded("vinery");
            case "VineryWinemakerDamageMixin"      -> loader.isModLoaded("vinery");
            case "VineryWineAgingSleepMixin"       -> loader.isModLoaded("vinery");
            case "ItemEntityMeleonBurnMixin"       -> loader.isModLoaded("perfectplushies");
            case "FurnaceMeleonFuelMixin"          -> loader.isModLoaded("perfectplushies");
            case "SpelunkerySpringWaterInfiniteMixin" -> loader.isModLoaded("spelunkery");
            case "SpelunkeryChiselVeinMiningMixin" -> loader.isModLoaded("spelunkery") && loader.isModLoaded("veinmining");
            case "SophisticatedChestDoubleOnPlacedMixin" -> loader.isModLoaded("sophisticatedstorage");
            case "BrewstationBoozeFailSpawnMixin"  -> loader.isModLoaded("brewery");
            case "CreateTreeFertilizerFixMixin"    -> loader.isModLoaded("create");
            case "CropBlockFertilizedMoistureMixin" -> loader.isModLoaded("farm_and_charm");
            case "RecipeManagerStripMixin"         -> loader.isModLoaded("farm_and_charm") || loader.isModLoaded("meadow");
            case "HerbalbrewsDrinkTooltipMixin"    -> loader.isModLoaded("herbalbrews");
            case "JeiClientLifecycleHandlerCaptureMixin" -> loader.isModLoaded("fairylights") && loader.isModLoaded("jei");
            case "NiftyCartsSendGuardMixin"        -> loader.isModLoaded("niftycarts");
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

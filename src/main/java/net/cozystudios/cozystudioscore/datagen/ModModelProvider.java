package net.cozystudios.cozystudioscore.datagen;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.MUSHLING_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.FERNLING_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.MYSTICAL_ELK_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.MYSTICAL_TRADER_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.COZY_CRUMBS, Models.GENERATED);
        itemModelGenerator.register(ModItems.MYSTICAL_BERRIES, Models.GENERATED);
        itemModelGenerator.register(ModItems.JUNGLE_HORN, Models.GENERATED);

        generateExtraModel("golden_leaf_bite1", itemModelGenerator);
        generateExtraModel("golden_leaf_bite2", itemModelGenerator);
        generateExtraModel("golden_leaf_bite3", itemModelGenerator);

        itemModelGenerator.register(ModItems.AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.DAYSPRING_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.WINDSWEPT_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.HOMEWARD_MUSIC_DISC, Models.GENERATED);
    }

    private void generateExtraModel(String name, ItemModelGenerator generator) {
        Model model = Models.GENERATED;
        Identifier id = new Identifier("cozystudioscore", "item/" + name);

        model.upload(id, TextureMap.layer0(id), generator.writer);
    }
}
package net.cozystudios.cozystudioscore.datagen;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    private static RegistryKey<Item> key(Item item) {
        return Registries.ITEM.getKey(item).orElseThrow(() ->
                new IllegalStateException("Item not registered: " + item));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        // --- Music Discs ---
        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS)
                .add(
                        key(ModItems.AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC),
                        key(ModItems.DAYSPRING_MUSIC_DISC)
                );

        // --- Creeper-Drop Discs ---
        getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(
                        key(ModItems.DAYSPRING_MUSIC_DISC),
                        key(ModItems.AT_ORIGINAL_SOUNDTRACK_MUSIC_DISC)
                );
    }
}
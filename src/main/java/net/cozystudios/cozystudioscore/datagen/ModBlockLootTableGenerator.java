package net.cozystudios.cozystudioscore.datagen;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public ModBlockLootTableGenerator(FabricDataOutput dataOutput,
                                      CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.KILN);
        addDrop(ModBlocks.TRANQUIL_LANTERN);
        addDrop(ModBlocks.GOLDEN_TRANQUIL_LANTERN);
        addDrop(ModBlocks.DIAMOND_TRANQUIL_LANTERN);
        addDrop(ModBlocks.NETHERITE_TRANQUIL_LANTERN);
        addDrop(ModBlocks.ARBORIST_TABLE);
    }
}
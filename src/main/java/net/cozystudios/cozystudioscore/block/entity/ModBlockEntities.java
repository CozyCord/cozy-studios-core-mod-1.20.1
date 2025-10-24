package net.cozystudios.cozystudioscore.block.entity;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<TranquilLanternBlockEntity> TRANQUIL_LANTERN;
    public static BlockEntityType<KilnBlockEntity> KILN;

    public static void registerBlockEntities() {
        TRANQUIL_LANTERN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "tranquil_lantern"),
                BlockEntityType.Builder.create(
                        TranquilLanternBlockEntity::new,
                        ModBlocks.TRANQUIL_LANTERN
                ).build(null)
        );

        KILN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "kiln"),
                BlockEntityType.Builder.create(
                        KilnBlockEntity::new,
                        ModBlocks.KILN
                ).build(null)
        );

        CozyStudiosCore.LOGGER.info("Registering Block Entities for " + CozyStudiosCore.MOD_ID);
    }
}
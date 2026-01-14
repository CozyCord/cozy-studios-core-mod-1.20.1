package net.cozystudios.cozystudioscore.block.entity;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<TranquilLanternBlockEntity> TRANQUIL_LANTERN;
    public static BlockEntityType<GoldenTranquilLanternBlockEntity> GOLDEN_TRANQUIL_LANTERN;
    public static BlockEntityType<DiamondTranquilLanternBlockEntity> DIAMOND_TRANQUIL_LANTERN;
    public static BlockEntityType<NetheriteTranquilLanternBlockEntity> NETHERITE_TRANQUIL_LANTERN;
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

        GOLDEN_TRANQUIL_LANTERN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "golden_tranquil_lantern"),
                BlockEntityType.Builder.create(
                        GoldenTranquilLanternBlockEntity::new,
                        ModBlocks.GOLDEN_TRANQUIL_LANTERN
                ).build(null)
        );

        DIAMOND_TRANQUIL_LANTERN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "diamond_tranquil_lantern"),
                BlockEntityType.Builder.create(
                        DiamondTranquilLanternBlockEntity::new,
                        ModBlocks.DIAMOND_TRANQUIL_LANTERN
                ).build(null)
        );

        NETHERITE_TRANQUIL_LANTERN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(CozyStudiosCore.MOD_ID, "netherite_tranquil_lantern"),
                BlockEntityType.Builder.create(
                        NetheriteTranquilLanternBlockEntity::new,
                        ModBlocks.NETHERITE_TRANQUIL_LANTERN
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

    }
}
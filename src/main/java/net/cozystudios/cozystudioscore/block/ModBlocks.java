package net.cozystudios.cozystudioscore.block;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.custom.ArboristTableBlock;
import net.cozystudios.cozystudioscore.block.custom.DiamondTranquilLanternBlock;
import net.cozystudios.cozystudioscore.block.custom.GoldenTranquilLanternBlock;
import net.cozystudios.cozystudioscore.block.custom.KilnBlock;
import net.cozystudios.cozystudioscore.block.custom.NetheriteTranquilLanternBlock;
import net.cozystudios.cozystudioscore.block.custom.TranquilLanternBlock;
import net.cozystudios.cozystudioscore.item.custom.DiamondTranquilLanternItem;
import net.cozystudios.cozystudioscore.item.custom.GoldenTranquilLanternItem;
import net.cozystudios.cozystudioscore.item.custom.NetheriteTranquilLanternItem;
import net.cozystudios.cozystudioscore.item.custom.TranquilLanternItem;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block TRANQUIL_LANTERN = registerBlock("tranquil_lantern",
            new TranquilLanternBlock(
                    Block.Settings.copy(Blocks.LANTERN)
                            .luminance(state -> 15)
                            .requiresTool()
                            .strength(2.0f, 3.0f) // hardness, blast resistance
            )
    );

    public static final Block GOLDEN_TRANQUIL_LANTERN = registerBlock("golden_tranquil_lantern",
            new GoldenTranquilLanternBlock(
                    Block.Settings.copy(Blocks.LANTERN)
                            .luminance(state -> 15)
                            .requiresTool()
                            .strength(2.5f, 4.0f)
            )
    );

    public static final Block DIAMOND_TRANQUIL_LANTERN = registerBlock("diamond_tranquil_lantern",
            new DiamondTranquilLanternBlock(
                    Block.Settings.copy(Blocks.LANTERN)
                            .luminance(state -> 15)
                            .requiresTool()
                            .strength(3.0f, 5.0f)
            )
    );

    public static final Block NETHERITE_TRANQUIL_LANTERN = registerBlock("netherite_tranquil_lantern",
            new NetheriteTranquilLanternBlock(
                    Block.Settings.copy(Blocks.LANTERN)
                            .luminance(state -> 15)
                            .requiresTool()
                            .strength(4.0f, 6.0f)
            )
    );

    public static final Block KILN = registerBlock("kiln",
            new KilnBlock(
                    Block.Settings.copy(Blocks.FURNACE)
                            .luminance(state -> state.get(AbstractFurnaceBlock.LIT) ? 13 : 0)
                            .strength(3.5f)
            )
    );

    public static final Block ARBORIST_TABLE = registerBlock("arborist_table",
            new ArboristTableBlock(Block.Settings.copy(Blocks.COMPOSTER))
    );


    // --- Registration helpers ---
    @SuppressWarnings("SameParameterValue")
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CozyStudiosCore.MOD_ID, name), block);
    }

    @SuppressWarnings("UnusedReturnValue")
    private static Item registerBlockItem(String name, Block block) {
        if (name.equals("tranquil_lantern")) {
            return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                    new TranquilLanternItem(block, new Item.Settings()));
        }
        if (name.equals("golden_tranquil_lantern")) {
            return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                    new GoldenTranquilLanternItem(block, new Item.Settings()));
        }
        if (name.equals("diamond_tranquil_lantern")) {
            return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                    new DiamondTranquilLanternItem(block, new Item.Settings()));
        }
        if (name.equals("netherite_tranquil_lantern")) {
            return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                    new NetheriteTranquilLanternItem(block, new Item.Settings()));
        }

        return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }


    public static void registerModBlocks() {
    }
}
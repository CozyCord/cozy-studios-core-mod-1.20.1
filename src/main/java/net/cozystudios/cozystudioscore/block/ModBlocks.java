package net.cozystudios.cozystudioscore.block;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.block.custom.ArboristTableBlock;
import net.cozystudios.cozystudioscore.block.custom.KilnBlock;
import net.cozystudios.cozystudioscore.block.custom.TranquilLanternBlock;
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

        return Registry.register(Registries.ITEM, new Identifier(CozyStudiosCore.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }


    public static void registerModBlocks() {
        CozyStudiosCore.LOGGER.info("Registering ModBlocks for " + CozyStudiosCore.MOD_ID);
    }
}
package net.cozystudios.cozystudioscore;

import me.shedaniel.autoconfig.AutoConfig;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.block.entity.ModBlockEntities;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.item.ModItemGroups;
import net.cozystudios.cozystudioscore.item.ModItems;
import net.cozystudios.cozystudioscore.loot.ModLootInjector;
import net.cozystudios.cozystudioscore.recipe.ConfigKilnDataPack;
import net.cozystudios.cozystudioscore.recipe.ModRecipeTypes;
import net.cozystudios.cozystudioscore.sound.ModSounds;
import net.cozystudios.cozystudioscore.world.ModTradersSpawner;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Field;

public class CozyStudiosCore implements ModInitializer {
    public static final String MOD_ID = "cozystudioscore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.register();
        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModRecipeTypes.register();
        net.cozystudios.cozystudioscore.screen.ModScreenHandlers.registerScreenHandlers();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            ConfigKilnDataPack.writePackForServer(server, config);
        });
        net.cozystudios.cozystudioscore.util.ModMasonTrades.registerCustomTrades();
        net.cozystudios.cozystudioscore.events.RightClickRecolorHandler.register();
        ModEntities.registerModEntities();
        net.cozystudios.cozystudioscore.util.ModRegistries.registerModStuffs();
        net.cozystudios.cozystudioscore.world.gen.ModWorldGeneration.generateModWorldGeneration();
        ModSounds.registerSounds();
        net.cozystudios.cozystudioscore.villager.ModVillagers.registerVillagers();
        ModTradersSpawner.register();
        ModLootInjector.register();

        // === Extra: configurable stack sizes ===
        var config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (config.maxStackSizeOverride >= 16 && config.maxStackSizeOverride <= 64) {
            fixStackSizes(config.maxStackSizeOverride);
        }

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            var state = net.cozystudios.cozystudioscore.save.FirstJoinGiftState.get(server);
            if (state.markIfNew(player.getUuid())) {
                ItemStack gift = new ItemStack(net.cozystudios.cozystudioscore.item.ModItems.GOLDEN_LEAF);

                if (!player.getInventory().insertStack(gift)) {
                    player.dropItem(gift, false);
                }
            }
        });


        LOGGER.info("Cozy Studios Core initialized!");
    }

    private void fixStackSizes(int newMax) {
        for (Item item : Registries.ITEM) {
            if (item.getMaxCount() == 16) {
                try {
                    Field field = Item.class.getDeclaredField("maxCount");
                    field.setAccessible(true);
                    field.set(item, newMax);
                } catch (Exception e) {
                    LOGGER.error("Could not change max stack-size of {}: {}",
                            Registries.ITEM.getId(item), e.toString());
                }
            }
        }
    }
}
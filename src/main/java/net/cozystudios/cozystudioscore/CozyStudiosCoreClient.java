package net.cozystudios.cozystudioscore;

import net.cozystudios.cozystudioscore.client.ModKeybinds;
import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.cozystudios.cozystudioscore.client.render.TranquilLanternRadiusRenderer;
import net.cozystudios.cozystudioscore.entity.ModEntities;
import net.cozystudios.cozystudioscore.entity.client.*;
import net.cozystudios.cozystudioscore.entity.layer.ModModelLayers;
import net.cozystudios.cozystudioscore.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.screen.KilnScreen;
import net.cozystudios.cozystudioscore.screen.ModScreenHandlers;

public class CozyStudiosCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRANQUIL_LANTERN, RenderLayer.getCutout());

        HandledScreens.register(ModScreenHandlers.KILN_SCREEN_HANDLER, KilnScreen::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MUSHLING, MushlingModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.MUSHLING, MushlingRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FERNLING, FernlingModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.FERNLING, FernlingRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MYSTICAL_ELK, MysticalElkModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.MYSTICAL_ELK, MysticalElkRenderer::new);
        EntityRendererRegistry.register(ModEntities.MYSTICAL_TRADER, MysticalTraderRenderer::new);

        TranquilLanternRadiusRenderer.register();

        ModKeybinds.register();
        ModNetworking.initClient();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TranquilLanternClientState.clear();
        });
    }
}
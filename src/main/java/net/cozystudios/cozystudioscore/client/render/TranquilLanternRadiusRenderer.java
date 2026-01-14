package net.cozystudios.cozystudioscore.client.render;

import net.cozystudios.cozystudioscore.block.ModBlocks;
import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TranquilLanternRadiusRenderer {

    public static boolean SHOW_RADIUS = false;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (!SHOW_RADIUS) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider provider = context.consumers();

            for (BlockPos pos : TranquilLanternClientState.getLanterns()) {
                BlockState state = TranquilLanternClientState.getLanternState(pos);
                if (state != null) {
                    drawLanternRadius(pos, state, matrices, provider, camera);
                }
            }
        });
    }

    private static void drawLanternRadius(BlockPos pos,
                                          BlockState state,
                                          MatrixStack matrices,
                                          VertexConsumerProvider provider,
                                          Camera camera) {

        // Get the radius based on the lantern type
        int radius = getRadiusForLantern(state);

        // Get the color based on the lantern type
        float[] color = getColorForLantern(state);

        Vec3d cam = camera.getPos();

        double x = pos.getX() + 0.5 - cam.x;
        double y = pos.getY() + 0.5 - cam.y;
        double z = pos.getZ() + 0.5 - cam.z;

        Box box = new Box(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );

        matrices.push();
        WorldRenderer.drawBox(
                matrices,
                provider.getBuffer(RenderLayer.getLines()),
                box,
                color[0], color[1], color[2], 1f
        );
        matrices.pop();
    }

    private static int getRadiusForLantern(BlockState state) {
        if (state.isOf(ModBlocks.NETHERITE_TRANQUIL_LANTERN)) {
            return ModConfig.get().getNetheriteTranquilLanternRadius();
        } else if (state.isOf(ModBlocks.DIAMOND_TRANQUIL_LANTERN)) {
            return ModConfig.get().getDiamondTranquilLanternRadius();
        } else if (state.isOf(ModBlocks.GOLDEN_TRANQUIL_LANTERN)) {
            return ModConfig.get().getGoldenTranquilLanternRadius();
        } else {
            return ModConfig.get().getTranquilLanternRadius();
        }
    }

    private static float[] getColorForLantern(BlockState state) {
        if (state.isOf(ModBlocks.NETHERITE_TRANQUIL_LANTERN)) {
            // Dark grey for netherite
            return new float[]{0.25f, 0.25f, 0.25f};
        } else if (state.isOf(ModBlocks.DIAMOND_TRANQUIL_LANTERN)) {
            // Light blue/cyan for diamond
            return new float[]{0.3f, 0.8f, 1.0f};
        } else if (state.isOf(ModBlocks.GOLDEN_TRANQUIL_LANTERN)) {
            // Gold/yellow for golden
            return new float[]{1.0f, 0.84f, 0.0f};
        } else {
            // White for base tranquil lantern
            return new float[]{1.0f, 1.0f, 1.0f};
        }
    }
}
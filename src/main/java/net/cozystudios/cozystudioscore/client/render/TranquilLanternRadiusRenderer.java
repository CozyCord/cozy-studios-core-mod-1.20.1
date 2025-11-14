package net.cozystudios.cozystudioscore.client.render;

import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

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
                drawLanternRadius(pos, matrices, provider, camera);
            }
        });
    }

    private static void drawLanternRadius(BlockPos pos,
                                          MatrixStack matrices,
                                          VertexConsumerProvider provider,
                                          Camera camera) {

        int radius = ModConfig.get().tranquilLanternRadius;

        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        double x = pos.getX() + 0.5 - camX;
        double y = pos.getY() + 0.5 - camY;
        double z = pos.getZ() + 0.5 - camZ;

        Box box = new Box(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );

        matrices.push();
        WorldRenderer.drawBox(
                matrices,
                provider.getBuffer(RenderLayer.getLines()),
                box,
                1f, 1f, 1f, 1f
        );
        matrices.pop();
    }
}
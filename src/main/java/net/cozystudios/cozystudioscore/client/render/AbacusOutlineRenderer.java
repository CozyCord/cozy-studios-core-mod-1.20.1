package net.cozystudios.cozystudioscore.client.render;

import net.cozystudios.cozystudioscore.item.ModItems;
import net.cozystudios.cozystudioscore.item.custom.AbacusItem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AbacusOutlineRenderer {

    private static final float R = 1.0f;
    private static final float G = 0.65f;
    private static final float B = 0.0f;
    private static final float A = 1.0f;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            ItemStack abacusStack = getHeldAbacus(client);
            if (abacusStack == null || !AbacusItem.isBound(abacusStack)) return;

            HitResult hit = client.crosshairTarget;
            if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

            BlockPos targetPos = ((BlockHitResult) hit).getBlockPos();
            BlockPos boundPos = AbacusItem.getBoundPos(abacusStack);
            if (boundPos == null) return;

            int distance = AbacusItem.getDistance(abacusStack, targetPos);
            if (distance < 0) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            VertexConsumerProvider provider = context.consumers();

            renderManhattanPath(boundPos, targetPos, matrices, provider, camera);
        });
    }

    private static ItemStack getHeldAbacus(MinecraftClient client) {
        ItemStack mainHand = client.player.getMainHandStack();
        if (mainHand.isOf(ModItems.ABACUS)) return mainHand;
        ItemStack offHand = client.player.getOffHandStack();
        if (offHand.isOf(ModItems.ABACUS)) return offHand;
        return null;
    }

    private static void renderManhattanPath(BlockPos boundPos, BlockPos targetPos,
                                             MatrixStack matrices,
                                             VertexConsumerProvider provider,
                                             Camera camera) {
        Vec3d cam = camera.getPos();

        int dx = targetPos.getX() - boundPos.getX();
        int dy = targetPos.getY() - boundPos.getY();
        int dz = targetPos.getZ() - boundPos.getZ();

        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;
        int stepZ = dz > 0 ? 1 : -1;

        matrices.push();
        VertexConsumer buffer = provider.getBuffer(RenderLayer.getLines());

        int x = boundPos.getX();
        int y = boundPos.getY();
        int z = boundPos.getZ();

        renderBlockOutline(matrices, buffer, x, y, z, cam);

        for (int i = 0; i < Math.abs(dx); i++) {
            x += stepX;
            renderBlockOutline(matrices, buffer, x, y, z, cam);
        }

        for (int i = 0; i < Math.abs(dy); i++) {
            y += stepY;
            renderBlockOutline(matrices, buffer, x, y, z, cam);
        }

        for (int i = 0; i < Math.abs(dz); i++) {
            z += stepZ;
            renderBlockOutline(matrices, buffer, x, y, z, cam);
        }

        matrices.pop();
    }

    private static void renderBlockOutline(MatrixStack matrices, VertexConsumer buffer,
                                            int blockX, int blockY, int blockZ, Vec3d cam) {
        double x = blockX - cam.x;
        double y = blockY - cam.y;
        double z = blockZ - cam.z;

        Box box = new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        WorldRenderer.drawBox(matrices, buffer, box, R, G, B, A);
    }
}

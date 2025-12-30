package net.cozystudios.cozystudioscore.client.render;

import net.cozystudios.cozystudioscore.client.TranquilLanternClientState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class TranquilLanternOutlineRenderer {

    private TranquilLanternOutlineRenderer() {}

    public static boolean SHOW_OUTLINE = false;

    private static final Identifier OUTLINE_TEX =
            new Identifier("minecraft", "textures/misc/white.png");

    private static final double OUTLINE_EPSILON = 0.003;

    public static void renderLanternOutlines(MatrixStack matrices, Camera camera) {
        if (!SHOW_OUTLINE) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        VertexConsumerProvider consumers = client.getBufferBuilders().getOutlineVertexConsumers();
        if (!(consumers instanceof OutlineVertexConsumerProvider outlineConsumers)) return;

        outlineConsumers.setColor(255, 255, 255, 255);

        Vec3d cam = camera.getPos();
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f posMat = entry.getPositionMatrix();
        Matrix3f normalMat = entry.getNormalMatrix();

        RenderLayer layer = RenderLayer.getOutline(OUTLINE_TEX);
        VertexConsumer vc = outlineConsumers.getBuffer(layer);

        for (BlockPos pos : TranquilLanternClientState.getLanterns()) {
            VoxelShape shape = client.world.getBlockState(pos)
                    .getOutlineShape(client.world, pos, ShapeContext.absent());
            if (shape.isEmpty()) continue;

            final double baseX = pos.getX() - cam.x;
            final double baseY = pos.getY() - cam.y;
            final double baseZ = pos.getZ() - cam.z;

            renderOuterFacesFromUnion(shape, vc, posMat, normalMat, baseX, baseY, baseZ);
        }
    }

    private enum Face { NEG_X, POS_X, NEG_Y, POS_Y, NEG_Z, POS_Z }

    private static void renderOuterFacesFromUnion(VoxelShape shape,
                                                  VertexConsumer vc,
                                                  Matrix4f posMat,
                                                  Matrix3f normalMat,
                                                  double baseX, double baseY, double baseZ) {

        ArrayList<double[]> boxes = new ArrayList<>();
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
                boxes.add(new double[]{minX, minY, minZ, maxX, maxY, maxZ})
        );

        final double eps = OUTLINE_EPSILON;
        final double tol = 1e-7;

        for (double[] b : boxes) {
            double minX = b[0], minY = b[1], minZ = b[2];
            double maxX = b[3], maxY = b[4], maxZ = b[5];

            double ex0 = minX - eps, ey0 = minY - eps, ez0 = minZ - eps;
            double ex1 = maxX + eps, ey1 = maxY + eps, ez1 = maxZ + eps;

            boolean showNegX = !isFaceCovered(boxes, minX, minY, minZ, maxY, maxZ, Face.NEG_X, tol);
            boolean showPosX = !isFaceCovered(boxes, maxX, minY, minZ, maxY, maxZ, Face.POS_X, tol);

            boolean showNegY = !isFaceCovered(boxes, minY, minX, minZ, maxX, maxZ, Face.NEG_Y, tol);
            boolean showPosY = !isFaceCovered(boxes, maxY, minX, minZ, maxX, maxZ, Face.POS_Y, tol);

            boolean showNegZ = !isFaceCovered(boxes, minZ, minX, minY, maxX, maxY, Face.NEG_Z, tol);
            boolean showPosZ = !isFaceCovered(boxes, maxZ, minX, minY, maxX, maxY, Face.POS_Z, tol);

            float x0 = (float) (baseX + ex0);
            float y0 = (float) (baseY + ey0);
            float z0 = (float) (baseZ + ez0);
            float x1 = (float) (baseX + ex1);
            float y1 = (float) (baseY + ey1);
            float z1 = (float) (baseZ + ez1);

            emitSelectedFaces(vc, posMat, normalMat,
                    x0, y0, z0, x1, y1, z1,
                    showPosY, showNegY,
                    showPosX, showNegX,
                    showPosZ, showNegZ);
        }
    }

    private static boolean isFaceCovered(List<double[]> boxes,
                                         double fixed,
                                         double a0, double b0,
                                         double a1, double b1,
                                         Face face,
                                         double tol) {

        for (double[] o : boxes) {
            double ox0 = o[0], oy0 = o[1], oz0 = o[2];
            double ox1 = o[3], oy1 = o[4], oz1 = o[5];

            switch (face) {
                case NEG_X -> {
                    if (Math.abs(ox1 - fixed) > tol) continue;
                    if (intervalCovers(oy0, oy1, a0, a1, tol) && intervalCovers(oz0, oz1, b0, b1, tol)) return true;
                }
                case POS_X -> {
                    if (Math.abs(ox0 - fixed) > tol) continue;
                    if (intervalCovers(oy0, oy1, a0, a1, tol) && intervalCovers(oz0, oz1, b0, b1, tol)) return true;
                }
                case NEG_Y -> {
                    if (Math.abs(oy1 - fixed) > tol) continue;
                    if (intervalCovers(ox0, ox1, a0, a1, tol) && intervalCovers(oz0, oz1, b0, b1, tol)) return true;
                }
                case POS_Y -> {
                    if (Math.abs(oy0 - fixed) > tol) continue;
                    if (intervalCovers(ox0, ox1, a0, a1, tol) && intervalCovers(oz0, oz1, b0, b1, tol)) return true;
                }
                case NEG_Z -> {
                    if (Math.abs(oz1 - fixed) > tol) continue;
                    if (intervalCovers(ox0, ox1, a0, a1, tol) && intervalCovers(oy0, oy1, b0, b1, tol)) return true;
                }
                case POS_Z -> {
                    if (Math.abs(oz0 - ( fixed ) ) > tol) continue;
                    if (intervalCovers(ox0, ox1, a0, a1, tol) && intervalCovers(oy0, oy1, b0, b1, tol)) return true;
                }
            }
        }
        return false;
    }

    private static double Ttf(double v) { return v; }

    private static boolean intervalCovers(double coverMin, double coverMax,
                                          double targetMin, double targetMax,
                                          double tol) {
        return coverMin <= targetMin + tol && coverMax >= targetMax - tol;
    }

    private static void emitSelectedFaces(VertexConsumer vc, Matrix4f mat, Matrix3f normalMat,
                                          float x0, float y0, float z0, float x1, float y1, float z1,
                                          boolean posY, boolean negY,
                                          boolean posX, boolean negX,
                                          boolean posZ, boolean negZ) {

        int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        int overlay = OverlayTexture.DEFAULT_UV;

        if (posY) quad(vc, mat, normalMat, overlay, light,
                x0, y1, z0, 0, 0,
                x1, y1, z0, 1, 0,
                x1, y1, z1, 1, 1,
                x0, y1, z1, 0, 1,
                0, 1, 0);

        if (negY) quad(vc, mat, normalMat, overlay, light,
                x0, y0, z1, 0, 0,
                x1, y0, z1, 1, 0,
                x1, y0, z0, 1, 1,
                x0, y0, z0, 0, 1,
                0, -1, 0);

        if (posX) quad(vc, mat, normalMat, overlay, light,
                x1, y0, z0, 0, 0,
                x1, y0, z1, 1, 0,
                x1, y1, z1, 1, 1,
                x1, y1, z0, 0, 1,
                1, 0, 0);

        if (negX) quad(vc, mat, normalMat, overlay, light,
                x0, y0, z1, 0, 0,
                x0, y0, z0, 1, 0,
                x0, y1, z0, 1, 1,
                x0, y1, z1, 0, 1,
                -1, 0, 0);

        if (posZ) quad(vc, mat, normalMat, overlay, light,
                x0, y0, z1, 0, 0,
                x1, y0, z1, 1, 0,
                x1, y1, z1, 1, 1,
                x0, y1, z1, 0, 1,
                0, 0, 1);

        if (negZ) quad(vc, mat, normalMat, overlay, light,
                x1, y0, z0, 0, 0,
                x0, y0, z0, 1, 0,
                x0, y1, z0, 1, 1,
                x1, y1, z0, 0, 1,
                0, 0, -1);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat, Matrix3f normalMat,
                             int overlay, int light,
                             float x0, float y0, float z0, float u0, float v0,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3,
                             float nx, float ny, float nz) {

        int r = 255, g = 255, b = 255, a = 255;

        vc.vertex(mat, x0, y0, z0).color(r, g, b, a).texture(u0, v0).overlay(overlay).light(light).normal(normalMat, nx, ny, nz).next();
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).texture(u1, v1).overlay(overlay).light(light).normal(normalMat, nx, ny, nz).next();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).texture(u2, v2).overlay(overlay).light(light).normal(normalMat, nx, ny, nz).next();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).texture(u3, v3).overlay(overlay).light(light).normal(normalMat, nx, ny, nz).next();
    }
}
package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.entity.custom.MysticalElkEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MysticalElkSaddleFeature
        extends FeatureRenderer<MysticalElkEntity, MysticalElkModel<MysticalElkEntity>> {

    private static final Identifier SADDLE_TEX =
            new Identifier("cozystudioscore", "textures/entity/mystical_elk_saddle.png");

    public MysticalElkSaddleFeature(FeatureRendererContext<MysticalElkEntity, MysticalElkModel<MysticalElkEntity>> ctx) {
        super(ctx);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light,
                       MysticalElkEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!entity.isSaddled()) return;

        RenderLayer layer = RenderLayer.getEntityCutoutNoCullZOffset(SADDLE_TEX);

        matrices.push();
        matrices.scale(1.1f, 0.9f, 1.1f);
        this.getContextModel().render(
                matrices,
                vertices.getBuffer(layer),
                light,
                OverlayTexture.DEFAULT_UV,
                1f, 1f, 1f, 1f
        );
        matrices.pop();
    }
}
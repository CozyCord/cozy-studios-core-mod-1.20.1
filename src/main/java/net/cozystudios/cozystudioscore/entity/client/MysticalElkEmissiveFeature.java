package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.MysticalElkEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MysticalElkEmissiveFeature extends FeatureRenderer<MysticalElkEntity, MysticalElkModel<MysticalElkEntity>> {
    private static final Identifier EYES_TEXTURE =
            new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mystical_elk_e.png");

    public MysticalElkEmissiveFeature(FeatureRendererContext<MysticalElkEntity, MysticalElkModel<MysticalElkEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       MysticalElkEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        RenderLayer emissiveLayer = RenderLayer.getEntityCutoutNoCullZOffset(EYES_TEXTURE);

        this.getContextModel().render(matrices,
                vertexConsumers.getBuffer(emissiveLayer),
                0xF000F0, // full bright
                OverlayTexture.DEFAULT_UV,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.MushlingEntity;
import net.cozystudios.cozystudioscore.entity.variant.MushlingVariant;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class MushlingEyesFeature extends FeatureRenderer<MushlingEntity, MushlingModel<MushlingEntity>> {
    private static final Map<MushlingVariant, Identifier> EYES_TEXTURES = new EnumMap<>(MushlingVariant.class);

    static {
        EYES_TEXTURES.put(MushlingVariant.DEFAULT,
                new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_e.png"));
        EYES_TEXTURES.put(MushlingVariant.ICE,
                new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_ice_e.png"));
        EYES_TEXTURES.put(MushlingVariant.CRIMSON,
                new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_crimson_e.png"));
        EYES_TEXTURES.put(MushlingVariant.LUSH,
                new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_lush_e.png"));
        EYES_TEXTURES.put(MushlingVariant.WARPED,
                new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_warped_e.png"));
    }

    public MushlingEyesFeature(FeatureRendererContext<MushlingEntity, MushlingModel<MushlingEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       MushlingEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        Identifier tex = EYES_TEXTURES.get(entity.getVariant());
        if (tex != null) {
            RenderLayer emissiveLayer = RenderLayer.getEntityCutoutNoCullZOffset(tex);

            this.getContextModel().render(matrices,
                    vertexConsumers.getBuffer(emissiveLayer),
                    0xF000F0,
                    OverlayTexture.DEFAULT_UV,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
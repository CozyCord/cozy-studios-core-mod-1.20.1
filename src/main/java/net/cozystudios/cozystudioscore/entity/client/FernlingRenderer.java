package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.FernlingEntity;
import net.cozystudios.cozystudioscore.entity.layer.ModModelLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import java.util.Map;
import com.google.common.collect.Maps;
import net.cozystudios.cozystudioscore.entity.variant.FernlingVariant;
import net.minecraft.util.Util;

public class FernlingRenderer extends LivingEntityRenderer<FernlingEntity, FernlingModel<FernlingEntity>> {
    private static final Map<FernlingVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(FernlingVariant.class), map -> {
                map.put(FernlingVariant.DEFAULT,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/fernling.png"));
                map.put(FernlingVariant.DESERT,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/fernling_desert.png"));
                map.put(FernlingVariant.ICE,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/fernling_ice.png"));
                map.put(FernlingVariant.SWAMP,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/fernling_swamp.png"));
            });

    // emissive eyes/glow layer
    private static final RenderLayer FERNLING_EYES_LAYER =
            RenderLayer.getEyes(new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/fernling_e.png"));

    public FernlingRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FernlingModel<>(ctx.getPart(ModModelLayers.FERNLING)), 0.6f);

        // add glowing eyes
        this.addFeature(new EyesFeatureRenderer<FernlingEntity, FernlingModel<FernlingEntity>>(this) {
            @Override
            public RenderLayer getEyesTexture() {
                return FERNLING_EYES_LAYER;
            }
        });
    }

    @Override
    protected boolean hasLabel(FernlingEntity entity) {
        return false;
    }

    @Override
    public Identifier getTexture(FernlingEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(FernlingEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {

        if (livingEntity.isBaby()) {
            matrixStack.scale(0.65f, 0.65f, 0.65f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult target = client.crosshairTarget;
        if (livingEntity.hasCustomName()
                && target != null
                && target.getType() == HitResult.Type.ENTITY
                && ((EntityHitResult) target).getEntity() == livingEntity) {

            double d = this.dispatcher.getSquaredDistanceToCamera(livingEntity);
            if (d <= 4096.0D) {
                Text name = livingEntity.getDisplayName();
                this.renderLabelIfPresent(livingEntity, name, matrixStack, vertexConsumerProvider, i);
            }
        }
    }

    @Override
    public Vec3d getPositionOffset(FernlingEntity entity, float tickDelta) {
        if (entity.isInSittingPose()) {
            return new Vec3d(0.0D, -0.1D, 0.0D);
        }
        return super.getPositionOffset(entity, tickDelta);
    }
}
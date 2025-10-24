package net.cozystudios.cozystudioscore.entity.client;

import com.google.common.collect.Maps;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.MushlingEntity;
import net.cozystudios.cozystudioscore.entity.layer.ModModelLayers;
import net.cozystudios.cozystudioscore.entity.variant.MushlingVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class MushlingRenderer extends LivingEntityRenderer<MushlingEntity, MushlingModel<MushlingEntity>> {
    private static final Map<MushlingVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(MushlingVariant.class), map -> {
                map.put(MushlingVariant.DEFAULT,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling.png"));
                map.put(MushlingVariant.ICE,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_ice.png"));
                map.put(MushlingVariant.CRIMSON,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_crimson.png"));
                map.put(MushlingVariant.LUSH,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_lush.png"));
                map.put(MushlingVariant.WARPED,
                        new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mushling_warped.png"));
            });

    public MushlingRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MushlingModel<>(ctx.getPart(ModModelLayers.MUSHLING)), 0.6f);

        this.addFeature(new MushlingEyesFeature(this));
    }

    @Override
    protected boolean hasLabel(MushlingEntity entity) {
        return false;
    }

    @Override
    public void render(MushlingEntity entity, float f, float g, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        if (entity.isBaby()) {
            matrices.scale(0.69f, 0.69f, 0.69f);
        }

        super.render(entity, f, g, matrices, vertexConsumers, light);

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult target = client.crosshairTarget;
        if (entity.hasCustomName()
                && target != null
                && target.getType() == HitResult.Type.ENTITY
                && ((EntityHitResult) target).getEntity() == entity) {

            double d = this.dispatcher.getSquaredDistanceToCamera(entity);
            if (d <= 4096.0D) {
                Text name = entity.getDisplayName();
                this.renderLabelIfPresent(entity, name, matrices, vertexConsumers, light);
            }
        }
    }

    @Override
    public Identifier getTexture(MushlingEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public Vec3d getPositionOffset(MushlingEntity entity, float tickDelta) {
        if (entity.isInSittingPose()) {
            return new Vec3d(0.0D, -0.1D, 0.0D);
        }
        return super.getPositionOffset(entity, tickDelta);
    }
}
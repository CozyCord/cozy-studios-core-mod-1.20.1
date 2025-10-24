package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.MysticalElkEntity;
import net.cozystudios.cozystudioscore.entity.layer.ModModelLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class MysticalElkRenderer extends MobEntityRenderer<MysticalElkEntity, MysticalElkModel<MysticalElkEntity>> {
    private static final Identifier TEXTURE =
            new Identifier(CozyStudiosCore.MOD_ID, "textures/entity/mystical_elk.png");

    public MysticalElkRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MysticalElkModel<>(ctx.getPart(ModModelLayers.MYSTICAL_ELK)), 0.8f);

        // add glowing emissive texture
        this.addFeature(new MysticalElkEmissiveFeature(this));

        // add saddle texture
        this.addFeature(new MysticalElkSaddleFeature(this));
    }

    @Override
    protected boolean hasLabel(MysticalElkEntity entity) {
        return false;
    }

    @Override
    public Identifier getTexture(MysticalElkEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(MysticalElkEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {

        if (livingEntity.isBaby()) {
            matrixStack.scale(0.7f, 0.7f, 0.7f);
        } else {
            matrixStack.scale(1.2f, 1.2f, 1.2f);
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
}
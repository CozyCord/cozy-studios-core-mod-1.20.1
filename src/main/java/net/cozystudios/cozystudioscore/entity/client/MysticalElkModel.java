package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.entity.animations.ModMysticalElkAnimations;
import net.cozystudios.cozystudioscore.entity.custom.MysticalElkEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class MysticalElkModel<T extends MysticalElkEntity> extends SinglePartEntityModel<T> {
    private final ModelPart elk;
    private final ModelPart head;

    public MysticalElkModel(ModelPart root) {
        this.elk = root.getChild("elk");
        ModelPart body = this.elk.getChild("body");
        this.head = body.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData elk = modelPartData.addChild("elk", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        ModelPartData body = elk.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData bone = body.addChild("bone", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-5.0F, -5.0F, -7.0F, 10.0F, 8.0F, 14.0F), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        body.addChild("left_arm", ModelPartBuilder.create().uv(40, 33)
                .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F), ModelTransform.pivot(3.5F, 2.0F, -5.5F));

        body.addChild("left_leg", ModelPartBuilder.create().uv(40, 33)
                .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F), ModelTransform.pivot(3.5F, 2.0F, 5.5F));

        body.addChild("right_leg", ModelPartBuilder.create().uv(40, 33).mirrored()
                .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F).mirrored(false), ModelTransform.pivot(-3.5F, 2.0F, 5.5F));

        body.addChild("right_arm", ModelPartBuilder.create().uv(40, 33).mirrored()
                .cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F).mirrored(false), ModelTransform.pivot(-3.5F, 2.0F, -5.5F));

        ModelPartData head = body.addChild("head", ModelPartBuilder.create()
                        .uv(0, 22).cuboid(-3.0F, -12.0F, -6.0F, 6.0F, 12.0F, 6.0F)
                        .uv(24, 33).cuboid(-2.0F, -9.0F, -10.0F, 4.0F, 4.0F, 4.0F),
                ModelTransform.pivot(0.0F, -1.0F, -5.0F));

        head.addChild("cube_r1", ModelPartBuilder.create().uv(24, 22).mirrored()
                        .cuboid(-11.0F, -23.0F, -8.0F, 11.0F, 11.0F, 0.0F).mirrored(false),
                ModelTransform.of(5.0F, 0.0F, 1.0F, 0.0F, 0.6981F, 0.0F));

        head.addChild("cube_r2", ModelPartBuilder.create().uv(24, 22)
                        .cuboid(0.0F, -23.0F, -8.0F, 11.0F, 11.0F, 0.0F),
                ModelTransform.of(-5.0F, 0.0F, 1.0F, 0.0F, -0.6981F, 0.0F));

        head.addChild("left_ear", ModelPartBuilder.create().uv(14, 40)
                        .cuboid(0.5F, -1.5F, -1.0F, 3.0F, 3.0F, 2.0F),
                ModelTransform.pivot(2.5F, -8.5F, -3.0F));

        head.addChild("right_ear", ModelPartBuilder.create().uv(14, 40).mirrored()
                        .cuboid(-3.5F, -1.5F, -1.0F, 3.0F, 3.0F, 2.0F).mirrored(false),
                ModelTransform.pivot(-2.5F, -8.5F, -3.0F));

        ModelPartData tail = body.addChild("Tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 7.0F));
        tail.addChild("cube_r3", ModelPartBuilder.create().uv(0, 40)
                        .cuboid(-1.5F, -3.5F, 7.0F, 3.0F, 3.0F, 4.0F),
                ModelTransform.of(0.0F, 4.0F, -6.0F, 0.48F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MysticalElkEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        head.yaw = netHeadYaw * (MathHelper.PI / 180F);
        head.pitch = headPitch * (MathHelper.PI / 180F);

        if (entity.runningAnimationState.isRunning()) {
            this.updateAnimation(entity.runningAnimationState, ModMysticalElkAnimations.MYSTICAL_ELK_RUNNING, ageInTicks, 1f);
        } else if (limbSwingAmount > 0.15F) {
            this.animateMovement(ModMysticalElkAnimations.MYSTICAL_ELK_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        } else {
            this.updateAnimation(entity.idleAnimationState, ModMysticalElkAnimations.MYSTICAL_ELK_IDLE, ageInTicks, 1f);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        elk.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return elk;
    }
}
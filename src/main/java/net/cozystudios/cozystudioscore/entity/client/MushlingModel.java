package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.entity.animations.ModAnimations;
import net.cozystudios.cozystudioscore.entity.custom.MushlingEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class MushlingModel<T extends MushlingEntity> extends SinglePartEntityModel<T> {
    private final ModelPart mushling;
    private final ModelPart head;

    public MushlingModel(ModelPart root) {
        this.mushling = root.getChild("mushling");
        this.head = mushling.getChild("body").getChild("body_2").getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData mushling = modelPartData.addChild("mushling", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData body = mushling.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

        ModelPartData body_2 = body.addChild("body_2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData bone = body_2.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5F, 0.0F));

        ModelPartData torso = bone.addChild("1", ModelPartBuilder.create().uv(24, 27).cuboid(-3.0F, -4.0F, -2.0F, 6.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, -0.5F));

        ModelPartData left_arm = bone.addChild("left_arm", ModelPartBuilder.create().uv(0, 37).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.5F, 0.0F));

        ModelPartData right_arm = bone.addChild("right_arm", ModelPartBuilder.create().uv(8, 37).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -2.5F, 0.0F));

        ModelPartData head = body_2.addChild("head", ModelPartBuilder.create().uv(0, 27).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData bone2 = head.addChild("bone2", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -0.25F, -6.0F, 12.0F, 3.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(-5.0F, -2.25F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.75F, 0.0F));

        ModelPartData head_1 = head.addChild("head_1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData eyebrow_1 = head.addChild("eyebrow_1", ModelPartBuilder.create().uv(32, 37).cuboid(0.0F, -1.5F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, -2.5F, -3.5F));

        ModelPartData eyebrow_2 = head.addChild("eyebrow_2", ModelPartBuilder.create().uv(38, 37).cuboid(-3.0F, -1.5F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -2.5F, -3.5F));

        ModelPartData plant = head.addChild("plant", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -6.0F, -3.0F));

        ModelPartData left_leg = body.addChild("left_leg", ModelPartBuilder.create().uv(16, 37).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 5.0F, 0.0F));

        ModelPartData right_leg = body.addChild("right_leg", ModelPartBuilder.create().uv(24, 37).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 5.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(MushlingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(entity, netHeadYaw, headPitch, ageInTicks);

        if (!entity.isInSittingPose()) {
            this.setHeadAngles(entity, netHeadYaw, headPitch, ageInTicks);
        }

        this.animateMovement(ModAnimations.WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);

        if (entity.isInSittingPose()) {
            this.updateAnimation(entity.sitAnimationState, ModAnimations.SLEEPING, ageInTicks, 1f);
        } else {
            this.updateAnimation(entity.idleAnimationState, ModAnimations.IDLE, ageInTicks, 1f);
        }
    }

    private void setHeadAngles(MushlingEntity entity, float headYaw, float headPitch, float animationProgress) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        mushling.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return mushling;
    }

    // Added: getter for the head (for emissive rendering)
    public ModelPart getHead() {
        return head;
    }
}
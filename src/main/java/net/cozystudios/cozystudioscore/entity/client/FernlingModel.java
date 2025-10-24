package net.cozystudios.cozystudioscore.entity.client;

import net.cozystudios.cozystudioscore.entity.animations.ModFernlingAnimations;
import net.cozystudios.cozystudioscore.entity.custom.FernlingEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class FernlingModel<T extends FernlingEntity> extends SinglePartEntityModel<T> {
    private final ModelPart fernling;
    private final ModelPart head;

    public FernlingModel(ModelPart root) {
        this.fernling = root.getChild("fernling");
        this.head = fernling.getChild("body").getChild("body_2").getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData fernling = modelPartData.addChild("fernling", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData body = fernling.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

        ModelPartData body_2 = body.addChild("body_2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 3.0F, 0.0F));

        ModelPartData bone = body_2.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5F, 0.0F));

        ModelPartData torso = bone.addChild("torso", ModelPartBuilder.create().uv(0, 12).cuboid(-3.0F, -4.0F, -2.0F, 6.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, -0.5F));

        ModelPartData left_arm = bone.addChild("left_arm", ModelPartBuilder.create().uv(22, 12).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -2.5F, 0.0F));

        ModelPartData right_arm = bone.addChild("right_arm", ModelPartBuilder.create().uv(22, 12).mirrored().cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, -2.5F, 0.0F));

        ModelPartData head = body_2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(12, 22).mirrored().cuboid(0.0F, -3.0F, -3.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-5.0F, -3.0F, 1.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r2 = head.addChild("cube_r2", ModelPartBuilder.create().uv(12, 22).cuboid(0.0F, -3.0F, -3.0F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -3.0F, 1.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData head_1 = head.addChild("head_1", ModelPartBuilder.create().uv(40, 0).cuboid(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData eyebrow_1 = head.addChild("eyebrow_1", ModelPartBuilder.create().uv(24, 0).cuboid(0.0F, -2.0F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, -2.5F, -3.5F));

        ModelPartData eyebrow_2 = head.addChild("eyebrow_2", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(-3.0F, -2.0F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, -2.5F, -3.5F));

        ModelPartData plant = head.addChild("plant", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, -6.0F, 0.0F, 0.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -3.0F));

        ModelPartData left_leg = body.addChild("left_leg", ModelPartBuilder.create().uv(22, 19).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 4.0F, 0.0F));

        ModelPartData right_leg = body.addChild("right_leg", ModelPartBuilder.create().uv(22, 19).mirrored().cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, 4.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(FernlingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(entity, netHeadYaw, headPitch, ageInTicks);

        this.animateMovement(ModFernlingAnimations.FERNLING_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, ModFernlingAnimations.FERNLING_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.sitAnimationState, ModFernlingAnimations.FERNLING_SLEEPING, ageInTicks, 1f);



    }

    private void setHeadAngles(FernlingEntity entity, float headYaw, float headPitch, float animationProgress) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        fernling.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return fernling;
    }
}
package net.cozystudios.cozystudioscore.mixin.client;

import net.cozystudios.cozystudioscore.client.render.TranquilLanternOutlineRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererOutlineMixin {

    @Shadow private BufferBuilderStorage bufferBuilders;

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void cozystudioscore$renderTranquilLanternOutlines(MatrixStack matrices,
                                                               float tickDelta,
                                                               long limitTime,
                                                               boolean renderBlockOutline,
                                                               Camera camera,
                                                               GameRenderer gameRenderer,
                                                               LightmapTextureManager lightmap,
                                                               Matrix4f projectionMatrix,
                                                               CallbackInfo ci) {

        OutlineVertexConsumerProvider outlineConsumers = this.bufferBuilders.getOutlineVertexConsumers();
        if (outlineConsumers == null) return;

        TranquilLanternOutlineRenderer.renderLanternOutlines(matrices, camera);
    }
}
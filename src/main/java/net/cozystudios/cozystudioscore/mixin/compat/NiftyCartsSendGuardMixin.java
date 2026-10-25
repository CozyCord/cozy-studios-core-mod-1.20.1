package net.cozystudios.cozystudioscore.mixin.compat;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.jmb19905.niftycarts.client.NiftyCartsClient", remap = false)
public abstract class NiftyCartsSendGuardMixin {

    @Redirect(
        method = "lambda$onInitializeClient$2",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking;send(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V",
            remap = true
        ),
        remap = false
    )
    private static void cozystudioscore$guardSend(Identifier channel, PacketByteBuf buf) {
        if (ClientPlayNetworking.canSend(channel)) {
            ClientPlayNetworking.send(channel, buf);
        }
    }
}

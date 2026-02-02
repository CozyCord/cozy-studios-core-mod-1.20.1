package net.cozystudios.cozystudioscore.mixin;

import net.cozystudios.cozystudioscore.config.GeneralConfig;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Unique private int cozyX, cozyY, cozyW, cozyH;

    @Unique
    private static boolean cozystudios$showCredits() {
        try {
            return GeneralConfig.get().showTitleScreenCredits;
        } catch (Throwable t) {
            return true;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void cozystudios$renderCredits(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!cozystudios$showCredits()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Text text = Text.literal("Mod Made By Cozy Studios").formatted(Formatting.UNDERLINE);

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        cozyW = mc.textRenderer.getWidth(text);
        cozyH = mc.textRenderer.fontHeight;
        cozyX = sw - cozyW - 4;
        cozyY = sh - mc.textRenderer.fontHeight - 12;

        boolean hovering = mouseX >= cozyX && mouseX <= cozyX + cozyW &&
                mouseY >= cozyY && mouseY <= cozyY + cozyH;

        int color = hovering ? 0xFFFF55 : 0x55FFFF;
        ctx.drawTextWithShadow(mc.textRenderer, text, cozyX, cozyY, color);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void cozystudios$click(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!cozystudios$showCredits()) return;
        if (button != 0) return;

        if (mouseX >= cozyX && mouseX <= cozyX + cozyW && mouseY >= cozyY && mouseY <= cozyY + cozyH) {
            Util.getOperatingSystem().open("https://modrinth.com/organization/cozystudios");
            cir.setReturnValue(true);
        }
    }
}
package net.cozystudios.cozystudioscore.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RestartRequiredScreen extends Screen {
    public RestartRequiredScreen() {
        super(Text.of("Restart Required"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.of("Quit Game"), button -> {
            MinecraftClient.getInstance().scheduleStop();
        }).dimensions(this.width / 2 - 100, this.height / 2, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("New mods have been detected and moved."), this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Please restart the game to apply changes."), this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
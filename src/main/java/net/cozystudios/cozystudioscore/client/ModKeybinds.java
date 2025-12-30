package net.cozystudios.cozystudioscore.client;

import net.cozystudios.cozystudioscore.client.render.TranquilLanternOutlineRenderer;
import net.cozystudios.cozystudioscore.client.render.TranquilLanternRadiusRenderer;
import net.cozystudios.cozystudioscore.network.ModNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding TOGGLE_LANTERN_RADIUS;

    public static void register() {
        TOGGLE_LANTERN_RADIUS = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.cozystudioscore.toggle_tranquil_lantern_radius",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "category.cozystudioscore"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_LANTERN_RADIUS.wasPressed()) {

                TranquilLanternRadiusRenderer.SHOW_RADIUS =
                        !TranquilLanternRadiusRenderer.SHOW_RADIUS;

                TranquilLanternOutlineRenderer.SHOW_OUTLINE =
                        TranquilLanternRadiusRenderer.SHOW_RADIUS;

                if (TranquilLanternRadiusRenderer.SHOW_RADIUS) {
                    ModNetworking.requestLanternSyncFromServer();

                    if (TranquilLanternClientState.getLanterns().isEmpty()) {
                        TranquilLanternClientState.rescanAroundPlayer(MinecraftClient.getInstance());
                    }
                }

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("Tranquil Lantern radius: " +
                                    (TranquilLanternRadiusRenderer.SHOW_RADIUS ? "ON" : "OFF")),
                            true
                    );
                }
            }
        });
    }
}
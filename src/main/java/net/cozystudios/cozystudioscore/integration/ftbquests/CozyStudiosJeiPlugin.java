package net.cozystudios.cozystudioscore.integration.ftbquests;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.Identifier;

@JeiPlugin
public class CozyStudiosJeiPlugin implements IModPlugin {
    private static final Identifier UID = new Identifier("cozystudioscore", "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JeiRuntimeHolder.set(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        JeiRuntimeHolder.set(null);
    }
}

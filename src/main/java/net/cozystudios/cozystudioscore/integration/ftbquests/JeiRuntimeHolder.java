package net.cozystudios.cozystudioscore.integration.ftbquests;

import mezz.jei.api.runtime.IJeiRuntime;

public final class JeiRuntimeHolder {
    private static volatile IJeiRuntime runtime;

    private JeiRuntimeHolder() {}

    public static void set(IJeiRuntime runtime) {
        JeiRuntimeHolder.runtime = runtime;
    }

    public static IJeiRuntime get() {
        return runtime;
    }
}

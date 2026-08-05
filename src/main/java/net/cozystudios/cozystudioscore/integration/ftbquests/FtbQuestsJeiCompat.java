package net.cozystudios.cozystudioscore.integration.ftbquests;

import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.integration.RecipeModHelper;
import net.cozystudios.cozystudioscore.CozyStudiosCore;

import java.lang.reflect.Field;

public final class FtbQuestsJeiCompat {
    private FtbQuestsJeiCompat() {}

    public static void init() {
        RecipeModHelper current = FTBQuests.getRecipeModHelper();
        String currentClass = current == null ? "null" : current.getClass().getName();
        JeiRecipeModHelper ours = new JeiRecipeModHelper();

        if (current == null || currentClass.endsWith(".RecipeModHelper$NoOp")) {
            try {
                FTBQuests.setRecipeModHelper(ours);
                return;
            } catch (IllegalStateException ignored) {
            }
        }

        try {
            Field field = FTBQuests.class.getDeclaredField("recipeModHelper");
            field.setAccessible(true);
            field.set(null, ours);
            CozyStudiosCore.LOGGER.info("Replaced FTB Quests RecipeModHelper ({}) with JEI-backed impl", currentClass);
        } catch (ReflectiveOperationException e) {
            CozyStudiosCore.LOGGER.error("Failed to force-replace FTB Quests RecipeModHelper", e);
        }
    }
}

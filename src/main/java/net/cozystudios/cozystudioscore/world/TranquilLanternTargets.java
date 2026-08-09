package net.cozystudios.cozystudioscore.world;

import net.cozystudios.cozystudioscore.tag.ModEntityTypeTags;
import net.minecraft.entity.Entity;

public final class TranquilLanternTargets {
    private TranquilLanternTargets() {}

    public static boolean isImmune(Entity entity) {
        return entity != null && entity.getType().isIn(ModEntityTypeTags.TRANQUIL_LANTERN_IMMUNE);
    }
}

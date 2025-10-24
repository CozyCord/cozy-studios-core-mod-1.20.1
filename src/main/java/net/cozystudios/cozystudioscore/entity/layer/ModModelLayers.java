package net.cozystudios.cozystudioscore.entity.layer;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;


public class ModModelLayers {
    public static final EntityModelLayer MUSHLING =
            new EntityModelLayer(new Identifier(CozyStudiosCore.MOD_ID, "mushling"), "main");

    public static final EntityModelLayer FERNLING =
            new EntityModelLayer(new Identifier(CozyStudiosCore.MOD_ID, "fernling"), "main");

    public static final EntityModelLayer MYSTICAL_ELK =
            new EntityModelLayer(new Identifier(CozyStudiosCore.MOD_ID, "mystical_elk"), "main");

}

package net.cozystudios.cozystudioscore.sound;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent GOLDEN_LEAF_USE = registerSoundEvent("golden_leaf_use");
    public static final SoundEvent AT_ORIGINAL_SOUNDTRACK = registerSoundEvent("at_original_soundtrack");
    public static final SoundEvent DAYSPRING = registerSoundEvent("dayspring");
    public static final SoundEvent FERNLING_IDLE = registerSoundEvent("fernling_idle");
    public static final SoundEvent MUSHLING_IDLE_1 = registerSoundEvent("mushling_idle_1");
    public static final SoundEvent MUSHLING_IDLE_2 = registerSoundEvent("mushling_idle_2");
    public static final SoundEvent MUSHLING_IDLE_3 = registerSoundEvent("mushling_idle_3");
    public static final SoundEvent MUSHLING_IDLE_4 = registerSoundEvent("mushling_idle_4");
    public static final SoundEvent JUNGLE_HORN = registerSoundEvent("jungle_horn");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(CozyStudiosCore.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

public static void registerSounds() {
    }
}
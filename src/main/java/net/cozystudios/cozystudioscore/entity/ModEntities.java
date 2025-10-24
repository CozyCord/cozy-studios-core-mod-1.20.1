package net.cozystudios.cozystudioscore.entity;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<MushlingEntity> MUSHLING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CozyStudiosCore.MOD_ID, "mushling"),
            EntityType.Builder.create(MushlingEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.75f, 0.75f)
                    .build("mushling")
    );

    public static final EntityType<FernlingEntity> FERNLING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CozyStudiosCore.MOD_ID, "fernling"),
            EntityType.Builder.create(FernlingEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.75f, 0.75f)
                    .build("fernling")
    );

    public static final EntityType<MysticalElkEntity> MYSTICAL_ELK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CozyStudiosCore.MOD_ID, "mystical_elk"),
            EntityType.Builder.create(MysticalElkEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(1.4f, 2.0f)
                    .build("mystical_elk")
    );

    public static final EntityType<MysticalTraderEntity> MYSTICAL_TRADER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CozyStudiosCore.MOD_ID, "mystical_trader"),
            EntityType.Builder.create(MysticalTraderEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.95f)
                    .build("mystical_trader")
    );

    public static void registerModEntities() {
        CozyStudiosCore.LOGGER.info("Registering Mod Entities for " + CozyStudiosCore.MOD_ID);

        FabricDefaultAttributeRegistry.register(MYSTICAL_TRADER, MysticalTraderEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MUSHLING, MushlingEntity.createMushlingAttributes());
        FabricDefaultAttributeRegistry.register(FERNLING, FernlingEntity.createFernlingAttributes());
        FabricDefaultAttributeRegistry.register(MYSTICAL_ELK, MysticalElkEntity.createMysticalElkAttributes());
    }
}
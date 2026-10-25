package net.cozystudios.cozystudioscore.compat.candlelight;

import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public final class CandlelightRingTrinketHandler {

    private static final Identifier GOLD_RING_ID = new Identifier("candlelight", "gold_ring");
    private static final int TICK_INTERVAL = 20;
    private static final int EFFECT_DURATION = 300;
    private static final int EFFECT_AMPLIFIER = 1;
    private static final int REFRESH_BELOW = 220;

    private static int tickCounter = 0;

    private static volatile Item goldRingItem;
    private static volatile boolean trinketsAvailable;
    private static volatile boolean accessoriesAvailable;

    private static volatile Method trinketsGetComponent;
    private static volatile Method optionalIsPresent;
    private static volatile Method optionalGet;
    private static volatile Method trinketComponentIsEquipped;

    private static volatile Method accessoriesGetOptionally;
    private static volatile Method accessoriesCapabilityIsEquipped;

    private CandlelightRingTrinketHandler() {}

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("candlelight")) return;

        trinketsAvailable = loader.isModLoaded("trinkets");
        accessoriesAvailable = loader.isModLoaded("accessories");
        if (!trinketsAvailable && !accessoriesAvailable) return;

        if (!resolveReflection()) {
            CozyStudiosCore.LOGGER.warn("[CandlelightRingTrinketHandler] reflection setup failed; skipping");
            return;
        }

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter < TICK_INTERVAL) return;
            tickCounter = 0;

            if (goldRingItem == null) {
                goldRingItem = Registries.ITEM.get(GOLD_RING_ID);
                if (goldRingItem == null || goldRingItem == Registries.ITEM.get(new Identifier("minecraft", "air"))) {
                    return;
                }
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator() || !player.isAlive()) continue;
                if (!isWearingGoldRing(player)) continue;

                StatusEffectInstance existing = player.getStatusEffect(StatusEffects.LUCK);
                if (existing != null && existing.getDuration() >= REFRESH_BELOW && existing.getAmplifier() >= EFFECT_AMPLIFIER) {
                    continue;
                }

                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LUCK,
                        EFFECT_DURATION,
                        EFFECT_AMPLIFIER,
                        true,
                        false,
                        true
                ));
            }
        });

        CozyStudiosCore.LOGGER.info("[CandlelightRingTrinketHandler] registered (trinkets={}, accessories={})",
                trinketsAvailable, accessoriesAvailable);
    }

    private static boolean resolveReflection() {
        try {
            if (trinketsAvailable) {
                Class<?> trinketsApi = Class.forName("dev.emi.trinkets.api.TrinketsApi");
                trinketsGetComponent = trinketsApi.getMethod("getTrinketComponent", LivingEntity.class);

                optionalIsPresent = java.util.Optional.class.getMethod("isPresent");
                optionalGet = java.util.Optional.class.getMethod("get");

                Class<?> trinketComponent = Class.forName("dev.emi.trinkets.api.TrinketComponent");
                trinketComponentIsEquipped = trinketComponent.getMethod("isEquipped", Item.class);
            }
            if (accessoriesAvailable) {
                Class<?> accCap = Class.forName("io.wispforest.accessories.api.AccessoriesCapability");
                accessoriesGetOptionally = accCap.getMethod("getOptionally", LivingEntity.class);
                accessoriesCapabilityIsEquipped = accCap.getMethod("isEquipped", Item.class);

                if (optionalIsPresent == null) {
                    optionalIsPresent = java.util.Optional.class.getMethod("isPresent");
                    optionalGet = java.util.Optional.class.getMethod("get");
                }
            }
            return true;
        } catch (Throwable t) {
            CozyStudiosCore.LOGGER.error("[CandlelightRingTrinketHandler] reflection resolve failed: {}", t.toString());
            return false;
        }
    }

    private static boolean isWearingGoldRing(ServerPlayerEntity player) {
        if (trinketsAvailable) {
            try {
                Object opt = trinketsGetComponent.invoke(null, player);
                if ((Boolean) optionalIsPresent.invoke(opt)) {
                    Object component = optionalGet.invoke(opt);
                    if ((Boolean) trinketComponentIsEquipped.invoke(component, goldRingItem)) {
                        return true;
                    }
                }
            } catch (Throwable ignored) {}
        }
        if (accessoriesAvailable) {
            try {
                Object opt = accessoriesGetOptionally.invoke(null, player);
                if ((Boolean) optionalIsPresent.invoke(opt)) {
                    Object capability = optionalGet.invoke(opt);
                    if ((Boolean) accessoriesCapabilityIsEquipped.invoke(capability, goldRingItem)) {
                        return true;
                    }
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }
}

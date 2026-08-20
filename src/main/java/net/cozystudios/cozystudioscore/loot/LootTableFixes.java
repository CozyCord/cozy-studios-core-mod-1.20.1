package net.cozystudios.cozystudioscore.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LootTableFixes {

    private static final String NS = CozyStudiosCore.MOD_ID;

    private static final Map<Identifier, Identifier> BEACHPARTY_REPLACEMENTS = Map.of(
            id("beachparty", "chests/buried_treasure"),
            id(NS, "loot_replacements/beachparty/chests/buried_treasure"),
            id("beachparty", "chests/shipwreck_supply"),
            id(NS, "loot_replacements/beachparty/chests/shipwreck_supply"),
            id("beachparty", "chests/shipwreck_treasure"),
            id(NS, "loot_replacements/beachparty/chests/shipwreck_treasure"),
            id("beachparty", "chests/underwater_ruin_big"),
            id(NS, "loot_replacements/beachparty/chests/underwater_ruin_big"),
            id("beachparty", "chests/underwater_ruin_small"),
            id(NS, "loot_replacements/beachparty/chests/underwater_ruin_small")
    );

    private static final Map<Identifier, Identifier> INCENDIUM_REPLACEMENTS = Map.of(
            id("incendium", "castle/tower_barrel"),
            id(NS, "loot_replacements/incendium/castle/tower_barrel"),
            id("incendium", "castle/king_statue"),
            id(NS, "loot_replacements/incendium/castle/king_statue"),
            id("incendium", "castle/barrel/generic"),
            id(NS, "loot_replacements/incendium/castle/barrel/generic")
    );

    private static volatile Gson lootGson;
    private static volatile boolean lootGsonResolved;

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        Map<Identifier, Identifier> active = new HashMap<>();
        if (loader.isModLoaded("beachparty")) {
            active.putAll(BEACHPARTY_REPLACEMENTS);
        }
        if (loader.isModLoaded("incendium")) {
            active.putAll(INCENDIUM_REPLACEMENTS);
        }
        if (active.isEmpty()) return;

        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
            Identifier replacementId = active.get(id);
            if (replacementId == null) return null;
            return loadReplacement(resourceManager, replacementId, id);
        });
    }

    private static LootTable loadReplacement(ResourceManager rm, Identifier replacementId, Identifier originalId) {
        Gson gson = getLootGson();
        if (gson == null) return null;

        Identifier resourcePath = new Identifier(
                replacementId.getNamespace(),
                "loot_tables/" + replacementId.getPath() + ".json"
        );
        var resourceOpt = rm.getResource(resourcePath);
        if (resourceOpt.isEmpty()) {
            CozyStudiosCore.LOGGER.error(
                    "Loot replacement resource missing for {} (expected {})",
                    originalId, resourcePath);
            return null;
        }
        Resource resource = resourceOpt.get();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            JsonElement json = JsonParser.parseReader(reader);
            LootTable table = gson.fromJson(json, LootTable.class);
            CozyStudiosCore.LOGGER.info("Replaced loot table {} with fixed version", originalId);
            return table;
        } catch (Exception e) {
            CozyStudiosCore.LOGGER.error("Failed to deserialize loot replacement {} for {}: {}",
                    resourcePath, originalId, e.toString());
            return null;
        }
    }

    private static Gson getLootGson() {
        if (lootGsonResolved) return lootGson;
        synchronized (LootTableFixes.class) {
            if (lootGsonResolved) return lootGson;
            try {
                for (Field f : LootManager.class.getDeclaredFields()) {
                    if (f.getType() == Gson.class) {
                        f.setAccessible(true);
                        lootGson = (Gson) f.get(null);
                        break;
                    }
                }
            } catch (Exception e) {
                CozyStudiosCore.LOGGER.error("Could not resolve LootManager Gson via reflection", e);
            }
            lootGsonResolved = true;
            if (lootGson == null) {
                CozyStudiosCore.LOGGER.error("LootManager Gson not found; loot table fixes disabled");
            }
            return lootGson;
        }
    }

    private static Identifier id(String namespace, String path) {
        return new Identifier(namespace, path);
    }
}

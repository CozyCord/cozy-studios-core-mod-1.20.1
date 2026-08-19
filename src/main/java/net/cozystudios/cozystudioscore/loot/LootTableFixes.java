package net.cozystudios.cozystudioscore.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LootTableFixes {

    private static final String NS = CozyStudiosCore.MOD_ID;

    private static final Gson LOOT_GSON = resolveLootGson();

    private static Gson resolveLootGson() {
        for (Field f : LootManager.class.getDeclaredFields()) {
            if (f.getType() == Gson.class) {
                try {
                    f.setAccessible(true);
                    return (Gson) f.get(null);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        throw new IllegalStateException("Could not locate LootManager Gson via reflection");
    }

    private static final Map<Identifier, Identifier> REPLACEMENTS = Map.ofEntries(
            Map.entry(id("beachparty", "chests/buried_treasure"),
                    id(NS, "loot_replacements/beachparty/chests/buried_treasure")),
            Map.entry(id("beachparty", "chests/shipwreck_supply"),
                    id(NS, "loot_replacements/beachparty/chests/shipwreck_supply")),
            Map.entry(id("beachparty", "chests/shipwreck_treasure"),
                    id(NS, "loot_replacements/beachparty/chests/shipwreck_treasure")),
            Map.entry(id("beachparty", "chests/underwater_ruin_big"),
                    id(NS, "loot_replacements/beachparty/chests/underwater_ruin_big")),
            Map.entry(id("beachparty", "chests/underwater_ruin_small"),
                    id(NS, "loot_replacements/beachparty/chests/underwater_ruin_small")),
            Map.entry(id("incendium", "castle/tower_barrel"),
                    id(NS, "loot_replacements/incendium/castle/tower_barrel")),
            Map.entry(id("incendium", "castle/king_statue"),
                    id(NS, "loot_replacements/incendium/castle/king_statue")),
            Map.entry(id("incendium", "castle/barrel/generic"),
                    id(NS, "loot_replacements/incendium/castle/barrel/generic"))
    );

    public static void register() {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
            Identifier replacementId = REPLACEMENTS.get(id);
            if (replacementId == null) return null;
            return loadReplacement(resourceManager, replacementId, id);
        });
    }

    private static LootTable loadReplacement(ResourceManager rm, Identifier replacementId, Identifier originalId) {
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
            LootTable table = LOOT_GSON.fromJson(json, LootTable.class);
            CozyStudiosCore.LOGGER.info("Replaced loot table {} with fixed version", originalId);
            return table;
        } catch (Exception e) {
            CozyStudiosCore.LOGGER.error("Failed to deserialize loot replacement {} for {}: {}",
                    resourcePath, originalId, e.toString());
            return null;
        }
    }

    private static Identifier id(String namespace, String path) {
        return new Identifier(namespace, path);
    }
}

package net.cozystudios.cozystudioscore.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cozystudios.cozystudioscore.CozyStudiosCore;
import net.cozystudios.cozystudioscore.config.KilnConfig;
import net.cozystudios.cozystudioscore.config.ModConfig;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ConfigKilnDataPack {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String PACK_FOLDER_NAME = "cozystudioscore_kiln_config";
    private static final int PACK_FORMAT_1_20_1 = 15;

    private ConfigKilnDataPack() {}

    public static void writePackForServer(MinecraftServer server, KilnConfig config) {
        try {
            Path datapacksDir = server.getSavePath(WorldSavePath.DATAPACKS);
            Path packRoot = datapacksDir.resolve(PACK_FOLDER_NAME);

            Path packMeta = packRoot.resolve("pack.mcmeta");
            Path recipesDir = packRoot.resolve("data/cozystudioscore/recipes/kiln");
            Files.createDirectories(recipesDir);

            String mcmeta = """
                    {
                      "pack": {
                        "pack_format": %d,
                        "description": "Cozy Studios Core • Kiln recipes generated from config"
                      }
                    }
                    """.formatted(PACK_FORMAT_1_20_1);
            Files.writeString(packMeta, mcmeta, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(recipesDir, "*.json")) {
                for (Path p : stream) Files.deleteIfExists(p);
            } catch (IOException ignored) {}

            int written = 0;
            for (String line : config.extraKilnRecipes) {
                RecipeSpec spec = parseLine(line, config.kilnConfigDefaultXp, config.kilnConfigDefaultTime);
                if (spec == null) {
                    CozyStudiosCore.LOGGER.warn("[Kiln Config] Skipping invalid line: {}", line);
                    continue;
                }

                if (!Registries.ITEM.containsId(spec.input) || !Registries.ITEM.containsId(spec.output)) {
                    CozyStudiosCore.LOGGER.warn("[Kiln Config] Unknown item id in line: {}", line);
                    continue;
                }

                Map<String, Object> json = new LinkedHashMap<>();
                json.put("type", "cozystudioscore:kiln");
                json.put("ingredient", Map.of("item", spec.input.toString()));
                json.put("result", spec.output.toString());
                json.put("experience", spec.xp);
                json.put("cookingtime", spec.time);

                String safeIn = spec.input.toString().replace(':', '_').replace('/', '_');
                String safeOut = spec.output.toString().replace(':', '_').replace('/', '_');
                Path outFile = recipesDir.resolve("cfg_%s_to_%s.json".formatted(safeIn, safeOut));

                Files.writeString(outFile, GSON.toJson(json), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                written++;
            }

            CozyStudiosCore.LOGGER.info(
                    "[Kiln Config] Wrote {} config kiln recipes to datapack '{}'. Use /reload to apply changes.",
                    written, PACK_FOLDER_NAME);
        } catch (Exception e) {
            CozyStudiosCore.LOGGER.error("[Kiln Config] Failed to write kiln config datapack", e);
        }
    }

    private static RecipeSpec parseLine(String raw, double defXp, int defTime) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        String[] lr = s.split("->");
        if (lr.length != 2) return null;

        Identifier in = toId(lr[0]);
        if (in == null) return null;

        String right = lr[1];
        String[] parts = right.split(";");
        Identifier out = toId(parts[0]);
        if (out == null) return null;

        double xp = defXp;
        int time = defTime;

        Pattern XP = Pattern.compile("\\bxp\\s*=\\s*([0-9]*\\.?[0-9]+)\\b", Pattern.CASE_INSENSITIVE);
        Pattern TIME = Pattern.compile("\\btime\\s*=\\s*([0-9]+)\\b", Pattern.CASE_INSENSITIVE);

        Matcher mx = XP.matcher(s);
        if (mx.find()) xp = Double.parseDouble(mx.group(1));

        Matcher mt = TIME.matcher(s);
        if (mt.find()) time = Integer.parseInt(mt.group(1));

        return new RecipeSpec(in, out, xp, time);
    }

    private static Identifier toId(String side) {
        String t = side.trim().replace(",", "");
        if (t.isEmpty()) return null;
        try {
            return new Identifier(t);
        } catch (Exception e) {
            return null;
        }
    }

    private record RecipeSpec(Identifier input, Identifier output, double xp, int time) {}
}
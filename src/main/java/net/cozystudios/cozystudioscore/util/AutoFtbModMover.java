package net.cozystudios.cozystudioscore.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AutoFtbModMover implements PreLaunchEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("cozystudioscore - auto ftb mod mover");
    private static final String CONFIG_FILE_NAME = "cozystudioscore/auto_ftb_mods.json";

    @Override
    public void onPreLaunch() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve(CONFIG_FILE_NAME);

        if (!Files.exists(configFile)) {
            LOGGER.info("Config file not found, skipping auto FTB mod mover.");
            return;
        }

        Map<String, String> targetHashes = loadHashesFromConfig(configFile);
        if (targetHashes.isEmpty()) {
            LOGGER.info("No FTB mod hashes configured, skipping.");
            return;
        }

        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path modsDir = gameDir.resolve("mods");
        Path downloadsDir = getUserDownloadsDir();

        if (downloadsDir == null || !Files.exists(downloadsDir)) {
            LOGGER.warn("Could not locate user Downloads directory, skipping.");
            return;
        }

        LOGGER.info("Scanning Downloads folder for FTB mods...");
        
        try (Stream<Path> stream = Files.walk(downloadsDir, 1)) {
            stream.filter(path -> path.toString().endsWith(".jar"))
                  .forEach(jarPath -> processJar(jarPath, modsDir, targetHashes));
        } catch (IOException e) {
            LOGGER.error("Failed to scan Downloads directory", e);
        }
    }

    // gotta parse config ourselves since conf doesn't exist at prelaunch
    private Map<String, String> loadHashesFromConfig(Path configFile) {
        Map<String, String> hashes = new HashMap<>();
        try (InputStream is = Files.newInputStream(configFile)) {
            Gson gson = new Gson();
            String jsonString = new String(is.readAllBytes());
            JsonObject root = gson.fromJson(jsonString, JsonObject.class);

            if (root.has("enableAutoFtbMods") && !root.get("enableAutoFtbMods").getAsBoolean()) {
                return hashes;
            }

            addHash(hashes, root, "ftbTeamsHash");
            addHash(hashes, root, "ftbLibraryHash");
            addHash(hashes, root, "ftbEssentialsHash");
            addHash(hashes, root, "ftbFilterHash");
            addHash(hashes, root, "ftbQuestsHash");
            addHash(hashes, root, "ftbXmodHash");
            addHash(hashes, root, "questAdditionsHash");
        } catch (Exception e) {
            LOGGER.error("Failed to load config for AutoFtbModMover", e);
        }
        return hashes;
    }

    private void addHash(Map<String, String> map, JsonObject config, String key) {
        if (config.has(key)) {
            String hash = config.get(key).getAsString();
            if (hash != null && !hash.isEmpty()) {
                map.put(hash.toLowerCase(), key);
            }
        }
    }

    private void processJar(Path jarPath, Path modsDir, Map<String, String> targetHashes) {
        try {
            String fileHash = calculateSha1(jarPath);
            if (targetHashes.containsKey(fileHash)) {
                String modKey = targetHashes.get(fileHash);
                LOGGER.info("Found matching FTB mod: {} (Hash: {}). Moving to mods folder...", jarPath.getFileName(), modKey);
                
                Path targetPath = modsDir.resolve(jarPath.getFileName());

                Files.move(jarPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Moved {} to {}", jarPath.getFileName(), targetPath);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process jar: " + jarPath, e);
        }
    }

    private String calculateSha1(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Path getUserDownloadsDir() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) return null;
        
        Path downloads = Paths.get(userHome, "Downloads");
        if (Files.exists(downloads) && Files.isDirectory(downloads)) {
            return downloads;
        }
        return null;
    }
}

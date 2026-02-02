package net.cozystudios.cozystudioscore.util;

import net.cozystudios.cozystudioscore.client.RestartRequiredScreen;
import net.cozystudios.cozystudioscore.config.AutoFtbModsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public final class AutoFtbModMover {
    private static final Logger LOGGER = LoggerFactory.getLogger("CozyStudiosCore | AutoFtbModMover");
    private static final String HASH_ALGORITHM = "SHA-1";
    private static final int TICK_INTERVAL = 100;

    private static final AtomicBoolean RESTART_REQUIRED = new AtomicBoolean(false);
    private static final AtomicBoolean ALL_MODS_PRESENT = new AtomicBoolean(false);

    private static int tickCounter = 0;

    private static final Map<String, String> MOD_NAMES = Map.of(
            "ftbTeamsHash", "FTB Teams",
            "ftbLibraryHash", "FTB Library",
            "ftbEssentialsHash", "FTB Essentials",
            "ftbFilterHash", "FTB Filter",
            "ftbQuestsHash", "FTB Quests",
            "ftbXmodHash", "FTB XMod",
            "questAdditionsHash", "Quest Additions"
    );

    private AutoFtbModMover() {}

    public static void registerClientTicker() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ALL_MODS_PRESENT.get()) return;

            if (RESTART_REQUIRED.get()) {
                if (!(client.currentScreen instanceof RestartRequiredScreen)) {
                    client.setScreen(new RestartRequiredScreen());
                }
            }

            if (tickCounter++ >= TICK_INTERVAL) {
                tickCounter = 0;
                runBackgroundCheck();
            }
        });
    }

    private static void runBackgroundCheck() {
        Thread checkThread = new Thread(() -> {
            if (checkAndMoveMods()) {
                RESTART_REQUIRED.set(true);
            }
        }, "AutoFtbModMover-Check-Thread");
        checkThread.setDaemon(true);
        checkThread.start();
    }

    private static boolean checkAndMoveMods() {
        AutoFtbModsConfig config = AutoFtbModsConfig.get();
        if (!config.enableAutoFtbMods) {
            return false;
        }

        Map<String, String> targetHashes = getTargetHashes(config);
        if (targetHashes.isEmpty()) {
            return false;
        }

        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path modsDir = gameDir.resolve("mods");

        if (areAllModsPresent(modsDir, targetHashes)) {
            ALL_MODS_PRESENT.set(true);
            return false;
        }

        Path downloadsDir = getUserDownloadsDir();
        if (downloadsDir == null) {
            LOGGER.warn("Could not determine user Downloads directory.");
            return false;
        }

        return scanAndMoveMods(downloadsDir, modsDir, targetHashes);
    }

    private static Map<String, String> getTargetHashes(AutoFtbModsConfig config) {
        Map<String, String> hashes = new HashMap<>();
        addHashIfPresent(hashes, config.ftbTeamsHash, "ftbTeamsHash");
        addHashIfPresent(hashes, config.ftbLibraryHash, "ftbLibraryHash");
        addHashIfPresent(hashes, config.ftbEssentialsHash, "ftbEssentialsHash");
        addHashIfPresent(hashes, config.ftbFilterHash, "ftbFilterHash");
        addHashIfPresent(hashes, config.ftbQuestsHash, "ftbQuestsHash");
        addHashIfPresent(hashes, config.ftbXmodHash, "ftbXmodHash");
        addHashIfPresent(hashes, config.questAdditionsHash, "questAdditionsHash");
        return hashes;
    }

    private static void addHashIfPresent(Map<String, String> map, String hash, String key) {
        if (hash != null && !hash.isBlank()) {
            map.put(hash.toLowerCase(), MOD_NAMES.getOrDefault(key, key));
        }
    }

    private static boolean scanAndMoveMods(Path sourceDir, Path targetDir, Map<String, String> targetHashes) {
        boolean movedAny = false;
        try (Stream<Path> stream = Files.walk(sourceDir, 1)) {
            var jars = stream.filter(path -> path.toString().toLowerCase().endsWith(".jar")).toList();

            for (Path jarPath : jars) {
                if (processJar(jarPath, targetDir, targetHashes)) {
                    movedAny = true;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to scan directory: {}", sourceDir, e);
        }
        return movedAny;
    }

    private static boolean areAllModsPresent(Path modsDir, Map<String, String> targetHashes) {
        if (!Files.exists(modsDir)) return false;

        try (Stream<Path> stream = Files.list(modsDir)) {
            var modFiles = stream.filter(p -> p.toString().toLowerCase().endsWith(".jar")).toList();
            Map<String, String> remainingHashes = new HashMap<>(targetHashes);

            for (Path modFile : modFiles) {
                try {
                    String hash = calculateSha1(modFile);
                    remainingHashes.remove(hash);
                    if (remainingHashes.isEmpty()) return true;
                } catch (IOException | NoSuchAlgorithmException ignored) {}
            }
            return remainingHashes.isEmpty();
        } catch (IOException e) {
            LOGGER.error("Failed to list mods directory: {}", modsDir, e);
            return false;
        }
    }

    private static boolean processJar(Path jarPath, Path modsDir, Map<String, String> targetHashes) {
        try {
            String fileHash = calculateSha1(jarPath);
            if (targetHashes.containsKey(fileHash)) {
                String modName = targetHashes.get(fileHash);
                LOGGER.info("Found matching mod: {} (Hash: {}). Preparing to move...", jarPath.getFileName(), modName);

                Path targetPath = modsDir.resolve(jarPath.getFileName());

                if (Files.exists(targetPath)) {
                    String existingHash = calculateSha1(targetPath);
                    if (existingHash.equals(fileHash)) {
                        LOGGER.debug("Mod {} already exists in mods folder with matching hash. Skipping.", modName);
                        return false;
                    }
                }

                Files.move(jarPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Successfully moved {} to {}", jarPath.getFileName(), targetPath);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process jar file: {}", jarPath, e);
        }
        return false;
    }

    private static String calculateSha1(Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        try (InputStream fis = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static Path getUserDownloadsDir() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) return null;

        Path downloads = Paths.get(userHome, "Downloads");
        if (Files.exists(downloads) && Files.isDirectory(downloads)) {
            return downloads;
        }
        return null;
    }
}

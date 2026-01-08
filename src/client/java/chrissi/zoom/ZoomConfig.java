package chrissi.zoom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZoomConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("chrissi-zoom");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("chrissi-zoom.json");

    public boolean playZoomSound = false;
    public float defaultZoomLevel = 0.35f;
    public float smoothness = 0.15f;

    public static ZoomConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, ZoomConfig.class);
            } catch (Exception e) {
                LOGGER.warn("Config load failed, using defaults: {}", e.getMessage());
                return createDefault();
            }
        }
        return createDefault();
    }

    private static ZoomConfig createDefault() {
        ZoomConfig config = new ZoomConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("Config save failed: {}", e.getMessage());
        }
    }
}
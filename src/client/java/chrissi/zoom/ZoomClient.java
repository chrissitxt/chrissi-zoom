package chrissi.zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomClient implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-zoom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final float MIN_ZOOM_LEVEL = 0.01f;
    private static final float MAX_ZOOM_LEVEL = 0.75f;
    private static final float SCROLL_SENSITIVITY = 0.025f;

    private KeyMapping zoomKey;
    private static boolean isZooming = false;
    private static float targetZoomLevel = 0.35f;
    public static ZoomConfig config;

    @Override
    public void onInitializeClient() {
        config = ZoomConfig.load();

        // register keybind (default: C key)
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.chrissi-zoom.zoom",
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC
        ));

        // main tick loop - handles zoom while key is held
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean wasZooming = isZooming;
            isZooming = zoomKey.isDown();

            // reset zoom level when starting to zoom
            if (isZooming && !wasZooming) {
                targetZoomLevel = config.defaultZoomLevel;

                if (config.playZoomSound && client.player != null) {
                    client.player.playSound(
                            SoundEvents.SPYGLASS_USE.value(),
                            0.5f,
                            1.0f
                    );
                }
            }

            // play sound when stopping zoom
            if (!isZooming && wasZooming) {
                if (config.playZoomSound && client.player != null) {
                    client.player.playSound(
                            SoundEvents.SPYGLASS_STOP_USING.value(),
                            0.5f,
                            1.0f
                    );
                }
            }
        });
    }

    public static boolean isZooming() {
        return isZooming;
    }

    public static float getTargetZoomLevel() {
        return isZooming ? targetZoomLevel : 1.0f;
    }

    public static void adjustZoom(double scrollDelta) {
        if (!isZooming) return;

        targetZoomLevel += (float) scrollDelta * -SCROLL_SENSITIVITY;
        targetZoomLevel = Math.max(MIN_ZOOM_LEVEL, Math.min(MAX_ZOOM_LEVEL, targetZoomLevel));
    }
}
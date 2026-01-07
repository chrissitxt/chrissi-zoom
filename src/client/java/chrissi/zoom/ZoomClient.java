package chrissi.zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomClient implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-zoom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ZoomClient INSTANCE;

    private KeyMapping zoomKey;
    private static boolean isZooming = false;
    private static float targetZoomLevel = 0.25f;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

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
                targetZoomLevel = 0.25f;
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

        targetZoomLevel += (float) scrollDelta * -0.025f;
        targetZoomLevel = Math.max(0.05f, Math.min(0.5f, targetZoomLevel));
    }
}
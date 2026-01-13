package chrissi.zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.lwjgl.glfw.GLFW;

public class ZoomClient implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-zoom";

    private static final double DEFAULT_ZOOM_LEVEL = 3.0;
    private static final double MIN_ZOOM_LEVEL = 1.0;
    private static final double MAX_ZOOM_LEVEL = 50.0;
    private static final double ZOOM_SCROLL_MULTIPLIER = 1.1;

    private static ZoomClient instance;
    private KeyMapping zoomKey;
    private double currentZoomLevel = DEFAULT_ZOOM_LEVEL;
    private Double savedMouseSensitivity;

    @Override
    public void onInitializeClient() {
        instance = this;

        // register keybind (default: c key)
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.chrissi-zoom.zoom",
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC
        ));
    }

    public static ZoomClient getInstance() {
        return instance;
    }

    public float modifyFov(float originalFov) {
        OptionInstance<Double> mouseSensitivity = Minecraft.getInstance().options.sensitivity();

        if (!zoomKey.isDown()) {
            currentZoomLevel = DEFAULT_ZOOM_LEVEL;

            // restore original mouse sensitivity
            if (savedMouseSensitivity != null) {
                mouseSensitivity.set(savedMouseSensitivity);
                savedMouseSensitivity = null;
            }

            return originalFov;
        }

        // save original mouse sensitivity on first zoom activation
        if (savedMouseSensitivity == null) {
            savedMouseSensitivity = mouseSensitivity.get();
        }

        // adjust mouse sensitivity proportionally to zoom level
        mouseSensitivity.set(savedMouseSensitivity * (1.0 / currentZoomLevel));

        // apply zoom by dividing fov
        return (float) (originalFov / currentZoomLevel);
    }

    public void onMouseScroll(double scrollAmount) {
        if (!zoomKey.isDown()) {
            return;
        }

        // adjust zoom level by scroll amount
        if (scrollAmount > 0) {
            currentZoomLevel *= ZOOM_SCROLL_MULTIPLIER;
        } else if (scrollAmount < 0) {
            currentZoomLevel /= ZOOM_SCROLL_MULTIPLIER;
        }

        // clamp zoom level to valid range
        currentZoomLevel = Math.max(MIN_ZOOM_LEVEL, Math.min(MAX_ZOOM_LEVEL, currentZoomLevel));
    }

    public boolean isZooming() {
        return zoomKey.isDown();
    }
}

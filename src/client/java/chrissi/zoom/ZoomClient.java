package chrissi.zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomClient implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-zoom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Minecraft MC = Minecraft.getInstance();

    private static final double DEFAULT_ZOOM_LEVEL = 3.0;
    private static final double MIN_ZOOM_LEVEL = 1.0;
    private static final double MAX_ZOOM_LEVEL = 50.0;
    private static final double ZOOM_SCROLL_MULTIPLIER = 1.1;
    private static final float ZOOM_SMOOTHNESS = 0.15f;

    private static ZoomClient instance;
    private KeyMapping zoomKey;
    private double targetZoomLevel = DEFAULT_ZOOM_LEVEL;
    private double currentZoomLevel = 1.0;
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
        OptionInstance<Double> mouseSensitivity = MC.options.sensitivity();

        if (!zoomKey.isDown()) {
            // smoothly zoom out
            targetZoomLevel = DEFAULT_ZOOM_LEVEL;
            currentZoomLevel = lerp(currentZoomLevel, targetZoomLevel, ZOOM_SMOOTHNESS);

            // restore original mouse sensitivity
            if (savedMouseSensitivity != null) {
                mouseSensitivity.set(savedMouseSensitivity);
                savedMouseSensitivity = null;
            }

            // snap to 1.0 when close enough
            if (Math.abs(currentZoomLevel - 1.0) < 0.001) {
                currentZoomLevel = 1.0;
            }

            return originalFov;
        }

        // save original mouse sensitivity on first zoom activation
        if (savedMouseSensitivity == null) {
            savedMouseSensitivity = mouseSensitivity.get();
        }

        // smooth interpolation towards target zoom level
        currentZoomLevel = lerp(currentZoomLevel, targetZoomLevel, ZOOM_SMOOTHNESS);

        // snap to target when close enough
        if (Math.abs(currentZoomLevel - targetZoomLevel) < 0.001) {
            currentZoomLevel = targetZoomLevel;
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
            targetZoomLevel *= ZOOM_SCROLL_MULTIPLIER;
        } else if (scrollAmount < 0) {
            targetZoomLevel /= ZOOM_SCROLL_MULTIPLIER;
        }

        // clamp zoom level to valid range
        targetZoomLevel = Math.max(MIN_ZOOM_LEVEL, Math.min(MAX_ZOOM_LEVEL, targetZoomLevel));
    }

    public boolean isZooming() {
        return zoomKey.isDown();
    }

    private double lerp(double current, double target, float alpha) {
        return current + (target - current) * alpha;
    }
}

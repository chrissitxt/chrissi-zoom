package chrissi.zoom;

import net.minecraft.util.Mth;

public class ZoomHandler {
    private static final float SMOOTHNESS = 0.15f;
    private static final float SNAP_THRESHOLD = 0.001f;

    private static float currentZoomLevel = 1.0f;

    public static float getZoomLevel() {
        float targetZoom = ZoomClient.getTargetZoomLevel();

        currentZoomLevel = Mth.lerp(SMOOTHNESS, currentZoomLevel, targetZoom);

        if (Math.abs(currentZoomLevel - targetZoom) < SNAP_THRESHOLD) {
            currentZoomLevel = targetZoom;
        }

        return currentZoomLevel;
    }
}
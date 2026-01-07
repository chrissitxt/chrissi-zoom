package chrissi.zoom;

import net.minecraft.util.Mth;

public class ZoomHandler {
    private static float currentZoomLevel = 1.0f;

    public static float getZoomLevel() {
        float targetZoom = ZoomClient.getTargetZoomLevel();
        float smoothness = targetZoom < currentZoomLevel ? 0.15f : 0.25f;

        currentZoomLevel = Mth.lerp(smoothness, currentZoomLevel, targetZoom);

        if (Math.abs(currentZoomLevel - targetZoom) < 0.001f) {
            currentZoomLevel = targetZoom;
        }

        return currentZoomLevel;
    }
}
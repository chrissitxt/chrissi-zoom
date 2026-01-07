package chrissi.zoom.mixin;

import chrissi.zoom.ZoomHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyZoom(CallbackInfoReturnable<Float> cir) {
        if (this.minecraft.player != null) {
            float zoomLevel = ZoomHandler.getZoomLevel();
            if (zoomLevel < 1.0f) {
                cir.setReturnValue(cir.getReturnValue() * zoomLevel);
            }
        }
    }
}
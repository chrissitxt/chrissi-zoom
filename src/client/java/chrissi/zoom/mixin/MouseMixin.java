package chrissi.zoom.mixin;

import chrissi.zoom.ZoomClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    // handle scroll wheel while zooming
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomClient.getInstance().isZooming() && vertical != 0) {
            ZoomClient.getInstance().onMouseScroll(vertical);
            // cancel scroll event to prevent hotbar switching
            ci.cancel();
        }
    }
}

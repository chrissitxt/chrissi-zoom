package chrissi.zoom.mixin;

import chrissi.zoom.ZoomClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {
    private static final int SCROLL_COOLDOWN_TICKS = 2;

    @Shadow @Final private Minecraft minecraft;
    private int scrollCooldown = 0;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (this.minecraft.player != null && ZoomClient.isZooming()) {
            if (scrollCooldown <= 0 && vertical != 0) {
                ZoomClient.adjustZoom(vertical);
                scrollCooldown = SCROLL_COOLDOWN_TICKS;
            } else if (scrollCooldown > 0) {
                scrollCooldown--;
            }
            ci.cancel();
        }
    }
}
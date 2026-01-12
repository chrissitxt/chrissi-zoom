package chrissi.zoom.mixin;

import chrissi.zoom.ZoomClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // modify fov to apply zoom effect
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyZoom(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomClient.getInstance().modifyFov(cir.getReturnValue()));
    }
}

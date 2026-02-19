package chrissi.zoom.mixin;

import chrissi.zoom.ZoomClient;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// modify fov to apply zoom effect
@Mixin(Camera.class)
public class GameRendererMixin {

    @Inject(method = "calculateFov(F)F", at = @At("RETURN"), cancellable = true)
    private void applyZoom(float partialTick, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomClient.getInstance().modifyFov(cir.getReturnValue()));
    }
}

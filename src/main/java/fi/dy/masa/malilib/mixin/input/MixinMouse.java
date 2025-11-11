package fi.dy.masa.malilib.mixin.input;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.util.Window;

@Mixin(Mouse.class)
public abstract class MixinMouse
{
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Scroller scroller;

    @Inject(method = "onCursorPos",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;hasResolutionChanged:Z", ordinal = 0))
    private void malilib_hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
		Window clientWindow = this.client.getWindow();
		double mouseX = ((Mouse) (Object) this).getX() * (double) clientWindow.getScaledWidth() / (double) clientWindow.getWidth();
		double mouseY = ((Mouse) (Object) this).getY() * (double) clientWindow.getScaledHeight() / (double) clientWindow.getHeight();

        ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY, this.client);
    }

    @Inject(method = "onMouseScroll", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void malilib_hookOnMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci)
    {
		Window clientWindow = this.client.getWindow();
		double mouseX = ((Mouse) (Object) this).getX() * (double) clientWindow.getScaledWidth() / (double) clientWindow.getWidth();
		double mouseY = ((Mouse) (Object) this).getY() * (double) clientWindow.getScaledHeight() / (double) clientWindow.getHeight();

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, xOffset, yOffset, this.client))
        {
            this.scroller.update(0.0, 0.0);
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", cancellable = true,
            at = @At(value = "INVOKE",
					 target = "Lnet/minecraft/client/Mouse;modifyMouseInput(Lnet/minecraft/client/input/MouseInput;Z)Lnet/minecraft/client/input/MouseInput;"))
    private void malilib_hookOnMouseClick(long window, MouseInput input, int action, CallbackInfo ci)
    {
        Window clientWindow = this.client.getWindow();
        double mouseX = ((Mouse) (Object) this).getX() * (double) clientWindow.getScaledWidth() / (double) clientWindow.getWidth();
        double mouseY = ((Mouse) (Object) this).getY() * (double) clientWindow.getScaledHeight() / (double) clientWindow.getHeight();

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseClick(new Click(mouseX, mouseY, input), action, this.client))
        {
            ci.cancel();
        }
    }
}

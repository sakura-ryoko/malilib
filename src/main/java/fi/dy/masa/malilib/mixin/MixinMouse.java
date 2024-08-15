package fi.dy.masa.malilib.mixin;

import net.minecraft.class_9928;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;

import fi.dy.masa.malilib.event.InputEventHandler;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public abstract class MixinMouse
{
    @Shadow @Final private MinecraftClient client;
    //@Shadow private double eventDeltaHorizontalWheel;
    //@Shadow private double eventDeltaVerticalWheel;
    @Shadow @Final private class_9928 field_52757;

    @Inject(method = "onCursorPos",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;hasResolutionChanged:Z", ordinal = 0))
    private void hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
        Window window = this.client.getWindow();
        int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight() / (double) window.getHeight());

        ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY);
    }

    @Inject(method = "onMouseScroll", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void hookOnMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci)
    {
        Window window = this.client.getWindow();
        int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight() / (double) window.getHeight());

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, xOffset, yOffset))
        {
            //this.eventDeltaHorizontalWheel = 0.0; --> e
            //this.eventDeltaVerticalWheel = 0.0; --> f
            //this.field_52764 = 0.0;
            //this.field_52765 = 0.0;
            this.field_52757.method_61971(0.0, 0.0);
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;IS_SYSTEM_MAC:Z", ordinal = 0))
    private void hookOnMouseClick(long handle, final int button, final int action, int mods, CallbackInfo ci)
    {
        Window window = this.client.getWindow();
        int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight() / (double) window.getHeight());

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseClick(mouseX, mouseY, button, action))
        {
            ci.cancel();
        }
    }
}

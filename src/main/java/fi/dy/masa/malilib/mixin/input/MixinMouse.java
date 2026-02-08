package fi.dy.masa.malilib.mixin.input;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.platform.Window;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

@Mixin(value = MouseHandler.class, priority = 990)
public abstract class MixinMouse
{
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ScrollWheelHandler scrollWheelHandler;

    @Inject(method = "onMove",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ignoreFirstMove:Z",
                     ordinal = 0,
                     opcode = Opcodes.GETFIELD))
    private void malilib_hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
		Window clientWindow = this.minecraft.getWindow();
		double mouseX = ((MouseHandler) (Object) this).xpos() * (double) clientWindow.getGuiScaledWidth() / (double) clientWindow.getScreenWidth();
		double mouseY = ((MouseHandler) (Object) this).ypos() * (double) clientWindow.getGuiScaledHeight() / (double) clientWindow.getScreenHeight();

        ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY, this.minecraft);
    }

    @Inject(method = "onScroll", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
                    ordinal = 0,
                     shift = At.Shift.AFTER))
    private void malilib_hookOnMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci)
    {
		Window clientWindow = this.minecraft.getWindow();
		double mouseX = ((MouseHandler) (Object) this).xpos() * (double) clientWindow.getGuiScaledWidth() / (double) clientWindow.getScreenWidth();
		double mouseY = ((MouseHandler) (Object) this).ypos() * (double) clientWindow.getGuiScaledHeight() / (double) clientWindow.getScreenHeight();

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, xOffset, yOffset, this.minecraft))
        {
            this.scrollWheelHandler.onMouseScroll(0.0, 0.0);
            ci.cancel();
        }
    }

    @Inject(method = "onButton", cancellable = true,
            at = @At(value = "INVOKE",
					 target = "Lnet/minecraft/client/MouseHandler;simulateRightClick(Lnet/minecraft/client/input/MouseButtonInfo;Z)Lnet/minecraft/client/input/MouseButtonInfo;"))
    private void malilib_hookOnMouseClick(long window, MouseButtonInfo input, int action, CallbackInfo ci)
    {
        Window clientWindow = this.minecraft.getWindow();
        double mouseX = ((MouseHandler) (Object) this).xpos() * (double) clientWindow.getGuiScaledWidth() / (double) clientWindow.getScreenWidth();
        double mouseY = ((MouseHandler) (Object) this).ypos() * (double) clientWindow.getGuiScaledHeight() / (double) clientWindow.getScreenHeight();

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseClick(new MouseButtonEvent(mouseX, mouseY, input), action, this.minecraft))
        {
            ci.cancel();
        }
    }
}

package fi.dy.masa.malilib.mixin.input;

import java.util.List;
import org.objectweb.asm.Opcodes;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.InputEventHandler;

@Mixin(value = MouseHandler.class, priority = 500)
public abstract class MixinMouse
{
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ScrollWheelHandler scrollWheelHandler;
	@Shadow public abstract double xpos();
	@Shadow public abstract double ypos();

	@Inject(method = "onMove",
	        at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ignoreFirstMove:Z",
                     ordinal = 0,
                     opcode = Opcodes.GETFIELD))
    private void malilib_hookOnMouseMove(long handle, double xpos, double ypos, double xrel, double yrel, CallbackInfo ci)
    {
		Window window = this.minecraft.getWindow();
		double mouseX = (this.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
		double mouseY = (this.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());

        ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY);
    }

    @Inject(method = "onScroll", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;overlay()Lnet/minecraft/client/gui/screens/Overlay;",
                    ordinal = 0,
                     shift = At.Shift.AFTER))
    private void malilib_hookOnMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo ci)
    {
		Window window = this.minecraft.getWindow();
		double mouseX = (this.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
		double mouseY = (this.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, xoffset, yoffset))
        {
            this.scrollWheelHandler.onMouseScroll(0.0, 0.0);
            ci.cancel();
        }
    }

	@Inject(method = "onButton", cancellable = true,
	        at = @At(value = "INVOKE",
	                 target = "Lcom/mojang/blaze3d/platform/FramerateLimitTracker;onInputReceived()V",
	                 shift = At.Shift.AFTER
	        )
	)
	private void malilib_hookOnMouseClick(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci)
	{
		Window window = this.minecraft.getWindow();
		double mouseX = (this.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
		double mouseY = (this.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());

		if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseClick(new MouseButtonEvent(mouseX, mouseY, rawButtonInfo), action))
		{
			ci.cancel();
		}
	}

	@Inject(method = "onDrop", at = @At("HEAD"), cancellable = true)
	private void malilib_hookOnMouseDrop(long handle, List<String> rawPaths, CallbackInfo ci)
	{
		Window window = this.minecraft.getWindow();
		double mouseX = (this.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
		double mouseY = (this.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());

		if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseDrop(mouseX, mouseY, rawPaths))
		{
			ci.cancel();
		}
	}
}

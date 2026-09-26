package fi.dy.masa.malilib.mixin.client;

import org.lwjgl.sdl.SDL_Event;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.InputEventHandler;

@Mixin(Window.class)
public abstract class MixinWindow
{
	@Inject(method = "handleEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleWindowEvent(final SDL_Event event, final CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}
}

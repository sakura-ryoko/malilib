package fi.dy.masa.malilib.mixin.input;

import org.lwjgl.sdl.SDL_Event;

import com.mojang.blaze3d.platform.SDLEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.InputEventHandler;

@Mixin(SDLEventHandler.class)
public class MixinSDLEventHandler
{
	@Inject(method = "handleKeyEvent", at = @At("HEAD"))
	private void malilib_onHandleKeyEvent(SDL_Event event, CallbackInfo ci)
	{
		((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
	}

	@Inject(method = "handleMouseButtonEvent", at = @At("HEAD"))
	private void malilib_onHandleMouseButtonEvent(SDL_Event event, CallbackInfo ci)
	{
		((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
	}
}

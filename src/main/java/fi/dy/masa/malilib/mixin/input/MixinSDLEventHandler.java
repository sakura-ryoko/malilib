package fi.dy.masa.malilib.mixin.input;

import org.lwjgl.sdl.SDL_Event;

import com.mojang.blaze3d.platform.SDLEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.InputEventHandler;

@Mixin(value = SDLEventHandler.class, priority = 500)
public class MixinSDLEventHandler
{
	@Inject(method = "handleKeymapChangedEvent", at = @At("HEAD"))
	private void malilib_onHandleKeymapChangedEvent(CallbackInfo ci)
	{
		((InputEventHandler) InputEventHandler.getInputManager()).onHandleKeymapChange();
	}

	@Inject(method = "handleKeyEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleKeyEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleTextInputEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleTextInputEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleTextEditingEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleTextEditingEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleMouseMotionEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleMouseMotionEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleMouseButtonEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleMouseButtonEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleDropFileEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleDropFileEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleDropBeginEvent", at = @At("HEAD"))
	private void malilib_onHandleDropBeginEvent(CallbackInfo ci)
	{
		((InputEventHandler) InputEventHandler.getInputManager()).onHandleDropStart();
	}

	@Inject(method = "handleMouseWheelEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleMouseWheelEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}

	@Inject(method = "handleDropCompleteEvent", at = @At("HEAD"), cancellable = true)
	private void malilib_onHandleDropCompleteEvent(SDL_Event event, CallbackInfo ci)
	{
		final boolean cancel = ((InputEventHandler) InputEventHandler.getInputManager()).onHandleEvent(event);
		if (cancel) { ci.cancel(); }
	}
}

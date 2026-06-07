package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(LevelExtractor.class)
public abstract class MixinLevelExtractor
{
	@Shadow @Final private LevelRenderer levelRenderer;

	@Inject(method = "extract",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 6,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onExtractWorldPreWeather(DeltaTracker deltaTracker, Camera camera,
	                                              float deltaPartialTick, CallbackInfo ci,
	                                              @Local(name = "profiler") ProfilerFiller profiler)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldPreWeather(deltaTracker, camera, deltaPartialTick, profiler);
	}

	@Inject(method = "extract",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 11,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onExtractWorldLast(DeltaTracker deltaTracker, Camera camera,
	                                        float deltaPartialTick, CallbackInfo ci,
	                                        @Local(name = "profiler") ProfilerFiller profiler)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldLast(deltaTracker, camera, deltaPartialTick, profiler);
	}
}

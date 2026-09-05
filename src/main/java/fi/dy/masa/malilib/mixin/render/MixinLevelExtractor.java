package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.SectionUpdateTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(LevelExtractor.class)
public abstract class MixinLevelExtractor
{
	@Shadow private @Nullable SectionUpdateTracker sectionUpdateTracker;
	@Shadow private @Nullable ClientLevel level;

	@Unique
	private boolean preMainExtracted = false;

	@Inject(method = "extract",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 1,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onExtractWorldPreMain1(DeltaTracker deltaTracker, Camera camera,
	                                           float deltaPartialTick, CallbackInfo ci,
	                                           @Local(name = "profiler") ProfilerFiller profiler)
	{
		if (this.sectionUpdateTracker != null && this.level != null)
		{
			((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldPreMain(deltaTracker, camera, deltaPartialTick, profiler);
			this.preMainExtracted = true;
		}
	}

	// This one calls "before entities" -- when the world is not rendering
	@Inject(method = "extract",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 2,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onExtractWorldPreMain2(DeltaTracker deltaTracker, Camera camera,
	                                           float deltaPartialTick, CallbackInfo ci,
	                                           @Local(name = "profiler") ProfilerFiller profiler)
	{
		if ((this.sectionUpdateTracker == null || this.level == null) && !this.preMainExtracted)
		{
			((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldPreMain(deltaTracker, camera, deltaPartialTick, profiler);
		}

		if (this.preMainExtracted)
		{
			this.preMainExtracted = false;
		}
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

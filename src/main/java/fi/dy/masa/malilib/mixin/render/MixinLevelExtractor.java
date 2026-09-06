package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelExtractor.class)
public abstract class MixinLevelExtractor
{
	@Shadow private @Nullable ClientLevel level;

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
		if (this.level != null)
		{
			((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldLast(deltaTracker, camera, deltaPartialTick, profiler);
		}
	}
}

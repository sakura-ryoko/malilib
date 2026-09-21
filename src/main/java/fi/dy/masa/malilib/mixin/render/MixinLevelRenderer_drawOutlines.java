package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.compat.iris.IrisCompat;
import fi.dy.masa.malilib.event.RenderEventHandler;

//@Restriction(conflict = @Condition(value = ModIds.sodium))
@Mixin(value = LevelRenderer.class)
public abstract class MixinLevelRenderer_drawOutlines
{
	@Inject(method = "executeOutline", at = @At("TAIL"))
	private void malilib_onDrawOutlines(FeatureRenderDispatcher.PreparedFrame featureFrame, CallbackInfo ci)
	{
		if (!IrisCompat.isShaderActive())
		{
			((RenderEventHandler) RenderEventHandler.getInstance()).runWorldLayerGroups();
		}
	}
}

package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.client.render.RenderTickCounter;

public interface IWorldRenderer
{
	@Nullable
	RenderTickCounter malilib_getDeltaTracker();
}

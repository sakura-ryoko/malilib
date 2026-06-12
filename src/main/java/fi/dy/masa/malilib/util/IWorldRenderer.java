package fi.dy.masa.malilib.util;

import net.minecraft.client.DeltaTracker;

import javax.annotation.Nullable;

// Compat for OnDemandRenderer
public interface IWorldRenderer
{
	@Nullable
	DeltaTracker malilib_getDeltaTracker();
}

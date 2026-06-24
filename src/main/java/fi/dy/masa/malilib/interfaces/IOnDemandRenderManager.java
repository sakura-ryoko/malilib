package fi.dy.masa.malilib.interfaces;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface IOnDemandRenderManager
{
	<T extends IOnDemandRenderState> void registerOnDemandRenderer(String key, IOnDemandRenderer<T> renderer) throws RuntimeException;
}

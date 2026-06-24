package fi.dy.masa.malilib;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;

import fi.dy.masa.malilib.config.IConfigBoolean;

public class MaLiLibCallbacks
{
	public static void init()
	{
//		MaLiLibConfigs.Generic.RENDER_TRANSPARENCY_FIX.setValueChangeCallback(MaLiLibCallbacks::RenderFixCallback);
	}

	public static void RenderFixCallback(IConfigBoolean config)
	{
		if (config.getBooleanValue())
		{
			MinecraftClient.getInstance().options.getGraphicsMode().setValue(GraphicsMode.FANCY);
//			MinecraftClient.getInstance().worldRenderer.reload();
		}
	}
}

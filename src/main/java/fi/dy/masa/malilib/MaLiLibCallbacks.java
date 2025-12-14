package fi.dy.masa.malilib;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.config.IConfigBoolean;

public class MaLiLibCallbacks
{
	public static void init()
	{
		MaLiLibConfigs.Generic.RENDER_TRANSPARENCY_FIX.setValueChangeCallback(MaLiLibCallbacks::RenderFixCallback);
	}

	public static void RenderFixCallback(IConfigBoolean config)
	{
		if (config.getBooleanValue())
		{
			Minecraft.getInstance().options.improvedTransparency().set(false);
		}
	}
}

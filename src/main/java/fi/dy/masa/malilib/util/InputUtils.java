package fi.dy.masa.malilib.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.util.game.wrap.GameWrap;

public class InputUtils
{
    public static int getMouseX()
    {
        Minecraft mc = GameWrap.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
    }

    public static int getMouseY()
    {
        Minecraft mc = GameWrap.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
    }

	public static double getMouseXDirect()
	{
		return GameWrap.getClient().mouseHandler.xpos();
	}

	public static double getMouseYDirect()
	{
		return GameWrap.getClient().mouseHandler.ypos();
	}

	public static double getMouseXScaled()
	{
		Minecraft mc = GameWrap.getClient();
		Window window = mc.getWindow();
		return (mc.mouseHandler.xpos() * ((double) window.getGuiScaledWidth() / window.getScreenWidth()));
	}

	public static double getMouseYScaled()
	{
		Minecraft mc = GameWrap.getClient();
		Window window = mc.getWindow();
		return (mc.mouseHandler.ypos() * ((double) window.getGuiScaledHeight() / window.getScreenHeight()));
	}

	public static InputConstants.Key getDefaultKey(KeyMapping key)
	{
		return key.defaultKey;
	}

	public static InputConstants.Key getBoundKey(KeyMapping key)
	{
		return key.key;
	}

	public static KeyMapping.Category getCategory(KeyMapping key)
	{
		return key.category;
	}

	public static boolean isBound(KeyMapping key)
	{
		return key.key != null && !key.key.equals(InputConstants.UNKNOWN);
	}
}

package fi.dy.masa.malilib.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.mixin.input.IMixinKeyBinding;

public class InputUtils
{
    public static int getMouseX()
    {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
    }

    public static int getMouseY()
    {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
    }

	public static double getMouseXDirect()
	{
		return Minecraft.getInstance().mouseHandler.xpos();
	}

	public static double getMouseYDirect()
	{
		return Minecraft.getInstance().mouseHandler.ypos();
	}

	public static double getMouseXScaled()
	{
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		return (mc.mouseHandler.xpos() * ((double) window.getGuiScaledWidth() / window.getScreenWidth()));
	}

	public static double getMouseYScaled()
	{
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		return (mc.mouseHandler.ypos() * ((double) window.getGuiScaledHeight() / window.getScreenHeight()));
	}

	public static InputConstants.Key getDefaultKey(KeyMapping key)
	{
		return ((IMixinKeyBinding) key).malilib$getDefaultKey();
	}

	public static InputConstants.Key getBoundKey(KeyMapping key)
	{
		return ((IMixinKeyBinding) key).malilib$getBoundKey();
	}

	public static KeyMapping.Category getCategory(KeyMapping key)
	{
		return ((IMixinKeyBinding) key).malilib$getCategory();
	}

	public static boolean isBound(KeyMapping key)
	{
		return ((IMixinKeyBinding) key).malilib$getBoundKey() != null && !((IMixinKeyBinding) key).malilib$getBoundKey().equals(InputConstants.UNKNOWN);
	}

	public static void bindKey(KeyMapping key, InputConstants.Key binding)
	{
		key.setKey(binding);
		KeyMapping.resetMapping();
	}

	public static void resetKeybind(KeyMapping key)
	{
		key.setKey(((IMixinKeyBinding) key).malilib$getDefaultKey());
		KeyMapping.resetMapping();
	}
}

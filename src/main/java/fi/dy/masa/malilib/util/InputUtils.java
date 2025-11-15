package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.mixin.input.IMixinKeyBinding;
import fi.dy.masa.malilib.util.game.wrap.GameWrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;

public class InputUtils
{
    public static int getMouseX()
    {
        MinecraftClient mc = GameWrap.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
    }

    public static int getMouseY()
    {
        MinecraftClient mc = GameWrap.getClient();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());
    }

	public static double getMouseXDirect()
	{
		return GameWrap.getClient().mouse.getX();
	}

	public static double getMouseYDirect()
	{
		return GameWrap.getClient().mouse.getY();
	}

	public static double getMouseXScaled()
	{
		MinecraftClient mc = GameWrap.getClient();
		Window window = mc.getWindow();
		return (mc.mouse.getX() * ((double) window.getScaledWidth() / window.getWidth()));
	}

	public static double getMouseYScaled()
	{
		MinecraftClient mc = GameWrap.getClient();
		Window window = mc.getWindow();
		return (mc.mouse.getY() * ((double) window.getScaledHeight() / window.getHeight()));
	}

	public static InputUtil.Key getDefaultKey(KeyBinding key)
	{
		return ((IMixinKeyBinding) key).malilib$getDefaultKey();
	}

	public static InputUtil.Key getBoundKey(KeyBinding key)
	{
		return ((IMixinKeyBinding) key).malilib$getBoundKey();
	}

	public static KeyBinding.Category getCategory(KeyBinding key)
	{
		return ((IMixinKeyBinding) key).malilib$getCategory();
	}

	public static boolean isBound(KeyBinding key)
	{
		return ((IMixinKeyBinding) key).malilib$getBoundKey() != null && !((IMixinKeyBinding) key).malilib$getBoundKey().equals(InputUtil.UNKNOWN_KEY);
	}

	public static void bindKey(KeyBinding key, InputUtil.Key binding)
	{
		key.setBoundKey(binding);
		KeyBinding.updateKeysByCode();
	}

	public static void resetKeybind(KeyBinding key)
	{
		key.setBoundKey(((IMixinKeyBinding) key).malilib$getDefaultKey());
		KeyBinding.updateKeysByCode();
	}
}

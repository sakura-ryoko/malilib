package fi.dy.masa.malilib.input.gamepad;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.StringUtils;

public enum GamepadType
{
	UNKNOWN             ("unknown"),
	STANDARD            ("standard"),
	XBOX_360            ("xbox_360"),
	XBOX_ONE            ("xbox_one"),
	PS_3                ("ps_3"),
	PS_4                ("ps_4"),
	PS_5                ("ps_5"),
	SWITCH_PRO          ("switch_pro"),
	SWITCH_JOY_LEFT     ("switch_joy_left"),
	SWITCH_JOY_RIGHT    ("switch_joy_right"),
	SWITCH_JOY_PAIR     ("switch_joy_pair"),
	GAME_CUBE           ("game_cube"),
	COUNT               ("count"),
	;
	
	private final String name;
	private final String translationKey;

	GamepadType(final String name)
	{
		this.name = name;
		this.translationKey = MaLiLibReference.MOD_ID + ".name.gamepad_type." + name;
	}
	
	public String getName()
	{
		return this.name;
	}

	public String getTranslationKey()
	{
		return this.translationKey;
	}
	
	public String getDisplayName()
	{
		return StringUtils.translate(this.translationKey);
	}
	
	@Nullable
	public static GamepadType fromTypeCode(final int type)
	{
		return switch (type)
		{
			case GamepadCodes.TYPE_UNKNOWN -> GamepadType.UNKNOWN;
			case GamepadCodes.TYPE_STANDARD -> GamepadType.STANDARD;
			case GamepadCodes.TYPE_XBOX_360 -> GamepadType.XBOX_360;
			case GamepadCodes.TYPE_XBOX_ONE -> GamepadType.XBOX_ONE;
			case GamepadCodes.TYPE_PS_3 -> GamepadType.PS_3;
			case GamepadCodes.TYPE_PS_4 -> GamepadType.PS_4;
			case GamepadCodes.TYPE_PS_5 -> GamepadType.PS_5;
			case GamepadCodes.TYPE_SWITCH_PRO -> GamepadType.SWITCH_PRO;
			case GamepadCodes.TYPE_SWITCH_JOY_LEFT -> GamepadType.SWITCH_JOY_LEFT;
			case GamepadCodes.TYPE_SWITCH_JOY_RIGHT -> GamepadType.SWITCH_JOY_RIGHT;
			case GamepadCodes.TYPE_SWITCH_JOY_PAIR -> GamepadType.SWITCH_JOY_PAIR;
			case GamepadCodes.TYPE_GAME_CUBE -> GamepadType.GAME_CUBE;
			case GamepadCodes.TYPE_COUNT -> GamepadType.COUNT;
			default -> null;
		};
	}
}

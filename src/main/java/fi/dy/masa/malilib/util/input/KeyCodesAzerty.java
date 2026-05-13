package fi.dy.masa.malilib.util.input;

import javax.annotation.Nullable;

public enum KeyCodesAzerty
{
	KEY_U_GRAVE             (39,  40,  "U_GRAVE", "APOSTROPHE"),
	KEY_SEMICOLON           (44,  51,  "SEMICOLON", "COMMA"),
	KEY_RIGHT_PARENTHESIS   (45,  12,  "RIGHT_PARENTHESIS", "MINUS"),
	KEY_COLON               (46,  52,  "COLON", "PERIOD"),
	KEY_EXCLAMATION_MARK    (47,  53,  "EXCLAMATION_MARK", "SLASH"),
	KEY_M                   (59,  39,  "M", "SEMICOLON"),
	KEY_Q                   (65,  30,  "Q", "A"),
	KEY_COMMA               (77,  50,  "COMMA", "M"),
	KEY_A                   (81,  16,  "A", "Q"),
	KEY_Z                   (87,  17,  "Z", "W"),
	KEY_W                   (90,  44,  "W", "Z"),
	KEY_CIRCUMFLEX_ACCENT   (91,  26,  "CIRCUMFLEX_ACCENT", "LEFT_BRACKET"),
	KEY_ASTERISK            (92,  43,  "ASTERISK", "BACKSLASH"),
	KEY_DOLLAR_SIGN         (93,  27,  "DOLLAR_SIGN", "RIGHT_BRACKET"),
	KEY_SUPERSCRIPT_2       (96,  41,  "SUPERSCRIPT_2", "GRAVE_ACCENT"),
	KEY_ALT_GR              (346, 312, "ALT_GR", "RIGHT_ALT"),
	;

	private final int keyCode;
	private final int scanCode;
	private final String name;
	private final String qwertyName;

	KeyCodesAzerty(int keyCode, int scanCode, String name, String qwertyName)
	{
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.name = name;
		this.qwertyName = qwertyName;
	}

	public int keyCode()
	{
		return this.keyCode;
	}

	public int scanCode()
	{
		return this.scanCode;
	}

	public String getName()
	{
		return this.name;
	}

	public String getQwertyName()
	{
		return this.qwertyName;
	}

	@Nullable
	public static KeyCodesAzerty fromKeyCode(int keyCode)
	{
		for (KeyCodesAzerty k : KeyCodesAzerty.values())
		{
			if (k.keyCode == keyCode)
			{
				return k;
			}
		}

		return null;
	}

	@Nullable
	public static KeyCodesAzerty fromScanCode(int scanCode)
	{
		for (KeyCodesAzerty k : KeyCodesAzerty.values())
		{
			if (k.keyCode == scanCode)
			{
				return k;
			}
		}

		return null;
	}

	@Nullable
	public static KeyCodesAzerty fromName(String name)
	{
		for (KeyCodesAzerty k : KeyCodesAzerty.values())
		{
			if (k.name.equalsIgnoreCase(name))
			{
				return k;
			}
		}

		return null;
	}

	@Nullable
	public static KeyCodesAzerty fromQwertyName(String qwertyName)
	{
		for (KeyCodesAzerty k : KeyCodesAzerty.values())
		{
			if (k.qwertyName.equalsIgnoreCase(qwertyName))
			{
				return k;
			}
		}

		return null;
	}
}

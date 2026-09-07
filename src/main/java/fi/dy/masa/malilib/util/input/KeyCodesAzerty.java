package fi.dy.masa.malilib.util.input;

import javax.annotation.Nullable;

public enum KeyCodesAzerty
{
	KEY_U_GRAVE             (39,  52,  "U_GRAVE", "APOSTROPHE"),
	KEY_SEMICOLON           (44,  54,  "SEMICOLON", "COMMA"),
	KEY_RIGHT_PAREN         (45,  45,  "RIGHT_PAREN", "MINUS"),
	KEY_COLON               (46,  55,  "COLON", "PERIOD"),
	KEY_EXCLAIM             (47,  56,  "EXCLAIM", "SLASH"),
	KEY_M                   (59,  51,  "M", "SEMICOLON"),
	KEY_Q                   (97,   4,  "Q", "A"),
	KEY_COMMA               (109, 16,  "COMMA", "M"),
	KEY_A                   (113, 20,  "A", "Q"),
	KEY_Z                   (119, 26,  "Z", "W"),
	KEY_W                   (122, 29,  "W", "Z"),
	KEY_CIRCUMFLEX          (91,  47,  "CIRCUMFLEX", "LEFT_BRACKET"),
	KEY_ASTERISK            (92,  49,  "ASTERISK", "BACKSLASH"),
	KEY_DOLLAR              (93,  48,  "DOLLAR", "RIGHT_BRACKET"),
	KEY_SUPERSCRIPT_TWO     (96,  43,  "SUPERSCRIPT_TWO", "GRAVE"),
	KEY_ALT_GR              (1073742054, 230, "ALT_GR", "RIGHT_ALT"),
	KEY_MENU                (1073741925, 101, "MENU", "APPLICATION"),
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
			if (k.keyCode() == keyCode)
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
			if (k.scanCode() == scanCode)
			{
				return k;
			}
		}

		return null;
	}

	@Nullable
	public static KeyCodesAzerty fromName(String name)
	{
		switch (name)
		{
			case "RIGHT_PARENTHESIS" ->
			{
				return KeyCodesAzerty.KEY_RIGHT_PAREN;
			}
			case "EXCLAMATION_MARK" ->
			{
				return KeyCodesAzerty.KEY_EXCLAIM;
			}
			case "CIRCUMFLEX_ACCENT" ->
			{
				return KeyCodesAzerty.KEY_CIRCUMFLEX;
			}
			case "DOLLAR_SIGN" ->
			{
				return KeyCodesAzerty.KEY_DOLLAR;
			}
			case "SUPERSCRIPT_2" ->
			{
				return KeyCodesAzerty.KEY_SUPERSCRIPT_TWO;
			}
		}

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
		if (qwertyName.equals("GRAVE_ACCENT"))
		{
			return KeyCodesAzerty.KEY_SUPERSCRIPT_TWO;
		}

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

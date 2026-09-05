package fi.dy.masa.malilib.util.input;

import javax.annotation.Nullable;

public enum KeyCodesAzerty
{
	KEY_U_GRAVE             (KeyCodes.KEY_APOSTROPHE,       ScanCodes.SCAN_APOSTROPHE,      "U_GRAVE",          "APOSTROPHE"),
	KEY_SEMICOLON           (KeyCodes.KEY_COMMA,            ScanCodes.SCAN_COMMA,           "SEMICOLON",        "COMMA"),
	KEY_RIGHT_PARENTHESIS   (KeyCodes.KEY_MINUS,            ScanCodes.SCAN_MINUS,           "RIGHT_PARENTHESIS","MINUS"),
	KEY_COLON               (KeyCodes.KEY_PERIOD,           ScanCodes.SCAN_PERIOD,          "COLON",            "PERIOD"),
	KEY_EXCLAMATION_MARK    (KeyCodes.KEY_SLASH,            ScanCodes.SCAN_SLASH,           "EXCLAMATION_MARK", "SLASH"),
	KEY_M                   (KeyCodes.KEY_SEMICOLON,        ScanCodes.SCAN_SEMICOLON,       "M",                "SEMICOLON"),
	KEY_Q                   (KeyCodes.KEY_A,                ScanCodes.SCAN_A,               "Q",                "A"),
	KEY_COMMA               (KeyCodes.KEY_M,                ScanCodes.SCAN_M,               "COMMA",            "M"),
	KEY_A                   (KeyCodes.KEY_Q,                ScanCodes.SCAN_Q,               "A",                "Q"),
	KEY_Z                   (KeyCodes.KEY_W,                ScanCodes.SCAN_W,               "Z",                "W"),
	KEY_W                   (KeyCodes.KEY_Z,                ScanCodes.SCAN_Z,               "W",                "Z"),
	KEY_CIRCUMFLEX_ACCENT   (KeyCodes.KEY_LEFT_BRACKET,     ScanCodes.SCAN_LEFT_BRACKET,    "CIRCUMFLEX_ACCENT","LEFT_BRACKET"),
	KEY_ASTERISK            (KeyCodes.KEY_BACKSLASH,        ScanCodes.SCAN_BACKSLASH,       "ASTERISK",         "BACKSLASH"),
	KEY_DOLLAR_SIGN         (KeyCodes.KEY_RIGHT_BRACKET,    ScanCodes.SCAN_RIGHT_BRACKET,   "DOLLAR_SIGN",      "RIGHT_BRACKET"),
	KEY_SUPERSCRIPT_2       (KeyCodes.KEY_GRAVE_ACCENT,     ScanCodes.SCAN_GRAVE_ACCENT,    "SUPERSCRIPT_2",    "GRAVE_ACCENT"),
	KEY_ALT_GR              (KeyCodes.KEY_RIGHT_ALT,        ScanCodes.SCAN_RIGHT_ALT,       "ALT_GR",           "RIGHT_ALT"),
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

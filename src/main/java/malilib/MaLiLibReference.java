package malilib;

import malilib.util.StringUtils;
import malilib.util.data.ModInfo;

public class MaLiLibReference
{
    public static final String MOD_ID = "malilib";
    public static final String MOD_NAME = "MaLiLib";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);

    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, MOD_NAME);
}

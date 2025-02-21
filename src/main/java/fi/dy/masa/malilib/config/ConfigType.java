package fi.dy.masa.malilib.config;

import net.minecraft.util.StringIdentifiable;

public enum ConfigType implements StringIdentifiable
{
    BOOLEAN,
    INTEGER,
    DOUBLE,
    FLOAT,
    COLOR,
    STRING,
    STRING_LIST,
    LOCKED_LIST,
    COLOR_LIST,
    OPTION_LIST,
    HOTKEY
    ;

    @Override
    public String asString()
    {
        return name().toLowerCase();
    }

    public static final StringIdentifiable.EnumCodec<ConfigType> CODEC = StringIdentifiable.createCodec(ConfigType::values);
}

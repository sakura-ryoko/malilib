package fi.dy.masa.malilib.config;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
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

    public static ConfigType fromString(String entry)
    {
        for (ConfigType type : values())
        {
            if (type.name().equalsIgnoreCase(entry))
            {
                return type;
            }
        }

        return null;
    }

    public static final StringIdentifiable.EnumCodec<ConfigType> CODEC = StringIdentifiable.createCodec(ConfigType::values);
    public static final PacketCodec<ByteBuf, ConfigType> PACKET_CODEC = PacketCodecs.STRING.xmap(ConfigType::fromString, ConfigType::asString);
}

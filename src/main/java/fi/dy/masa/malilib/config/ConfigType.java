package fi.dy.masa.malilib.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.config.options.table.ConfigTable;

public enum ConfigType implements StringIdentifiable
{
    BOOLEAN     ("boolean",         ConfigBoolean.CODEC),
    INTEGER     ("integer",         ConfigInteger.CODEC),
    DOUBLE      ("double",          ConfigDouble.CODEC),
    FLOAT       ("float",           ConfigFloat.CODEC),
    COLOR       ("color",           ConfigColor.CODEC),
    STRING      ("string",          ConfigString.CODEC),
    STRING_LIST ("string_list",     ConfigString.CODEC),
    LOCKED_LIST ("locked_list",     null),
    COLOR_LIST  ("color_list",      ConfigColorList.CODEC),
    OPTION_LIST ("option_list",     null),
    HOTKEY      ("hotkey",          ConfigHotkey.CODEC),
    TABLE       ("table",           ConfigTable.CODEC),
    ;

    public static final StringIdentifiable.EnumCodec<ConfigType> CODEC = StringIdentifiable.createCodec(ConfigType::values);
    public static final PacketCodec<ByteBuf, ConfigType> PACKET_CODEC = PacketCodecs.STRING.xmap(ConfigType::fromString, ConfigType::asString);

    private final String name;
    private final Codec<? extends IConfigBase> codec;

    ConfigType(String name, Codec<? extends IConfigBase> codec)
    {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public @Nonnull String asString()
    {
        return this.name;
    }

    public @Nullable Codec<? extends IConfigBase> codec()
    {
        return this.codec;
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
}

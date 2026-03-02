package fi.dy.masa.malilib.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.config.options.table.ConfigTable;

public enum ConfigType implements StringRepresentable
{
    BOOLEAN     ("boolean",         ConfigBoolean.CODEC),
    INTEGER     ("integer",         ConfigInteger.CODEC),
    DOUBLE      ("double",          ConfigDouble.CODEC),
    FLOAT       ("float",           ConfigFloat.CODEC),
    COLOR       ("color",           ConfigColor.CODEC),
    BLOCK_STATE ("block_state",     ConfigBlockState.CODEC),
    STRING      ("string",          ConfigString.CODEC),
    STRING_LIST ("string_list",     ConfigString.CODEC),
    LOCKED_LIST ("locked_list",     null),
    COLOR_LIST  ("color_list",      ConfigColorList.CODEC),
    OPTION_LIST ("option_list",     null),
    HOTKEY      ("hotkey",          ConfigHotkey.CODEC),
	TABLE       ("table",           ConfigTable.CODEC),
    ;

    public static final StringRepresentable.EnumCodec<@NotNull ConfigType> CODEC = StringRepresentable.fromEnum(ConfigType::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull ConfigType> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(ConfigType::fromString, ConfigType::getSerializedName);

    private final String name;
    private final Codec<? extends IConfigBase> codec;

    ConfigType(String name, Codec<? extends IConfigBase> codec)
    {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public @Nonnull String getSerializedName()
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

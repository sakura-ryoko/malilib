package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum ActiveMode implements IConfigOptionListEntry, StringRepresentable
{
    NEVER       ("never",       "malilib.label.active_mode.never"),
    WITH_KEY    ("with_key",    "malilib.label.active_mode.with_key"),
    ALWAYS      ("always",      "malilib.label.active_mode.always");

    public static final StringRepresentable.EnumCodec<@NotNull ActiveMode> CODEC = StringRepresentable.fromEnum(ActiveMode::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull ActiveMode> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(ActiveMode::fromStringStatic, ActiveMode::getSerializedName);
    public static final ImmutableList<@NotNull ActiveMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    ActiveMode(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.configString;
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public ActiveMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static ActiveMode fromStringStatic(String name)
    {
        for (ActiveMode mode : ActiveMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return ActiveMode.NEVER;
    }
}

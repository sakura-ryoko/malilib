package fi.dy.masa.malilib.config;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.util.StringUtils;

public enum HudAlignment implements IConfigOptionListEntry, StringRepresentable
{
    TOP_LEFT        ("top_left",        "malilib.label.alignment.top_left"),
    TOP_RIGHT       ("top_right",       "malilib.label.alignment.top_right"),
    BOTTOM_LEFT     ("bottom_left",     "malilib.label.alignment.bottom_left"),
    BOTTOM_RIGHT    ("bottom_right",    "malilib.label.alignment.bottom_right"),
    CENTER          ("center",          "malilib.label.alignment.center");

    public static final StringRepresentable.EnumCodec<@NotNull HudAlignment> CODEC = StringRepresentable.fromEnum(HudAlignment::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull HudAlignment> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(HudAlignment::fromStringStatic, HudAlignment::getSerializedName);
    public static final ImmutableList<@NotNull HudAlignment> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String unlockName;

    HudAlignment(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlockName = unlocName;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.unlockName);
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
    public HudAlignment fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static HudAlignment fromStringStatic(String name)
    {
        for (HudAlignment alignment : HudAlignment.values())
        {
            if (alignment.configString.equalsIgnoreCase(name))
            {
                return alignment;
            }
        }

        return HudAlignment.TOP_LEFT;
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.configString;
    }
}

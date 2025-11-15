package fi.dy.masa.malilib.config;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.util.StringUtils;

public enum HudAlignment implements IConfigOptionListEntry, StringIdentifiable
{
    TOP_LEFT        ("top_left",        "malilib.label.alignment.top_left"),
    TOP_RIGHT       ("top_right",       "malilib.label.alignment.top_right"),
    BOTTOM_LEFT     ("bottom_left",     "malilib.label.alignment.bottom_left"),
    BOTTOM_RIGHT    ("bottom_right",    "malilib.label.alignment.bottom_right"),
    CENTER          ("center",          "malilib.label.alignment.center");

    public static final StringIdentifiable.EnumCodec<HudAlignment> CODEC = StringIdentifiable.createCodec(HudAlignment::values);
    public static final PacketCodec<ByteBuf, HudAlignment> PACKET_CODEC = PacketCodecs.STRING.xmap(HudAlignment::fromStringStatic, HudAlignment::asString);
    public static final ImmutableList<HudAlignment> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String unlocName;

    HudAlignment(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.unlocName);
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
    public @Nonnull String asString()
    {
        return this.configString;
    }
}

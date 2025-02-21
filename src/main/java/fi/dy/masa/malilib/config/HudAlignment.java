package fi.dy.masa.malilib.config;

import java.util.function.IntFunction;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.IEnumCodecProvider;

public enum HudAlignment implements IConfigOptionListEntry, IEnumCodecProvider
{
    TOP_LEFT        (0, "top_left",        "malilib.label.alignment.top_left"),
    TOP_RIGHT       (1, "top_right",       "malilib.label.alignment.top_right"),
    BOTTOM_LEFT     (2, "bottom_left",     "malilib.label.alignment.bottom_left"),
    BOTTOM_RIGHT    (3, "bottom_right",    "malilib.label.alignment.bottom_right"),
    CENTER          (4, "center",          "malilib.label.alignment.center");

    public static final StringIdentifiable.EnumCodec<HudAlignment> CODEC = StringIdentifiable.createCodec(HudAlignment::values);
    public static final IntFunction<HudAlignment> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(HudAlignment::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, HudAlignment> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, HudAlignment::getIndex);
    public static final ImmutableList<HudAlignment> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String unlocName;

    HudAlignment(int index, String configString, String unlocName)
    {
        this.index = index;
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public Codec<HudAlignment> codec()
    {
        return CODEC;
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
        for (HudAlignment aligment : HudAlignment.values())
        {
            if (aligment.configString.equalsIgnoreCase(name))
            {
                return aligment;
            }
        }

        return HudAlignment.TOP_LEFT;
    }

    @Override
    public int getIndex()
    {
        return this.index;
    }

    @Override
    public String getName()
    {
        return this.configString;
    }

    @Override
    public String asString()
    {
        return this.configString;
    }
}

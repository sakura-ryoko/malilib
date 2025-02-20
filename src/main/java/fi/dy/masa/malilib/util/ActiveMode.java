package fi.dy.masa.malilib.util;

import java.util.function.IntFunction;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.data.IEnumCodecProvider;

public enum ActiveMode implements IConfigOptionListEntry, IEnumCodecProvider
{
    NEVER       (0, "never",       "malilib.label.active_mode.never"),
    WITH_KEY    (1, "with_key",    "malilib.label.active_mode.with_key"),
    ALWAYS      (2, "always",      "malilib.label.active_mode.always");

    public static final StringIdentifiable.EnumCodec<ActiveMode> CODEC = StringIdentifiable.createCodec(ActiveMode::values);
    public static final IntFunction<ActiveMode> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(ActiveMode::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, ActiveMode> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, ActiveMode::getIndex);
    public static final ImmutableList<ActiveMode> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String translationKey;

    ActiveMode(int index, String configString, String translationKey)
    {
        this.index = index;
        this.configString = configString;
        this.translationKey = translationKey;
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
    public String asString()
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

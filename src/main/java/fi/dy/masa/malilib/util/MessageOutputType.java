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

public enum MessageOutputType implements IConfigOptionListEntry, IEnumCodecProvider
{
    NONE      (0, "none",      "malilib.label.message_output_type.none"),
    ACTIONBAR (1, "actionbar", "malilib.label.message_output_type.actionbar"),
    MESSAGE   (2, "message",   "malilib.label.message_output_type.message");

    public static final StringIdentifiable.EnumCodec<MessageOutputType> CODEC = StringIdentifiable.createCodec(MessageOutputType::values);
    public static final IntFunction<MessageOutputType> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(MessageOutputType::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, MessageOutputType> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, MessageOutputType::getIndex);
    public static final ImmutableList<MessageOutputType> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String translationKey;

    MessageOutputType(int index, String configString, String translationKey)
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
    public MessageOutputType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static MessageOutputType fromStringStatic(String name)
    {
        for (MessageOutputType val : VALUES)
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return MessageOutputType.NONE;
    }
}

package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum MessageOutputType implements IConfigOptionListEntry, StringIdentifiable
{
    NONE      ("none",      "malilib.label.message_output_type.none"),
    ACTIONBAR ("actionbar", "malilib.label.message_output_type.actionbar"),
    MESSAGE   ("message",   "malilib.label.message_output_type.message");

    public static final StringIdentifiable.EnumCodec<MessageOutputType> CODEC = StringIdentifiable.createCodec(MessageOutputType::values);
    public static final PacketCodec<ByteBuf, MessageOutputType> PACKET_CODEC = PacketCodecs.STRING.xmap(MessageOutputType::fromStringStatic, MessageOutputType::asString);
    public static final ImmutableList<MessageOutputType> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    MessageOutputType(String configString, String translationKey)
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
    public @Nonnull String asString()
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

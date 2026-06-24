package fi.dy.masa.malilib.util.text;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum TextAlignment implements IConfigOptionListEntry, StringIdentifiable
{
    LEFT       ("left",       "malilib.label.text_alignment.left"),
    RIGHT      ("right",      "malilib.label.text_alignment.right"),
    CENTER     ("center",     "malilib.label.text_alignment.center");

    public static final EnumCodec<@NotNull TextAlignment> CODEC = StringIdentifiable.createCodec(TextAlignment::values);
    public static final PacketCodec<@NotNull ByteBuf, @NotNull TextAlignment> PACKET_CODEC = PacketCodecs.STRING.xmap(TextAlignment::fromStringStatic, TextAlignment::asString);
    public static final ImmutableList<@NotNull TextAlignment> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    TextAlignment(String configString, String translationKey)
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
    public TextAlignment fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static TextAlignment fromStringStatic(String name)
    {
        for (TextAlignment mode : TextAlignment.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return TextAlignment.CENTER;
    }
}

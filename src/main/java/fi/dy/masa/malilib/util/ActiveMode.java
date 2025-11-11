package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum ActiveMode implements IConfigOptionListEntry, StringIdentifiable
{
    NEVER       ("never",       "malilib.label.active_mode.never"),
    WITH_KEY    ("with_key",    "malilib.label.active_mode.with_key"),
    ALWAYS      ("always",      "malilib.label.active_mode.always");

    public static final StringIdentifiable.EnumCodec<ActiveMode> CODEC = StringIdentifiable.createCodec(ActiveMode::values);
    public static final PacketCodec<ByteBuf, ActiveMode> PACKET_CODEC = PacketCodecs.STRING.xmap(ActiveMode::fromStringStatic, ActiveMode::asString);
    public static final ImmutableList<ActiveMode> VALUES = ImmutableList.copyOf(values());

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

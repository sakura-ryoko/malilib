package fi.dy.masa.malilib.hotkeys;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeyAction implements IConfigOptionListEntry, StringIdentifiable
{
    PRESS   ("press",   "malilib.label.key_action.press"),
    RELEASE ("release", "malilib.label.key_action.release"),
    BOTH    ("both",    "malilib.label.key_action.both");

    public static final StringIdentifiable.EnumCodec<KeyAction> CODEC = StringIdentifiable.createCodec(KeyAction::values);
    public static final PacketCodec<ByteBuf, KeyAction> PACKET_CODEC = PacketCodecs.STRING.xmap(KeyAction::fromStringStatic, KeyAction::asString);
    public static final ImmutableList<KeyAction> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    KeyAction(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public @Nonnull String asString()
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
    public KeyAction fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static KeyAction fromStringStatic(String name)
    {
        for (KeyAction action : KeyAction.values())
        {
            if (action.configString.equalsIgnoreCase(name))
            {
                return action;
            }
        }

        return KeyAction.PRESS;
    }
}

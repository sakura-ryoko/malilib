package fi.dy.masa.malilib.hotkeys;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public enum KeyAction implements IConfigOptionListEntry, StringRepresentable
{
    PRESS   ("press",   "malilib.label.key_action.press"),
    RELEASE ("release", "malilib.label.key_action.release"),
    BOTH    ("both",    "malilib.label.key_action.both");

    public static final StringRepresentable.EnumCodec<@NotNull KeyAction> CODEC = StringRepresentable.fromEnum(KeyAction::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull KeyAction> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(KeyAction::fromStringStatic, KeyAction::getSerializedName);
    public static final ImmutableList<@NotNull KeyAction> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    KeyAction(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public @Nonnull String getSerializedName()
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

package fi.dy.masa.malilib.hotkeys;

import java.util.function.IntFunction;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.IEnumCodecProvider;

public enum KeyAction implements IConfigOptionListEntry, IEnumCodecProvider
{
    PRESS   (0, "press",   "malilib.label.key_action.press"),
    RELEASE (1, "release", "malilib.label.key_action.release"),
    BOTH    (2, "both",    "malilib.label.key_action.both");

    public static final StringIdentifiable.EnumCodec<KeyAction> CODEC = StringIdentifiable.createCodec(KeyAction::values);
    public static final IntFunction<KeyAction> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(KeyAction::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, KeyAction> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, KeyAction::getIndex);
    public static final ImmutableList<KeyAction> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String translationKey;

    KeyAction(int index, String configString, String translationKey)
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
    public String asString()
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

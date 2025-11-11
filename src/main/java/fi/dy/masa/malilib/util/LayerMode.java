package fi.dy.masa.malilib.util;

import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum LayerMode implements IConfigOptionListEntry, StringIdentifiable
{
    ALL             (0, "all",             "malilib.gui.label.layer_mode.all"),
    SINGLE_LAYER    (1, "single_layer",    "malilib.gui.label.layer_mode.single_layer"),
    LAYER_RANGE     (2, "layer_range",     "malilib.gui.label.layer_mode.layer_range"),
    ALL_BELOW       (3, "all_below",       "malilib.gui.label.layer_mode.all_below"),
    ALL_ABOVE       (4, "all_above",       "malilib.gui.label.layer_mode.all_above");

    public static final StringIdentifiable.EnumCodec<LayerMode> CODEC = StringIdentifiable.createCodec(LayerMode::values);
    public static final IntFunction<LayerMode> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(LayerMode::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, LayerMode> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, LayerMode::getIndex);
    public static final ImmutableList<LayerMode> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String translationKey;

    LayerMode(int index, String configString, String translationKey)
    {
        this.index = index;
        this.configString = configString;
        this.translationKey = translationKey;
    }

    public int getIndex()
    {
        return this.index;
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
    public LayerMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static LayerMode fromStringStatic(String name)
    {
        for (LayerMode mode : LayerMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return LayerMode.ALL;
    }
}

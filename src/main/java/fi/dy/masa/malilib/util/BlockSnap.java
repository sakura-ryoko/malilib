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

public enum BlockSnap implements IConfigOptionListEntry, IEnumCodecProvider
{
    NONE        (0, "none",    "malilib.gui.label.block_snap.none"),
    CENTER      (1, "center",  "malilib.gui.label.block_snap.center"),
    CORNER      (2, "corner",  "malilib.gui.label.block_snap.corner");

    public static final StringIdentifiable.EnumCodec<BlockSnap> CODEC = StringIdentifiable.createCodec(BlockSnap::values);
    public static final IntFunction<BlockSnap> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(BlockSnap::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, BlockSnap> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, BlockSnap::getIndex);
    public static final ImmutableList<BlockSnap> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String configString;
    private final String translationKey;

    BlockSnap(int index, String configString, String translationKey)
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

    public BlockSnap fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static BlockSnap fromStringStatic(String name)
    {
        for (BlockSnap val : BlockSnap.values())
        {
            if (val.name().equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return BlockSnap.NONE;
    }
}

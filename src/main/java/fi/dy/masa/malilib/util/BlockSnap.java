package fi.dy.masa.malilib.util;

import java.util.function.IntFunction;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.data.IEnumCodecProvider;

public enum BlockSnap implements IConfigOptionListEntry, StringIdentifiable
{
    NONE        ("none",    "malilib.gui.label.block_snap.none"),
    CENTER      ("center",  "malilib.gui.label.block_snap.center"),
    CORNER      ("corner",  "malilib.gui.label.block_snap.corner");

    public static final StringIdentifiable.EnumCodec<BlockSnap> CODEC = StringIdentifiable.createCodec(BlockSnap::values);
    public static final ImmutableList<BlockSnap> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    BlockSnap(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public Codec<BlockSnap> codec()
    {
        return CODEC;
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

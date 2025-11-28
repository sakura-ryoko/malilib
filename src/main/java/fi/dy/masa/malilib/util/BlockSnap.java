package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum BlockSnap implements IConfigOptionListEntry, StringRepresentable
{
    NONE        ("none",    "malilib.gui.label.block_snap.none"),
    CENTER      ("center",  "malilib.gui.label.block_snap.center"),
    CORNER      ("corner",  "malilib.gui.label.block_snap.corner");

    public static final StringRepresentable.EnumCodec<@NotNull BlockSnap> CODEC = StringRepresentable.fromEnum(BlockSnap::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BlockSnap> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(BlockSnap::fromStringStatic, BlockSnap::getSerializedName);
    public static final ImmutableList<@NotNull BlockSnap> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    BlockSnap(String configString, String translationKey)
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
    public @Nonnull String getSerializedName()
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

package fi.dy.masa.malilib.util.position;

import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * Post-ReWrite code
 */
public enum BlockMirror implements IConfigOptionListEntry, StringIdentifiable
{
    NONE (0, "none", null, net.minecraft.util.BlockMirror.NONE),
    X    (1, "x", Direction.Axis.X, net.minecraft.util.BlockMirror.FRONT_BACK),
    Y    (2, "y", Direction.Axis.Y, net.minecraft.util.BlockMirror.NONE),
    Z    (3, "z", Direction.Axis.Z, net.minecraft.util.BlockMirror.LEFT_RIGHT);

    public static final StringIdentifiable.EnumCodec<BlockMirror> CODEC = StringIdentifiable.createCodec(BlockMirror::values);
    public static final IntFunction<BlockMirror> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(BlockMirror::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, BlockMirror> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, BlockMirror::getIndex);
    public static final BlockMirror[] VALUES = values();

    private final int index;
    private final String name;
    private final String translationKey;
    private final net.minecraft.util.BlockMirror vanillaMirror;
    @Nullable private final Direction.Axis axis;

    BlockMirror(int index, String name, @Nullable Direction.Axis axis, net.minecraft.util.BlockMirror vanillaMirror)
    {
        this.index = index;
        this.name = name;
        this.vanillaMirror = vanillaMirror;
        this.translationKey = MaLiLibReference.MOD_ID + ".label.block_mirror." + name;
        this.axis = axis;
    }

    public int getIndex()
    {
        return this.index;
    }

    @Override
    public String getStringValue()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public @Nonnull String asString()
    {
        return this.name;
    }

    /**
     * Determines the rotation that is equivalent to this mirror if the rotating object faces in the given direction
     */
    public BlockRotation toRotation(Direction direction)
    {
        if (direction.getAxis() == this.axis)
        {
            return BlockRotation.CW_180;
        }

        return BlockRotation.NONE;
    }

    /**
     * Mirror the given direction according to this mirror
     */
    public Direction mirror(Direction direction)
    {
        if (direction.getAxis() == this.axis)
        {
            return direction.getOpposite();
        }

        return direction;
    }

    public BlockMirror cycle(boolean reverse)
    {
        int index = (this.index + (reverse ? -1 : 1)) & 3;
        return VALUES[index];
    }

    @Override
    public IConfigOptionListEntry fromString(String value)
    {
        return byName(value);
    }

    public net.minecraft.util.BlockMirror getVanillaMirror()
    {
        return this.vanillaMirror;
    }

    public static BlockMirror byName(String name)
    {
        for (BlockMirror mirror : VALUES)
        {
            if (mirror.name.equalsIgnoreCase(name))
            {
                return mirror;
            }
        }

        return NONE;
    }
}

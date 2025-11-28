package fi.dy.masa.malilib.util.position;

import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * Post-ReWrite code
 */
public enum BlockMirror implements IConfigOptionListEntry, StringRepresentable
{
    NONE (0, "none", null, net.minecraft.world.level.block.Mirror.NONE),
    X    (1, "x", Direction.Axis.X, net.minecraft.world.level.block.Mirror.FRONT_BACK),
    Y    (2, "y", Direction.Axis.Y, net.minecraft.world.level.block.Mirror.NONE),
    Z    (3, "z", Direction.Axis.Z, net.minecraft.world.level.block.Mirror.LEFT_RIGHT);

    public static final StringRepresentable.EnumCodec<@NotNull BlockMirror> CODEC = StringRepresentable.fromEnum(BlockMirror::values);
    public static final IntFunction<BlockMirror> INDEX_TO_VALUE = ByIdMap.continuous(BlockMirror::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BlockMirror> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_TO_VALUE, BlockMirror::getIndex);
    public static final BlockMirror[] VALUES = values();

    private final int index;
    private final String name;
    private final String translationKey;
    private final net.minecraft.world.level.block.Mirror vanillaMirror;
    @Nullable private final Direction.Axis axis;

    BlockMirror(int index, String name, @Nullable Direction.Axis axis, net.minecraft.world.level.block.Mirror vanillaMirror)
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
    public @Nonnull String getSerializedName()
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

    public net.minecraft.world.level.block.Mirror getVanillaMirror()
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

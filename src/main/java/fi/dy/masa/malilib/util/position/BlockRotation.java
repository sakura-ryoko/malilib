package fi.dy.masa.malilib.util.position;

import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * Post-ReWrite code
 */
public enum BlockRotation implements IConfigOptionListEntry, StringRepresentable
{
    NONE    (0, "none",       Rotation.NONE),
    CW_90   (1, "rotate_90",  Rotation.CLOCKWISE_90),
    CW_180  (2, "rotate_180", Rotation.CLOCKWISE_180),
    CCW_90  (3, "rotate_270", Rotation.COUNTERCLOCKWISE_90);

    public static final StringRepresentable.EnumCodec<@NotNull BlockRotation> CODEC = StringRepresentable.fromEnum(BlockRotation::values);
    public static final IntFunction<BlockRotation> INDEX_TO_VALUE = ByIdMap.continuous(BlockRotation::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BlockRotation> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_TO_VALUE, BlockRotation::getIndex);
    public static final BlockRotation[] VALUES = values();

    private final int index;
    private final String name;
    private final String translationKey;
    private final Rotation vanillaRotation;

    BlockRotation(int index, String name, Rotation vanillaRotation)
    {
        this.index = index;
        this.name = name;
        this.translationKey = MaLiLibReference.MOD_ID+".label.block_rotation." + name;
        this.vanillaRotation = vanillaRotation;
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public String getStringValue()
    {
        return this.getName();
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.name;
    }

    public BlockRotation add(BlockRotation rotation)
    {
        int index = (this.index + rotation.index) & 3;
        return VALUES[index];
    }

    public Direction rotate(Direction direction)
    {
        if (direction.getAxis() != Direction.Axis.Y)
        {
            switch (this)
            {
                case CW_90:     return direction.getClockWise();
                case CW_180:    return direction.getOpposite();
                case CCW_90:    return direction.getCounterClockWise();
            }
        }

        return direction;
    }

    public BlockRotation getReverseRotation()
    {
	    return switch (this)
	    {
		    case CCW_90 -> BlockRotation.CW_90;
		    case CW_90 -> BlockRotation.CCW_90;
		    case CW_180 -> BlockRotation.CW_180;
		    default -> this;
	    };

    }

    public BlockRotation cycle(boolean reverse)
    {
        int index = (this.index + (reverse ? -1 : 1)) & 3;
        return VALUES[index];
    }

    @Override
    public IConfigOptionListEntry fromString(String value)
    {
        return byName(value);
    }

    public Rotation getVanillaRotation()
    {
        return this.vanillaRotation;
    }

    public static BlockRotation byName(String name)
    {
        for (BlockRotation rot : VALUES)
        {
            if (rot.name.equalsIgnoreCase(name))
            {
                return rot;
            }
        }

        return NONE;
    }
}

package fi.dy.masa.malilib.util.position;

import java.util.function.IntFunction;
import javax.annotation.Nonnull;
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
public enum BlockRotation implements IConfigOptionListEntry, StringIdentifiable
{
    NONE    (0, net.minecraft.util.BlockRotation.NONE,                  "none"),
    CW_90   (1, net.minecraft.util.BlockRotation.CLOCKWISE_90,          "rotate_90"),
    CW_180  (2, net.minecraft.util.BlockRotation.CLOCKWISE_180,         "rotate_180"),
    CCW_90  (3, net.minecraft.util.BlockRotation.COUNTERCLOCKWISE_90,   "rotate_270");

    public static final StringIdentifiable.EnumCodec<BlockRotation> CODEC = StringIdentifiable.createCodec(BlockRotation::values);
    public static final IntFunction<BlockRotation> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(BlockRotation::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, BlockRotation> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, BlockRotation::getIndex);
    public static final BlockRotation[] VALUES = values();

    private final int index;
    private final String configString;
    private final String translationKey;
    private final net.minecraft.util.BlockRotation vanillaRotation;

    BlockRotation(int index, net.minecraft.util.BlockRotation vanillaRotation, String name)
    {
        this.index = index;
        this.vanillaRotation = vanillaRotation;
        this.configString = name;
        this.translationKey = MaLiLibReference.MOD_ID + ".label.block_rotation." + name;
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

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public @Nonnull String asString()
    {
        return this.configString;
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
            switch(this)
            {
                case CW_90:     return direction.rotateYClockwise();
                case CW_180:    return direction.getOpposite();
                case CCW_90:    return direction.rotateYCounterclockwise();
            }
        }

        return direction;
    }

    public BlockRotation getReverseRotation()
    {
        switch (this)
        {
            case CCW_90:    return BlockRotation.CW_90;
            case CW_90:     return BlockRotation.CCW_90;
            case CW_180:    return BlockRotation.CW_180;
            default:
        }

        return this;
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

    public net.minecraft.util.BlockRotation getVanillaRotation()
    {
        return this.vanillaRotation;
    }

    public static BlockRotation byName(String name)
    {
        for (BlockRotation rot : VALUES)
        {
            if (rot.configString.equalsIgnoreCase(name))
            {
                return rot;
            }
        }

        return NONE;
    }
}

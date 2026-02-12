package fi.dy.masa.malilib.util.position;

import java.util.Locale;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

public enum Direction implements StringRepresentable
{
    DOWN(0, 1, -1, Axis.Y, AxisDirection.NEGATIVE, "down", net.minecraft.core.Direction.DOWN),
    UP(1, 0, -1, Axis.Y, AxisDirection.POSITIVE, "up", net.minecraft.core.Direction.UP),
    NORTH(2, 3, 2, Axis.Z, AxisDirection.NEGATIVE, "north", net.minecraft.core.Direction.NORTH),
    SOUTH(3, 2, 0, Axis.Z, AxisDirection.POSITIVE, "south", net.minecraft.core.Direction.SOUTH),
    WEST(4, 5, 1, Axis.X, AxisDirection.NEGATIVE, "west", net.minecraft.core.Direction.WEST),
    EAST(5, 4, 3, Axis.X, AxisDirection.POSITIVE, "east", net.minecraft.core.Direction.EAST);

    public static final Direction[] ALL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    public static final Direction[] HORIZONTALS_BY_INDEX = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP};

    public static final StringRepresentable.EnumCodec<@NotNull Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
    public static final IntFunction<Direction> INDEX_TO_VALUE = ByIdMap.continuous(Direction::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull Direction> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_TO_VALUE, Direction::getIndex);
    public static final Direction[] VALUES = values();

    private final int index;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int oppositeId;
    private final int horizontalIndex;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final net.minecraft.core.Direction vanillaDirection;
    private final String name;
    private final String translationKey;

    Direction(int index, int oppositeId, int horizontalIndex, Axis axis, AxisDirection axisDirection, String name, net.minecraft.core.Direction vanillaDirection)
    {
        this.index = index;
        this.offsetX = axis == Axis.X ? axisDirection.getOffset() : 0;
        this.offsetY = axis == Axis.Y ? axisDirection.getOffset() : 0;
        this.offsetZ = axis == Axis.Z ? axisDirection.getOffset() : 0;
        this.oppositeId = oppositeId;
        this.horizontalIndex = horizontalIndex;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.name = name;
        this.translationKey = "malilib.label.direction." + name;
        this.vanillaDirection = vanillaDirection;
    }

    public int getIndex()
    {
        return this.index;
    }

    public Axis getAxis()
    {
        return this.axis;
    }

    public AxisDirection getAxisDirection()
    {
        return this.axisDirection;
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
    public @Nonnull String getSerializedName()
    {
        return this.name;
    }

    public int getXOffset()
    {
        return this.offsetX;
    }

    public int getYOffset()
    {
        return this.offsetY;
    }

    public int getZOffset()
    {
        return this.offsetZ;
    }

    public Direction getOpposite()
    {
        return ALL_DIRECTIONS[this.oppositeId];
    }

    public net.minecraft.core.Direction getVanillaDirection()
    {
        return this.vanillaDirection;
    }

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public Direction rotateY()
    {
        switch (this)
        {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
        }

        return this;
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public Direction rotateYCCW()
    {
        switch (this)
        {
            case NORTH:
                return WEST;
            case WEST:
                return SOUTH;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
        }

        return this;
    }

    public Direction rotateAround(Axis axis)
    {
        switch (axis)
        {
            case X:
                if (this != WEST && this != EAST)
                {
                    return this.rotateX();
                }
                return this;
            case Y:
                if (this != UP && this != DOWN)
                {
                    return this.rotateY();
                }
                return this;
            case Z:
                if (this != NORTH && this != SOUTH)
                {
                    return this.rotateZ();
                }
                return this;
        }

        return this;
    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public Direction rotateX()
    {
        switch (this)
        {
            case NORTH:
                return DOWN;
            case DOWN:
                return SOUTH;
            case SOUTH:
                return UP;
            case UP:
                return NORTH;
        }

        return this;
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    public Direction rotateZ()
    {
        switch (this)
        {
            case EAST:
                return DOWN;
            case DOWN:
                return WEST;
            case WEST:
                return UP;
            case UP:
                return EAST;
        }

        return this;
    }

    public Direction cycle(boolean reverse)
    {
        return reverse ? this.cycleBackward() : this.cycleForward();
    }

    public Direction cycleForward()
    {
        int index = this.index;
        index = index >= 5 ? 0 : index + 1;
        return ALL_DIRECTIONS[index];
    }

    public Direction cycleBackward()
    {
        int index = this.index;
        index = index == 0 ? 5 : index - 1;
        return ALL_DIRECTIONS[index];
    }

    public static Direction byIndex(int index)
    {
        return ALL_DIRECTIONS[index % 6];
    }

    public static Direction byHorizontalIndex(int horizontalIndexIn)
    {
        return HORIZONTALS_BY_INDEX[horizontalIndexIn & 3];
    }

    public static Direction of(net.minecraft.core.Direction facing)
    {
        return byIndex(facing.get3DDataValue());
    }

    /**
     * "Get the Direction corresponding to the given angle in degrees (0-360).
     * Out of bounds values are wrapped around.
     * An angle of 0 is SOUTH, an angle of 90 would be WEST."
     */
    public static Direction fromAngle(double angle)
    {
        return byHorizontalIndex(MathUtils.floor(angle / 90.0 + 0.5) & 3);
    }

    /**
     * Gets the angle in degrees corresponding to this Direction.
     */
    public float getHorizontalAngle()
    {
        return (float) ((this.horizontalIndex & 3) * 90);
    }

    public enum Axis implements StringRepresentable
    {
        X(0, "x", false),
        Y(1, "y", true),
        Z(2, "z", false);

        public static final StringRepresentable.EnumCodec<@NotNull Axis> CODEC = StringRepresentable.fromEnum(Axis::values);
        public static final IntFunction<Axis> INDEX_TO_VALUE = ByIdMap.continuous(Axis::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<@NotNull ByteBuf, @NotNull Axis> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_TO_VALUE, Axis::getIndex);
        public static final Axis[] VALUES_ARR = values();
        public static final ImmutableList<Axis> ALL_AXES = ImmutableList.copyOf(VALUES_ARR);

        private final int index;
        private final String name;
        private final boolean isVertical;

        Axis(int index, String name, boolean isVertical)
        {
            this.index = index;
            this.name = name;
            this.isVertical = isVertical;
        }

        public int getIndex()
        {
            return this.index;
        }

        public String getName()
        {
            return this.name;
        }

        @Override
        public @NonNull String getSerializedName()
        {
            return this.name;
        }

        public boolean isHorizontal()
        {
            return this.isVertical == false;
        }

        public boolean isVertical()
        {
            return this.isVertical;
        }

        public Axis cycle(boolean reverse)
        {
            return reverse ? this.cycleBackward() : this.cycleForward();
        }

        public Axis cycleForward()
        {
            int index = this.index;

            if (++index >= VALUES_ARR.length)
            {
                index = 0;
            }

            return VALUES_ARR[index];
        }

        public Axis cycleBackward()
        {
            int index = this.index;

            if (--index < 0)
            {
                index = VALUES_ARR.length - 1;
            }

            return VALUES_ARR[index];
        }

        public static Axis byName(String name)
        {
            switch (name.toLowerCase(Locale.ROOT))
            {
                case "x":
                    return X;
                case "y":
                    return Y;
                case "z":
                    return Z;
            }

            return Y;
        }
    }

    public enum AxisDirection
    {
        NEGATIVE(-1),
        POSITIVE(1);

        private final int offset;

        AxisDirection(int offset)
        {
            this.offset = offset;
        }

        public int getOffset()
        {
            return this.offset;
        }
    }
}

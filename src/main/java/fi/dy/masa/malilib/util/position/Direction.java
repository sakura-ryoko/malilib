package fi.dy.masa.malilib.util.position;

import java.util.Locale;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

public enum Direction implements StringIdentifiable
{
    DOWN(0, 1, -1, Axis.Y, AxisDirection.NEGATIVE, "down", net.minecraft.util.math.Direction.DOWN),
    UP(1, 0, -1, Axis.Y, AxisDirection.POSITIVE, "up", net.minecraft.util.math.Direction.UP),
    NORTH(2, 3, 2, Axis.Z, AxisDirection.NEGATIVE, "north", net.minecraft.util.math.Direction.NORTH),
    SOUTH(3, 2, 0, Axis.Z, AxisDirection.POSITIVE, "south", net.minecraft.util.math.Direction.SOUTH),
    WEST(4, 5, 1, Axis.X, AxisDirection.NEGATIVE, "west", net.minecraft.util.math.Direction.WEST),
    EAST(5, 4, 3, Axis.X, AxisDirection.POSITIVE, "east", net.minecraft.util.math.Direction.EAST);

    public static final Direction[] ALL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    public static final Direction[] HORIZONTALS_BY_INDEX = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.UP};

    public static final StringIdentifiable.EnumCodec<@NotNull Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
    public static final IntFunction<Direction> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(Direction::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<@NotNull ByteBuf, @NotNull Direction> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, Direction::getIndex);
    public static final Direction[] VALUES = values();

    private final int index;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int oppositeId;
    private final int horizontalIndex;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final net.minecraft.util.math.Direction vanillaDirection;
    private final String name;
    private final String translationKey;

    Direction(int index, int oppositeId, int horizontalIndex, Axis axis, AxisDirection axisDirection, String name, net.minecraft.util.math.Direction vanillaDirection)
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
    public @Nonnull String asString()
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

    public net.minecraft.util.math.Direction getVanillaDirection()
    {
        return this.vanillaDirection;
    }

    public Direction rotateAround(Axis axis)
    {
        return switch (axis)
        {
            case X ->
            {
                if (this != WEST && this != EAST)
                {
                    yield this.rotateX();
                }
                yield this;
            }
            case Y ->
            {
                if (this != UP && this != DOWN)
                {
                    yield this.rotateY();
                }
                yield this;
            }
            case Z ->
            {
                if (this != NORTH && this != SOUTH)
                {
                    yield this.rotateZ();
                }
                yield this;
            }
        };
    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public Direction rotateX()
    {
	    return switch (this)
	    {
		    case NORTH -> DOWN;
		    case DOWN -> SOUTH;
		    case SOUTH -> UP;
		    case UP -> NORTH;
		    default -> this;
	    };
    }

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public Direction rotateY()
    {
        return switch (this)
        {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            default -> this;
        };
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    public Direction rotateZ()
    {
	    return switch (this)
	    {
		    case EAST -> DOWN;
		    case DOWN -> WEST;
		    case WEST -> UP;
		    case UP -> EAST;
		    default -> this;
	    };
    }

    /**
     * Rotates this Facing around the given axis counter-clockwise.
     */
    public Direction rotateAroundCCW(Axis axis)
    {
        return switch (axis)
        {
            case X ->
            {
                if (this != WEST && this != EAST)
                {
                    yield this.rotateXCCW();
                }
                yield this;
            }
            case Y ->
            {
                if (this != UP && this != DOWN)
                {
                    yield this.rotateYCCW();
                }
                yield this;
            }
            case Z ->
            {
                if (this != NORTH && this != SOUTH)
                {
                    yield this.rotateZCCW();
                }
                yield this;
            }
        };
    }

    /**
     * Rotate this Facing around the X axis counter-clockwise (NORTH => UP => SOUTH => DOWN => NORTH)
     */
    public Direction rotateXCCW()
    {
        return switch (this)
        {
            case NORTH -> UP;
            case UP -> SOUTH;
            case SOUTH -> DOWN;
            case DOWN -> NORTH;
            default -> this;
        };
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public Direction rotateYCCW()
    {
        return switch (this)
        {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            default -> this;
        };
    }

    /**
     * Rotate this Facing around the Z axis counter-clockwise (EAST => UP => WEST => DOWN => EAST)
     */
    public Direction rotateZCCW()
    {
        return switch (this)
        {
            case EAST -> UP;
            case UP -> WEST;
            case WEST -> DOWN;
            case DOWN -> EAST;
            default -> this;
        };
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

    public static Direction of(net.minecraft.util.math.Direction facing)
    {
        return byIndex(facing.getIndex());
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

    public enum Axis implements StringIdentifiable
    {
        X(0, "x", false),
        Y(1, "y", true),
        Z(2, "z", false);

        public static final StringIdentifiable.EnumCodec<@NotNull Axis> CODEC = StringIdentifiable.createCodec(Axis::values);
        public static final IntFunction<Axis> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(Axis::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
        public static final PacketCodec<@NotNull ByteBuf, @NotNull Axis> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, Axis::getIndex);
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
        public String asString()
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

        public double pick(final double x, final double y, final double z)
        {
            return switch (this)
            {
                case X -> x;
                case Y -> y;
                case Z -> z;
            };
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
            return switch (name.toLowerCase(Locale.ROOT))
            {
                case "x" -> X;
                case "z" -> Z;
                default -> Y;
            };
        }

        public static Axis fromVanilla(net.minecraft.util.math.Direction.Axis axis)
        {
            return Axis.byName(axis.asString());
        }

        public net.minecraft.util.math.Direction.Axis toVanilla()
        {
            return net.minecraft.util.math.Direction.Axis.fromId(this.name);
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

package malilib.util.position;

import java.util.Locale;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumFacing;

import malilib.util.MathUtils;
import malilib.util.StringUtils;

public enum Direction
{
    DOWN (0, 1, -1, Axis.Y, AxisDirection.NEGATIVE, "down",  EnumFacing.DOWN),
    UP   (1, 0, -1, Axis.Y, AxisDirection.POSITIVE, "up",    EnumFacing.UP),
    NORTH(2, 3,  2, Axis.Z, AxisDirection.NEGATIVE, "north", EnumFacing.NORTH),
    SOUTH(3, 2,  0, Axis.Z, AxisDirection.POSITIVE, "south", EnumFacing.SOUTH),
    WEST (4, 5,  1, Axis.X, AxisDirection.NEGATIVE, "west",  EnumFacing.WEST),
    EAST (5, 4,  3, Axis.X, AxisDirection.POSITIVE, "east",  EnumFacing.EAST);

    public static final Direction[] ALL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    public static final Direction[] HORIZONTALS_BY_INDEX = new Direction[] { Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST };
    public static final Direction[] VERTICAL_DIRECTIONS = new Direction[] { Direction.DOWN, Direction.UP };

    private final int index;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int oppositeId;
    private final int horizontalIndex;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final EnumFacing vanillaDirection;
    private final String name;
    private final String translationKey;

    Direction(int index, int oppositeId, int horizontalIndex, Axis axis, AxisDirection axisDirection, String name, EnumFacing vanillaDirection)
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

    public EnumFacing getVanillaDirection()
    {
        return this.vanillaDirection;
    }

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public Direction rotateY()
    {
        switch(this)
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
        switch(this)
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
        switch(axis)
        {
            case X:
                if (this != WEST && this != EAST) { return this.rotateX(); }
                return this;
            case Y:
                if (this != UP && this != DOWN) { return this.rotateY(); }
                return this;
            case Z:
                if (this != NORTH && this != SOUTH) { return this.rotateZ(); }
                return this;
        }

        return this;
    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    public Direction rotateX()
    {
        switch(this)
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
        switch(this)
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

    public static Direction of(EnumFacing facing)
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
        return (float)((this.horizontalIndex & 3) * 90);
    }

    public enum Axis
    {
        X(0, "x", false),
        Y(1, "y", true),
        Z(2, "z", false);

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

        public String getName()
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
                index = 0;

            return VALUES_ARR[index];
        }

        public Axis cycleBackward()
        {
            int index = this.index;

            if (--index < 0)
                index = VALUES_ARR.length - 1;

            return VALUES_ARR[index];
        }

        public static Axis byName(String name)
        {
            switch (name.toLowerCase(Locale.ROOT))
            {
                case "x": return X;
                case "y": return Y;
                case "z": return Z;
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

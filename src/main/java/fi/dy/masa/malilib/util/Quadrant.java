package fi.dy.masa.malilib.util;

import java.util.function.IntFunction;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import fi.dy.masa.malilib.util.data.IEnumCodecProvider;

public enum Quadrant implements IEnumCodecProvider
{
    NORTH_WEST (0, "north_west"),
    NORTH_EAST (1, "north_east"),
    SOUTH_WEST (2, "south_west"),
    SOUTH_EAST (3, "south_east");

    public static final StringIdentifiable.EnumCodec<Quadrant> CODEC = StringIdentifiable.createCodec(Quadrant::values);
    public static final IntFunction<Quadrant> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(Quadrant::getIndex, values(), ValueLists.OutOfBoundsHandling.WRAP);
    public static final PacketCodec<ByteBuf, Quadrant> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, Quadrant::getIndex);
    public static final ImmutableList<Quadrant> VALUES = ImmutableList.copyOf(values());

    private final int index;
    private final String name;

    Quadrant(int index, String name)
    {
        this.index = index;
        this.name = name;
    }

    @Override
    public int getIndex()
    {
        return this.index;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String asString()
    {
        return this.name;
    }

    public static Quadrant getQuadrant(BlockPos pos, Vec3d center)
    {
        return getQuadrant(pos.getX(), pos.getZ(), center);
    }

    public static Quadrant getQuadrant(int x, int z, Vec3d center)
    {
        // West
        if (x <= center.x)
        {
            // North
            if (z <= center.z)
            {
                return NORTH_WEST;
            }
            // South
            else
            {
                return SOUTH_WEST;
            }
        }
        // East
        else
        {
            // North
            if (z <= center.z)
            {
                return NORTH_EAST;
            }
            // South
            else
            {
                return SOUTH_EAST;
            }
        }
    }

    public static Quadrant getQuadrant(double x, double z, Vec3d center)
    {
        // West
        if (x <= center.x)
        {
            // North
            if (z <= center.z)
            {
                return NORTH_WEST;
            }
            // South
            else
            {
                return SOUTH_WEST;
            }
        }
        // East
        else
        {
            // North
            if (z <= center.z)
            {
                return NORTH_EAST;
            }
            // South
            else
            {
                return SOUTH_EAST;
            }
        }
    }
}
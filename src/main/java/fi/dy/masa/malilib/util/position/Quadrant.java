package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum Quadrant implements IConfigOptionListEntry, StringRepresentable
{
    NORTH_WEST ("north_west"),
    NORTH_EAST ("north_east"),
    SOUTH_WEST ("south_west"),
    SOUTH_EAST ("south_east");

    public static final EnumCodec<@NotNull Quadrant> CODEC = StringRepresentable.fromEnum(Quadrant::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull Quadrant> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(Quadrant::fromStringStatic, Quadrant::getSerializedName);
    public static final ImmutableList<@NotNull Quadrant> VALUES = ImmutableList.copyOf(values());

    private final String configString;

    Quadrant(String name)
    {
        this.configString = name;
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.configString;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
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

    @Override
    public Quadrant fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static Quadrant fromStringStatic(String name)
    {
        for (Quadrant val : VALUES)
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return Quadrant.NORTH_WEST;
    }

    public static Quadrant getQuadrant(BlockPos pos, Vec3 center)
    {
        return getQuadrant(pos.getX(), pos.getZ(), center);
    }

    public static Quadrant getQuadrant(int x, int z, Vec3 center)
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

    public static Quadrant getQuadrant(double x, double z, Vec3 center)
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
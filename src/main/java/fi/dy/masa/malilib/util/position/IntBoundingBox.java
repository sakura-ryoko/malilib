package fi.dy.masa.malilib.util.position;

import java.util.Objects;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.IntArrayData;

public record IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
{
    public static final IntBoundingBox ORIGIN = new IntBoundingBox(0, 0, 0, 0, 0, 0);

    public static final Codec<IntBoundingBox> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("minX").forGetter(get -> get.minX),
                    PrimitiveCodec.INT.fieldOf("minY").forGetter(get -> get.minY),
                    PrimitiveCodec.INT.fieldOf("minZ").forGetter(get -> get.minZ),
                    PrimitiveCodec.INT.fieldOf("maxX").forGetter(get -> get.maxX),
                    PrimitiveCodec.INT.fieldOf("maxY").forGetter(get -> get.maxY),
                    PrimitiveCodec.INT.fieldOf("maxZ").forGetter(get -> get.maxZ)
            ).apply(inst, IntBoundingBox::new)
    );
    public static final PacketCodec<@NotNull ByteBuf, @NotNull IntBoundingBox> PACKET_CODEC = new PacketCodec<>()
    {
        @Override
        public void encode(@NonNull ByteBuf buf, IntBoundingBox value)
        {
            PacketCodecs.INTEGER.encode(buf, value.minX);
            PacketCodecs.INTEGER.encode(buf, value.minY);
            PacketCodecs.INTEGER.encode(buf, value.minZ);
            PacketCodecs.INTEGER.encode(buf, value.maxX);
            PacketCodecs.INTEGER.encode(buf, value.maxY);
            PacketCodecs.INTEGER.encode(buf, value.maxZ);
        }

        @Override
        public @NonNull IntBoundingBox decode(@NonNull ByteBuf buf)
        {
            return new IntBoundingBox(
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf)
            );
        }
    };

    public boolean contains(Vec3i pos)
    {
        return  pos.getX() >= this.minX &&
                pos.getX() <= this.maxX &&
                pos.getZ() >= this.minZ &&
                pos.getZ() <= this.maxZ &&
                pos.getY() >= this.minY &&
                pos.getY() <= this.maxY;
    }

    public boolean contains(long pos)
    {
        int x = BlockPos.unpackLongX(pos);
        int y = BlockPos.unpackLongY(pos);
        int z = BlockPos.unpackLongZ(pos);

        return  x >= this.minX && y >= this.minY && z >= this.minZ &&
                x <= this.maxX && y <= this.maxY && z <= this.maxZ;
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.maxX >= box.minX &&
                this.minX <= box.maxX &&
                this.maxZ >= box.minZ &&
                this.minZ <= box.maxZ &&
                this.maxY >= box.minY &&
                this.minY <= box.maxY;
    }

    public int getMinValueForAxis(Direction.Axis axis)
    {
	    return switch (axis)
	    {
		    case X -> this.minX;
		    case Y -> this.minY;
		    case Z -> this.minZ;
	    };

    }

    public int getMaxValueForAxis(Direction.Axis axis)
    {
	    return switch (axis)
	    {
		    case X -> this.maxX;
		    case Y -> this.maxY;
		    case Z -> this.maxZ;
	    };

    }

    public IntBoundingBox expand(int amount)
    {
        return this.expand(amount, amount, amount);
    }

    public IntBoundingBox expand(int x, int y, int z)
    {
        return new IntBoundingBox(this.minX - x, this.minY - y, this.minZ - z,
                                  this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public IntBoundingBox shrink(int x, int y, int z)
    {
        return this.expand(-x, -y, -z);
    }

    public BlockPos getMinCorner()
    {
        return new BlockPos(this.minX, this.minY, this.minZ);
    }

    public BlockPos getMaxCorner()
    {
        return new BlockPos(this.maxX, this.maxY, this.maxZ);
    }

    public BlockBox toVanillaBox()
    {
        return new BlockBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public NbtIntArray toNbtIntArray()
    {
        return new NbtIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }

    public IntArrayData toDataIntArray()
    {
        return new IntArrayData(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }

    public JsonArray toJson()
    {
        JsonArray arr = new JsonArray();

        arr.add(new JsonPrimitive(this.minX));
        arr.add(new JsonPrimitive(this.minY));
        arr.add(new JsonPrimitive(this.minZ));
        arr.add(new JsonPrimitive(this.maxX));
        arr.add(new JsonPrimitive(this.maxY));
        arr.add(new JsonPrimitive(this.maxZ));

        return arr;
    }

    @Nullable
    public IntBoundingBox createIntersectingBox(IntBoundingBox other)
    {
        if (this.intersects(other))
        {
            int minX = Math.max(this.minX, other.minX);
            int minY = Math.max(this.minY, other.minY);
            int minZ = Math.max(this.minZ, other.minZ);
            int maxX = Math.min(this.maxX, other.maxX);
            int maxY = Math.min(this.maxY, other.maxY);
            int maxZ = Math.min(this.maxZ, other.maxZ);

            return new IntBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }

        return null;
    }

    @Nullable
    public static IntBoundingBox fromJson(JsonArray arr)
    {
        if (arr.size() == 6)
        {
            try
            {
                return new IntBoundingBox(
                        arr.get(0).getAsInt(),
                        arr.get(1).getAsInt(),
                        arr.get(2).getAsInt(),
                        arr.get(3).getAsInt(),
                        arr.get(4).getAsInt(),
                        arr.get(5).getAsInt());
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to read an IntBoundingBox from JSON '{}'", arr);
            }
        }

        return null;
    }

    public static IntBoundingBox fromVanillaBox(BlockBox box)
    {
        return createProper(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public static IntBoundingBox createProper(Vec3i pos1, Vec3i pos2)
    {
        return createProper(pos1.getX(), pos1.getY(), pos1.getZ(),
                            pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public static IntBoundingBox createProper(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new IntBoundingBox(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2),
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2));
    }

    public static IntBoundingBox createForWorldBounds(@Nullable World world)
    {
        int worldMinH = -30000000;
        int worldMaxH = 30000000;
        int worldMinY = world != null ? world.getBottomY() : -64;
        int worldMaxY = world != null ? world.getTopYInclusive() : 319;

        return new IntBoundingBox(worldMinH, worldMinY, worldMinH, worldMaxH, worldMaxY, worldMaxH);
    }

    public static IntBoundingBox fromArray(int[] coords)
    {
        if (coords.length == 6)
        {
            return new IntBoundingBox(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
        }
        else
        {
            return IntBoundingBox.ORIGIN;
        }
    }

    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == this) { return true; }
        if (otherObj == null || this.getClass() != otherObj.getClass())
        {
            return false;
        }

        if (otherObj instanceof IntBoundingBox)
        {
            IntBoundingBox other = (IntBoundingBox) otherObj;

            return  this.minX == other.minX && this.minY == other.minY && this.minZ == other.minZ &&
                    this.maxX == other.maxX && this.maxY == other.maxY && this.maxZ == other.maxZ;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    public @NonNull String toString()
    {
        return String.format("IntBoundingBox:{minX:%d, minY:%d, minZ:%d, maxX:%d, maxY:%d, maxZ:%d}\n",
                             this.minX, this.minY, this.minZ,
                             this.maxX, this.maxY, this.maxZ);
    }
}

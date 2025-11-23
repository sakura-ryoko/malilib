package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
{
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
	public static final PacketCodec<ByteBuf, IntBoundingBox> PACKET_CODEC = new PacketCodec<>()
	{
		@Override
		public void encode(ByteBuf buf, IntBoundingBox value)
		{
			PacketCodecs.INTEGER.encode(buf, value.minX);
			PacketCodecs.INTEGER.encode(buf, value.minY);
			PacketCodecs.INTEGER.encode(buf, value.minZ);
			PacketCodecs.INTEGER.encode(buf, value.maxX);
			PacketCodecs.INTEGER.encode(buf, value.maxY);
			PacketCodecs.INTEGER.encode(buf, value.maxZ);
		}

		@Override
		public IntBoundingBox decode(ByteBuf buf)
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

	public boolean containsPos(Vec3i pos)
	{
		return pos.getX() >= this.minX &&
				pos.getX() <= this.maxX &&
				pos.getZ() >= this.minZ &&
				pos.getZ() <= this.maxZ &&
				pos.getY() >= this.minY &&
				pos.getY() <= this.maxY;
	}

	public boolean containsPos(long pos)
	{
		int x = BlockPos.unpackLongX(pos);
		int y = BlockPos.unpackLongY(pos);
		int z = BlockPos.unpackLongZ(pos);

		return x >= this.minX && y >= this.minY && z >= this.minZ &&
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
		switch (axis)
		{
			case X:
				return this.minX;
			case Y:
				return this.minY;
			case Z:
				return this.minZ;
		}

		return 0;
	}

	public int getMaxValueForAxis(Direction.Axis axis)
	{
		switch (axis)
		{
			case X:
				return this.maxX;
			case Y:
				return this.maxY;
			case Z:
				return this.maxZ;
		}

		return 0;
	}

	public BlockBox toVanillaBox()
	{
		return new BlockBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public NbtIntArray toNBTIntArray()
	{
		return new NbtIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}

	public static IntBoundingBox fromVanillaBox(BlockBox box)
	{
		return createProper(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
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
			return new IntBoundingBox(0, 0, 0, 0, 0, 0);
		}
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

}

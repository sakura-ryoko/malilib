package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * See {@link fi.dy.masa.malilib.util.position.IntBoundingBox}
 */
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
	public static final StreamCodec<@NotNull ByteBuf, @NotNull IntBoundingBox> PACKET_CODEC = new StreamCodec<>()
	{
		@Override
		public void encode(ByteBuf buf, IntBoundingBox value)
		{
			ByteBufCodecs.INT.encode(buf, value.minX);
			ByteBufCodecs.INT.encode(buf, value.minY);
			ByteBufCodecs.INT.encode(buf, value.minZ);
			ByteBufCodecs.INT.encode(buf, value.maxX);
			ByteBufCodecs.INT.encode(buf, value.maxY);
			ByteBufCodecs.INT.encode(buf, value.maxZ);
		}

		@Override
		public IntBoundingBox decode(ByteBuf buf)
		{
			return new IntBoundingBox(
					ByteBufCodecs.INT.decode(buf),
					ByteBufCodecs.INT.decode(buf),
					ByteBufCodecs.INT.decode(buf),
					ByteBufCodecs.INT.decode(buf),
					ByteBufCodecs.INT.decode(buf),
					ByteBufCodecs.INT.decode(buf)
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
		int x = BlockPos.getX(pos);
		int y = BlockPos.getY(pos);
		int z = BlockPos.getZ(pos);

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
		return switch (axis)
		{
			case X -> this.minX;
			case Y -> this.minY;
			case Z -> this.minZ;
			default -> 0;
		};
	}

	public int getMaxValueForAxis(Direction.Axis axis)
	{
		return switch (axis)
		{
			case X -> this.maxX;
			case Y -> this.maxY;
			case Z -> this.maxZ;
			default -> 0;
		};
	}

	public BoundingBox toVanillaBox()
	{
		return new BoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public IntArrayTag toNBTIntArray()
	{
		return new IntArrayTag(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}

	public static IntBoundingBox fromVanillaBox(BoundingBox box)
	{
		return createProper(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
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

	public static IntBoundingBox createForWorldBounds(@Nullable Level world)
	{
		int worldMinH = -30000000;
		int worldMaxH = 30000000;
		int worldMinY = world != null ? world.getMinY() : -64;
		int worldMaxY = world != null ? world.getMaxY() : 319;

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

package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.util.MathUtils;

public class VecBox
{
	public static final Codec<VecBox> CODEC = RecordCodecBuilder.create(
			inst -> inst.group(
					PrimitiveCodec.DOUBLE.fieldOf("minX").forGetter(get -> get.minX),
					PrimitiveCodec.DOUBLE.fieldOf("minY").forGetter(get -> get.minY),
					PrimitiveCodec.DOUBLE.fieldOf("minZ").forGetter(get -> get.minZ),
					PrimitiveCodec.DOUBLE.fieldOf("maxX").forGetter(get -> get.maxX),
					PrimitiveCodec.DOUBLE.fieldOf("maxY").forGetter(get -> get.maxY),
					PrimitiveCodec.DOUBLE.fieldOf("maxZ").forGetter(get -> get.maxZ)
			).apply(inst, VecBox::new)
	);
	public static final StreamCodec<@NotNull ByteBuf, @NotNull VecBox> PACKET_CODEC = new StreamCodec<>()
	{
		@Override
		public void encode(@Nonnull ByteBuf buf, VecBox value)
		{
			ByteBufCodecs.DOUBLE.encode(buf, value.minX);
			ByteBufCodecs.DOUBLE.encode(buf, value.minY);
			ByteBufCodecs.DOUBLE.encode(buf, value.minZ);
			ByteBufCodecs.DOUBLE.encode(buf, value.maxX);
			ByteBufCodecs.DOUBLE.encode(buf, value.maxY);
			ByteBufCodecs.DOUBLE.encode(buf, value.maxZ);
		}

		@Override
		public @Nonnull VecBox decode(@Nonnull ByteBuf buf)
		{
			return new VecBox(
					ByteBufCodecs.DOUBLE.decode(buf),
					ByteBufCodecs.DOUBLE.decode(buf),
					ByteBufCodecs.DOUBLE.decode(buf),
					ByteBufCodecs.DOUBLE.decode(buf),
					ByteBufCodecs.DOUBLE.decode(buf),
					ByteBufCodecs.DOUBLE.decode(buf)
			);
		}
	};

	public final static VecBox ZERO = new VecBox(Vec3d.ZERO);
	public final double minX;
	public final double minY;
	public final double minZ;
	public final double maxX;
	public final double maxY;
	public final double maxZ;

	public VecBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ)
	{
		this.minX = MathUtils.min(minX, maxX);
		this.minY = MathUtils.min(minY, maxY);
		this.minZ = MathUtils.min(minZ, maxZ);
		this.maxX = MathUtils.max(maxX, minX);
		this.maxY = MathUtils.max(maxY, minY);
		this.maxZ = MathUtils.max(maxZ, minZ);
	}

	public VecBox(Vec3d pos)
	{
		this(pos.x, pos.y, pos.z, pos.x + 1.0F, pos.y + 1.0F, pos.z + 1.0F);
	}

	public VecBox(BlockPos pos)
	{
		this(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}

	public VecBox(final Vec3d min, final Vec3d max)
	{
		this(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public VecBox(IntBoundingBox bb)
	{
		this(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());
	}

	public static VecBox of(final Vec3 min, final Vec3 max)
	{
		return new VecBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public static VecBox of(final BlockPos p0, final BlockPos p1)
	{
		return new VecBox(
				MathUtils.min(p0.getX(), p1.getX()),
				MathUtils.min(p0.getY(), p1.getY()),
				MathUtils.min(p0.getZ(), p1.getZ()),
				MathUtils.max(p0.getX(), p1.getX()) + 1,
				MathUtils.max(p0.getY(), p1.getY()) + 1,
				MathUtils.max(p0.getZ(), p1.getZ()) + 1
		);
	}

	public static VecBox of(final net.minecraft.core.BlockPos p0, final net.minecraft.core.BlockPos p1)
	{
		return new VecBox(
				MathUtils.min(p0.getX(), p1.getX()),
				MathUtils.min(p0.getY(), p1.getY()),
				MathUtils.min(p0.getZ(), p1.getZ()),
				MathUtils.max(p0.getX(), p1.getX()) + 1,
				MathUtils.max(p0.getY(), p1.getY()) + 1,
				MathUtils.max(p0.getZ(), p1.getZ()) + 1
		);
	}

	public static VecBox of(AABB bb)
	{
		return new VecBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}

	public static VecBox of(final Vec3d center, final double sizeX, final double sizeY, final double sizeZ)
	{
		return new VecBox(
				center.x - sizeX / 2.0,
				center.y - sizeY / 2.0,
				center.z - sizeZ / 2.0,
				center.x + sizeX / 2.0,
				center.y + sizeY / 2.0,
				center.z + sizeZ / 2.0
		);
	}

	public VecBox setMinX(final double minX)
	{
		return new VecBox(minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public VecBox setMinY(final double minY)
	{
		return new VecBox(this.minX, minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public VecBox setMinZ(final double minZ)
	{
		return new VecBox(this.minX, this.minY, minZ, this.maxX, this.maxY, this.maxZ);
	}

	public VecBox setMaxX(final double maxX)
	{
		return new VecBox(this.minX, this.minY, this.minZ, maxX, this.maxY, this.maxZ);
	}

	public VecBox setMaxY(final double maxY)
	{
		return new VecBox(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ);
	}

	public VecBox setMaxZ(final double maxZ)
	{
		return new VecBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, maxZ);
	}

	public double getMinAxis(Direction.Axis axis)
	{
		return axis.pick(this.minX, this.minY, this.minZ);
	}

	public double getMaxAxis(Direction.Axis axis)
	{
		return axis.pick(this.maxX, this.maxY, this.maxZ);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (!(o instanceof VecBox bb))
		{
			return false;
		}
		else if (Double.compare(bb.minX, this.minX) != 0)
		{
			return false;
		}
		else if (Double.compare(bb.minY, this.minY) != 0)
		{
			return false;
		}
		else if (Double.compare(bb.minZ, this.minZ) != 0)
		{
			return false;
		}
		else if (Double.compare(bb.maxX, this.maxX) != 0)
		{
			return false;
		}
		else
		{
			return Double.compare(bb.maxY, this.maxY) == 0 && Double.compare(bb.maxZ, this.maxZ) == 0;
		}
	}

	@Override
	public int hashCode()
	{
		int result = Double.hashCode(this.minX);

		result = 31 * result + Double.hashCode(this.minY);
		result = 31 * result + Double.hashCode(this.minZ);
		result = 31 * result + Double.hashCode(this.maxX);
		result = 31 * result + Double.hashCode(this.maxY);

		return 31 * result + Double.hashCode(this.maxZ);
	}

	@Override
	public String toString()
	{
		return String.format("VecBox:{minX=%f, minY=%f, minZ=%f, maxX=%f, maxY=%f, maxZ=%f}",
		                     this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public VecBox contract(final double x, final double y, final double z)
	{
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;

		if (x < 0.0)
		{
			minX -= x;
		}
		else if (x > 0.0)
		{
			maxX -= x;
		}

		if (y < 0.0)
		{
			minY -= y;
		}
		else if (y > 0.0)
		{
			maxY -= y;
		}
		if (z < 0.0)
		{
			minZ -= z;
		}
		else if (z > 0.0)
		{
			maxZ -= z;
		}

		return new VecBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public VecBox expand(Vec3d scale)
	{
		return this.expand(scale.x, scale.y, scale.z);
	}

	public VecBox expand(final double x, final double y, final double z)
	{
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;

		if (x < 0.0)
		{
			minX += x;
		}
		else if (x > 0.0)
		{
			maxX += x;
		}

		if (y < 0.0)
		{
			minY += y;
		}
		else if (y > 0.0)
		{
			maxY += y;
		}

		if (z < 0.0)
		{
			minZ += z;
		}
		else if (z > 0.0)
		{
			maxZ += z;
		}

		return new VecBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public VecBox inflate(final double value)
	{
		return this.inflate(value, value, value);
	}

	public VecBox inflate(final double x, final double y, final double z)
	{
		double minX = this.minX - x;
		double minY = this.minY - y;
		double minZ = this.minZ - z;
		double maxX = this.maxX + x;
		double maxY = this.maxY + y;
		double maxZ = this.maxZ + z;

		return new VecBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public VecBox deflate(final double x, final double y, final double z)
	{
		return this.inflate(-x, -y, -z);
	}

	public VecBox deflate(final double value)
	{
		return this.inflate(-value);
	}

	public VecBox intersect(final VecBox bb)
	{
		final double minX = MathUtils.max(bb.minX, this.minX);
		final double minY = MathUtils.max(bb.minY, this.minY);
		final double minZ = MathUtils.max(bb.minZ, this.minZ);
		final double maxX = MathUtils.min(bb.maxX, this.maxX);
		final double maxY = MathUtils.min(bb.maxY, this.maxY);
		final double maxZ = MathUtils.min(bb.maxZ, this.maxZ);

		return new VecBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public VecBox union(final VecBox bb)
	{
		final double minX = MathUtils.min(bb.minX, this.minX);
		final double minY = MathUtils.min(bb.minY, this.minY);
		final double minZ = MathUtils.min(bb.minZ, this.minZ);
		final double maxX = MathUtils.max(bb.maxX, this.maxX);
		final double maxY = MathUtils.max(bb.maxY, this.maxY);
		final double maxZ = MathUtils.max(bb.maxZ, this.maxZ);

		return new VecBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public VecBox move(final double x, final double y, final double z)
	{
		return new VecBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	public VecBox move(final BlockPos pos)
	{
		return this.move(pos.getX(), pos.getY(), pos.getZ());
	}

	public VecBox move(final Vec3d vec)
	{
		return this.move(vec.x, vec.y, vec.z);
	}

	public VecBox move(final Vector3f vector)
	{
		return this.move(vector.x, vector.y, vector.z);
	}

	public boolean intersects(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ)
	{
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
	}

	public boolean intersects(final VecBox bb)
	{
		return this.intersects(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}

	public boolean intersects(final BlockPos pos)
	{
		return this.intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}

	public boolean intersects(final Vec3d min, final Vec3d max)
	{
		return this.intersects(
				MathUtils.min(min.x, max.x), MathUtils.min(min.y, max.y), MathUtils.min(min.z, max.z),
				MathUtils.max(min.x, max.x), MathUtils.max(min.y, max.y), MathUtils.max(min.z, max.z));
	}

	public boolean contains(final double x, final double y, final double z)
	{
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
	}

	public boolean contains(final BlockPos pos)
	{
		return this.contains(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean contains(final Vec3d vec)
	{
		return this.contains(vec.x, vec.y, vec.z);
	}

	public double getSizeX() {
		return this.maxX - this.minX;
	}

	public double getSizeY() {
		return this.maxY - this.minY;
	}

	public double getSizeZ() {
		return this.maxZ - this.minZ;
	}

	public double getSize()
	{
		double x = this.getSizeX();
		double y = this.getSizeY();
		double z = this.getSizeZ();

		return (x + y + z) / 3.0;
	}

	public double distanceSquared(final Vec3d vec)
	{
		final double x = MathUtils.max(MathUtils.max(this.minX - vec.x, vec.x - this.maxX), 0.0F);
		final double y = MathUtils.max(MathUtils.max(this.minY - vec.y, vec.y - this.maxY), 0.0F);
		final double z = MathUtils.max(MathUtils.max(this.minZ - vec.z, vec.z - this.maxZ), 0.0F);

		return x * x + y * y + z * z;
	}

	public double distanceSquared(final VecBox bb)
	{
		final double x = MathUtils.max(MathUtils.max(this.minX - bb.maxX, bb.minX - this.maxX), 0.0F);
		final double y = MathUtils.max(MathUtils.max(this.minY - bb.maxY, bb.minY - this.maxY), 0.0F);
		final double z = MathUtils.max(MathUtils.max(this.minZ - bb.maxZ, bb.minZ - this.maxZ), 0.0F);

		return x * x + y * y + z * z;
	}

	public boolean isNaN()
	{
		return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
	}

	public Vec3d getCenter()
	{
		return new Vec3d(MathUtils.lerp(0.5, this.minX, this.maxX), MathUtils.lerp(0.5, this.minY, this.maxY), MathUtils.lerp(0.5, this.minZ, this.maxZ));
	}

	public Vec3d getBottomCenter()
	{
		return new Vec3d(MathUtils.lerp(0.5, this.minX, this.maxX), this.minY, MathUtils.lerp(0.5, this.minZ, this.maxZ));
	}

	public Vec3d getMinPos()
	{
		return new Vec3d(this.minX, this.minY, this.minZ);
	}

	public Vec3d getMaxPos()
	{
		return new Vec3d(this.maxX, this.maxY, this.maxZ);
	}

	public AABB toVanilla()
	{
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}
}

package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.ChunkSectionPos;

import fi.dy.masa.malilib.util.MathUtils;

public record ChunkPos(int x, int z)
{
	public static final Codec<ChunkPos> CODEC = RecordCodecBuilder.create(
			inst -> inst.group(
					PrimitiveCodec.INT.fieldOf("x").forGetter(ChunkPos::x),
					PrimitiveCodec.INT.fieldOf("z").forGetter(ChunkPos::z)
			).apply(inst, ChunkPos::new)
	);
	public static final PacketCodec<@NotNull ByteBuf, @NotNull ChunkPos> PACKET_CODEC = new PacketCodec<>()
	{
		@Override
		public void encode(@Nonnull ByteBuf buf, ChunkPos value)
		{
			PacketCodecs.INTEGER.encode(buf, value.x());
			PacketCodecs.INTEGER.encode(buf, value.z());
		}

		@Override
		public @Nonnull ChunkPos decode(@Nonnull ByteBuf buf)
		{
			return new ChunkPos(
					PacketCodecs.INTEGER.decode(buf),
					PacketCodecs.INTEGER.decode(buf)
			);
		}
	};

	public static final ChunkPos ZERO = new ChunkPos(0, 0);

	public static ChunkPos from(BlockPos pos)
	{
		return new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	public static ChunkPos from(net.minecraft.util.math.BlockPos pos)
	{
		return new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	public static ChunkPos getMinRegion(final int rx, final int rz)
	{
		return new ChunkPos(rx << 5, rz << 5);
	}
	public static ChunkPos getMaxRegion(final int rx, final int rz)
	{
		return new ChunkPos((rx << 5) + 31, (rz << 5) + 31);
	}

	public static int getX(final long pos)
	{
		return (int) (pos & 4294967295L);
	}

	public static int getZ(final long pos)
	{
		return (int) (pos >>> 32 & 4294967295L);
	}

	public int getMinBlockX()
	{
		return ChunkSectionPos.getBlockCoord(this.x);
	}

	public int getMinBlockZ()
	{
		return ChunkSectionPos.getBlockCoord(this.z);
	}

	public int getMaxBlockX()
	{
		return this.getBlockX(15);
	}

	public int getMaxBlockZ()
	{
		return this.getBlockZ(15);
	}

	public int getBlockX(final int offset)
	{
		return ChunkSectionPos.getOffsetPos(this.x, offset);
	}

	public int getBlockZ(final int offset)
	{
		return ChunkSectionPos.getOffsetPos(this.z, offset);
	}

	@Override
	public String toString()
	{
		return "ChunkPos:{x=" + this.x + ", z=" + this.z + "}";
	}

	public BlockPos getWorldPosition()
	{
		return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
	}

	public int getChessboardDistance(final net.minecraft.util.math.ChunkPos pos)
	{
		return this.getChessboardDistance(pos.x, pos.z);
	}

	public int getChessboardDistance(final ChunkPos pos)
	{
		return this.getChessboardDistance(pos.x, pos.z);
	}

	public int getChessboardDistance(final int x, final int z)
	{
		return MathUtils.chessboardDistance(x, z, this.x, this.z);
	}

	public int distanceSquared(final net.minecraft.util.math.ChunkPos pos)
	{
		return this.distanceSquared(pos.x, pos.z);
	}

	public int distanceSquared(final ChunkPos pos)
	{
		return this.distanceSquared(pos.x, pos.z);
	}

	public int distanceSquared(final long pos)
	{
		return this.distanceSquared(getX(pos), getZ(pos));
	}

	private int distanceSquared(final int x, final int z)
	{
		int deltaX = x - this.x;
		int deltaZ = z - this.z;
		return deltaX * deltaX + deltaZ * deltaZ;
	}

	@Override
	public int hashCode()
	{
		return hash(this.x, this.z);
	}

	public static int hash(final int x, final int z)
	{
		int xt = 1664525 * x + 1013904223;
		int zt = 1664525 * (z ^ -559038737) + 1013904223;
		return xt ^ zt;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		final ChunkPos that = (ChunkPos) o;
		return this.x == that.x && this.z == that.z;
	}

	public static long asLong(int chunkX, int chunkZ)
	{
		return ((long) chunkZ << 32) | ((long) chunkX & 0xFFFFFFFFL);
	}

	public static ChunkPos of(final long pos)
	{
		return new ChunkPos((int) pos, (int) (pos >> 32));
	}

	public static ChunkPos of(net.minecraft.util.math.ChunkPos pos)
	{
		return new ChunkPos(pos.x, pos.z);
	}
}

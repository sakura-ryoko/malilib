package fi.dy.masa.malilib.util;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SubChunkPos extends Vec3i
{
    public static final Codec<SubChunkPos> BLOCK_POS_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(BlockPos::new)
            ).apply(inst, SubChunkPos::new)
    );
    public static final Codec<SubChunkPos> VEC3I_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(Vec3i::getZ)
            ).apply(inst, SubChunkPos::new)
    );
    public static final Codec<SubChunkPos> CODEC = VEC3I_CODEC;

    public SubChunkPos(BlockPos pos)
    {
        this(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public SubChunkPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public static class DistanceComparator implements Comparator<SubChunkPos>
    {
        private final SubChunkPos referencePosition;

        public DistanceComparator(SubChunkPos referencePosition)
        {
            this.referencePosition = referencePosition;
        }

        @Override
        public int compare(SubChunkPos pos1, SubChunkPos pos2)
        {
            int x = this.referencePosition.getX();
            int y = this.referencePosition.getY();
            int z = this.referencePosition.getZ();

            double dist1 = pos1.getSquaredDistance(x, y, z);
            double dist2 = pos2.getSquaredDistance(x, y, z);

            return Double.compare(dist1, dist2);
        }
    }
}

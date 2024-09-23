package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class NBTUtils
{
    public static NbtCompound createBlockPosTag(Vec3i pos)
    {
        return writeBlockPosToTag(pos, new NbtCompound());
    }

    public static NbtCompound writeBlockPosToTag(Vec3i pos, NbtCompound tag)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            tag.contains("x", Constants.NBT.TAG_INT) &&
            tag.contains("y", Constants.NBT.TAG_INT) &&
            tag.contains("z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromIntArray(@Nonnull NbtCompound nbt, String key)
    {
        if (nbt.contains(key, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] array = nbt.getIntArray(key);

            return new BlockPos(array[0], array[1], array[2]);
        }

        return null;
    }

    public static NbtCompound writeBlockPosToNbtIntArray(BlockPos pos, String key)
    {
        NbtCompound nbt = new NbtCompound();
        int[] array = {pos.getX(), pos.getY(), pos.getZ()};
        nbt.putIntArray(key, array);

        return nbt;
    }

    public static NbtCompound writeVec3dToTag(Vec3d vec, NbtCompound tag)
    {
        tag.putDouble("dx", vec.x);
        tag.putDouble("dy", vec.y);
        tag.putDouble("dz", vec.z);
        return tag;
    }

    public static NbtCompound writeEntityPositionToTag(Vec3d pos, NbtCompound tag)
    {
        NbtList posList = new NbtList();

        posList.add(NbtDouble.of(pos.x));
        posList.add(NbtDouble.of(pos.y));
        posList.add(NbtDouble.of(pos.z));
        tag.put("Pos", posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            tag.contains("dx", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dy", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vec3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readEntityPositionFromTag(@Nullable NbtCompound tag)
    {
        if (tag != null && tag.contains("Pos", Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tag.getList("Pos", Constants.NBT.TAG_DOUBLE);

            if (tagList.getHeldType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }
}

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

    public static NbtCompound writeBlockPosToTag(Vec3i pos, @Nonnull NbtCompound tag)
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

    /**
     * Read an IntArray type BlockPos from NBT.
     *
     * @param nbt ()
     * @param key ()
     * @return ()
     */
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

    /**
     * Write a Block pos as an IntArray in NBT.
     *
     * @param pos ()
     * @param key ()
     * @return ()
     */
    public static NbtCompound writeBlockPosToNbtIntArray(BlockPos pos, String key)
    {
        NbtCompound nbt = new NbtCompound();
        int[] array = {pos.getX(), pos.getY(), pos.getZ()};
        nbt.putIntArray(key, array);

        return nbt;
    }

    public static NbtCompound writeVec3dToTag(Vec3d vec, @Nonnull NbtCompound tag)
    {
        tag.putDouble("dx", vec.x);
        tag.putDouble("dy", vec.y);
        tag.putDouble("dz", vec.z);
        return tag;
    }

    public static NbtCompound writeEntityPositionToTag(Vec3d pos, @Nonnull NbtCompound tag)
    {
        NbtList posList = new NbtList();

        posList.add(NbtDouble.of(pos.x));
        posList.add(NbtDouble.of(pos.y));
        posList.add(NbtDouble.of(pos.z));
        tag.put(NbtKeys.POS, posList);

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
        if (tag != null && tag.contains(NbtKeys.POS, Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tag.getList(NbtKeys.POS, Constants.NBT.TAG_DOUBLE);

            if (tagList.getHeldType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }

    /**
     * Read the "BlockAttached" BlockPos from NBT.
     *
     * @param tag ()
     * @return ()
     */
    @Nullable
    public static BlockPos readAttachedPosFromTag(@Nonnull NbtCompound tag)
    {
        if (tag.contains("TileX", Constants.NBT.TAG_INT) &&
            tag.contains("TileY", Constants.NBT.TAG_INT) &&
            tag.contains("TileZ", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("TileX"), tag.getInt("TileY"), tag.getInt("TileZ"));
        }

        return null;
    }

    /**
     * Write the "Block Attached" BlockPos to NBT.
     *
     * @param pos ()
     * @param tag ()
     * @return ()
     */
    public static NbtCompound writeAttachedPosToTag(BlockPos pos, @Nonnull NbtCompound tag)
    {
        tag.putInt("TileX", pos.getX());
        tag.putInt("TileY", pos.getY());
        tag.putInt("TileZ", pos.getZ());

        return tag;
    }
}

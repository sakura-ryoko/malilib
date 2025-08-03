package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import malilib.MaLiLib;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.converter.DataConverterNbt;

public class BlockWrap
{
    @Nullable
    public static CompoundData writeBlockEntityToTag(TileEntity be)
    {
        try
        {
            NBTTagCompound tag = be.writeToNBT(new NBTTagCompound());

            if (tag != null)
            {
                return DataConverterNbt.fromVanillaCompound(tag);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Failed to write BlockEntity {} to NBT", be, e);
        }

        return null;
    }

    public static boolean readBlockEntityFrom(TileEntity be, CompoundData data)
    {
        try
        {
            NBTTagCompound tag = DataConverterNbt.toVanillaCompound(data);

            if (tag != null)
            {
                be.readFromNBT(tag);
                return true;
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Failed to read BlockEntity {} from NBT '{}'", be, data, e);
        }

        return false;
    }
}

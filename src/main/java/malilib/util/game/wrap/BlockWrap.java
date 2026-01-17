package malilib.util.game.wrap;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

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

    @Nullable
    public static ILockableContainer getLockableContainer(World worldIn, BlockPos pos, Block blockIn,
                                                          TileEntityChest chestBe, boolean bypassBlockedCheck)
    {
        if (bypassBlockedCheck == false && isChestBlocked(worldIn, pos))
        {
            return null;
        }

        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos sidePos = pos.offset(side);
            Block blockSide = worldIn.getBlockState(sidePos).getBlock();

            if (blockSide != blockIn)
            {
                continue;
            }

            if (bypassBlockedCheck == false && isChestBlocked(worldIn, sidePos))
            {
                return null;
            }

            TileEntity beSide = worldIn.getTileEntity(sidePos);

            if (beSide instanceof TileEntityChest)
            {
                if (side != EnumFacing.WEST && side != EnumFacing.NORTH)
                {
                    return new InventoryLargeChest("container.chestDouble", chestBe, (TileEntityChest) beSide);
                }
                else
                {
                    return new InventoryLargeChest("container.chestDouble", (TileEntityChest) beSide, chestBe);
                }
            }
        }

        return chestBe;
    }

    public static boolean isChestBlocked(World worldIn, BlockPos pos)
    {
        return isBelowSolidBlock(worldIn, pos) || isOcelotSittingOnBlock(worldIn, pos);
    }

    public static boolean isBelowSolidBlock(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.up()).isNormalCube();
    }

    public static boolean isOcelotSittingOnBlock(World worldIn, BlockPos pos)
    {
        AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        List<Entity> list = worldIn.getEntitiesWithinAABB(EntityOcelot.class, bb);

        for (Entity entity : list)
        {
            if (((EntityOcelot) entity).isSitting())
            {
                return true;
            }
        }

        return false;
    }
}

package fi.dy.masa.malilib.sync;

import javax.annotation.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.sync.data.*;
import fi.dy.masa.malilib.test.TestDataSync;
import fi.dy.masa.malilib.util.InventoryUtils;

public class SyncUtils
{
    @SuppressWarnings("unchecked")
    public static @Nullable <T extends SyncData> Inventory getInventory(World world, BlockPos pos)
    {
        Inventory inv = InventoryUtils.getInventory(world, pos);

        if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue() &&
            TestDataSync.getInstance().hasBlockEntity(pos))
        {
            T fbe = (T) TestDataSync.getInstance().getBlockEntity(pos);

            if (InventoryUtils.fbeHasItems(fbe))
            {
                inv = InventoryUtils.getAsInventory(InventoryUtils.getStoredItems(fbe));
            }
        }

        if ((inv == null || inv.isEmpty()) && !MinecraftClient.getInstance().isIntegratedServerRunning()
            && world.getBlockState(pos).getBlock() instanceof BlockEntityProvider
            && MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            TestDataSync.getInstance().requestBlockEntityAt(world, pos);
        }

        return inv;
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends SyncData> T getFakeBlockEntity(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);

        if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue() &&
            TestDataSync.getInstance().hasBlockEntity(pos))
        {
            return (T) TestDataSync.getInstance().getBlockEntity(pos);
        }

        if (world.isClient
            && state.getBlock() instanceof BlockEntityProvider
            && MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            TestDataSync.getInstance().requestBlockEntityAt(world, pos);
        }
        else if (state.hasBlockEntity())
        {
            BlockEntity be = world.getWorldChunk(pos).getBlockEntity(pos);

            return toFakeBlockEntity(be);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends SyncData> T getFakeEntity(Entity entity)
    {
        int entityId = entity.getId();

        if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue() &&
            TestDataSync.getInstance().hasEntity(entityId))
        {
            return (T) TestDataSync.getInstance().getEntity(entityId);
        }

        if (entity.getWorld().isClient &&
            MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            TestDataSync.getInstance().requestEntity(entityId);

            return (T) TestDataSync.getInstance().getCache().createEntity(entity);
        }
        else
        {
            return toFakeEntity(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends SyncData> T toFakeBlockEntity(BlockEntity be)
    {
        switch (be)
        {
            case BeehiveBlockEntity ignored ->
            {
                return (T) new SyncBees(be);
            }
            case LecternBlockEntity ignored ->
            {
                return (T) new SyncBooks(be);
            }
            case ChiseledBookshelfBlockEntity ignored ->
            {
                return (T) new SyncBooks(be);
            }
            case BrewingStandBlockEntity ignored ->
            {
                return (T) new SyncBrewer(be);
            }
            case BannerBlockEntity ignored ->
            {
                return (T) new SyncPatterns(be);
            }
            case SkullBlockEntity ignored ->
            {
                return (T) new SyncProfile(be);
            }
            case SingleStackInventory.SingleStackBlockEntityInventory ignored ->
            {
                return (T) new SyncSingleStack(be);
            }
            case Inventory ignored ->
            {
                return (T) new SyncInventory(be);
            }
            case null ->
            {
                return null;
            }
            default ->
            {
                T var = (T) new SyncGeneric(be);
                var.copyNbtFromBlockEntity(be);
                return var;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends SyncData> T toFakeEntity(Entity e)
    {
        switch (e)
        {
            case MerchantEntity ignored ->
            {
                return (T) new SyncMerchant(e);
            }
            case InventoryOwner ignored ->
            {
                return (T) new SyncMerchant(e);
            }
            case AbstractHorseEntity ignored ->
            {
                return (T) new SyncHorse(e);
            }
            case LivingEntity ignored ->
            {
                return (T) new SyncEquipment(e);
            }
            case ItemEntity ignored ->
            {
                return (T) new SyncSingleStack(e);
            }
            case null ->
            {
                return null;
            }
            default ->
            {
                T var = (T) new SyncGeneric(e);
                var.copyNbtFromEntity(e);
                return var;
            }
        }
    }
}

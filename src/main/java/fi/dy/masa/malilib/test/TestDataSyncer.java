package fi.dy.masa.malilib.test;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;

public class TestDataSyncer
{
    private static final TestDataSyncer INSTANCE = new TestDataSyncer();

    public TestDataSyncer()
    {
    }

    public static TestDataSyncer getInstance() { return INSTANCE; }

    private World getWorld()
    {
        return WorldUtils.getBestWorld(MinecraftClient.getInstance());
    }

    public Pair<Entity, NbtCompound> requestEntity(int entityId)
    {
        if (this.getWorld() != null)
        {
            Entity entity = this.getWorld().getEntityById(entityId);
            NbtCompound nbt = new NbtCompound();

            if (entity != null && entity.saveSelfNbt(nbt))
            {
                return Pair.of(entity, nbt);
            }
        }

        return null;
    }

    public Pair<BlockEntity, NbtCompound> requestBlockEntity(World world, BlockPos pos)
    {
        if (world.getBlockState(pos).getBlock() instanceof BlockEntityProvider)
        {
            BlockEntity be = world.getWorldChunk(pos).getBlockEntity(pos);

            if (be != null)
            {
                NbtCompound nbt = be.createNbtWithIdentifyingData(world.getRegistryManager());

                return Pair.of(be, nbt);
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public Inventory getBlockInventory(World world, BlockPos pos, boolean useNbt)
    {
        Inventory inv = null;

        Pair<BlockEntity, NbtCompound> pair = requestBlockEntity(world, pos);

        if (pair == null)
        {
            return null;
        }

        if (useNbt)
        {
            inv = InventoryUtils.getNbtInventory(pair.getRight(), -1, world.getRegistryManager());
        }
        else
        {
            BlockEntity be = pair.getLeft();

            if (be instanceof Inventory inv1)
            {
                if (be instanceof ChestBlockEntity)
                {
                    BlockState state = world.getBlockState(pos);
                    ChestType type = state.get(ChestBlock.CHEST_TYPE);

                    if (type != ChestType.SINGLE)
                    {
                        BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));
                        if (!world.isChunkLoaded(posAdj)) return null;
                        BlockState stateAdj = world.getBlockState(posAdj);

                        Pair<BlockEntity, NbtCompound> pairAdj = this.requestBlockEntity(world, posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                            pairAdj.getLeft() instanceof ChestBlockEntity inv2 &&
                            stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
                            stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
                        {
                            Inventory invRight = type == ChestType.RIGHT ? inv1 : inv2;
                            Inventory invLeft = type == ChestType.RIGHT ? inv2 : inv1;

                            inv = new DoubleInventory(invRight, invLeft);
                        }
                    }
                    else
                    {
                        inv = inv1;
                    }
                }
                else
                {
                    inv = inv1;
                }
            }
        }

        return inv;
    }
}

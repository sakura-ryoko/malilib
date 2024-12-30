package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDataSyncer
{
    void reset(boolean isLogout);

    void onGameInit();

    void onWorldPre();

    void onWorldJoin();

    @Nullable
    NbtCompound getFromBlockEntityCacheNbt(BlockPos pos);

    @Nullable
    BlockEntity getFromBlockEntityCache(BlockPos pos);

    @Nullable
    NbtCompound getFromEntityCacheNbt(int entityId);

    @Nullable
    Entity getFromEntityCache(int entityId);

    Pair<BlockEntity, NbtCompound> requestBlockEntity(World world, BlockPos pos);

    Pair<Entity, NbtCompound> requestEntity(int entityId);

    Inventory getBlockInventory(World world, BlockPos pos, boolean useNbt);

    Inventory getEntityInventory(World world, BlockPos pos, boolean useNbt);

    BlockEntity handleBlockEntityData(BlockPos pos, NbtCompound nbt, @Nullable Identifier type);

    Entity handleEntityData(int entityId, NbtCompound nbt);

    void handleBulkEntityData(int transactionId, NbtCompound nbt);

    void handleVanillaQueryNbt(int transactionId, NbtCompound nbt);
}

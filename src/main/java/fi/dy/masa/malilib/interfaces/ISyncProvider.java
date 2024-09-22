package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.sync.data.SyncData;
import fi.dy.masa.malilib.sync.SyncDataCache;

public interface ISyncProvider
{
    void onInstanceStart();

    void onInstanceStop();

    void onStartServices(World world);

    void onStopServices();

    World getWorld();

    SyncDataCache<? extends SyncData,? extends SyncData> getCache();

    void requestBlockEntity(BlockPos pos, @Nullable BlockState state);

    void requestBlockEntityAt(World world, BlockPos pos);

    void requestEntity(int entityId);

    default void requestBulkData(ChunkPos chunkPos, @Nullable Box boundingBox) {}

    void handleVanillaQueryNbt(int transactionId, NbtCompound nbt);

    void onReceiveBlockEntity(BlockPos pos, NbtCompound nbt);

    void onReceiveEntity(int entityId, NbtCompound nbt);

    default void onReceiveBulkData(ChunkPos chunkPos, @Nonnull NbtCompound nbtCompound) {}

    boolean hasBlockEntity(BlockPos pos);

    boolean hasEntity(int entityId);

    default boolean hasBulkData(ChunkPos pos) {return false;}

    @Nullable <B extends SyncData> B getBlockEntity(BlockPos pos);

    @Nullable <E extends SyncData> E getEntity(int entityId);
}

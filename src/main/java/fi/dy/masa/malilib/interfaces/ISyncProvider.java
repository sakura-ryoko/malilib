package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.sync.fbe.FakeBlockEntity;
import fi.dy.masa.malilib.sync.fe.FakeEntity;
import fi.dy.masa.malilib.sync.cache.SyncCache;

public interface ISyncProvider
{
    void onInstanceStart();

    void onInstanceStop();

    void onStartServices(World world);

    void onStopServices();

    World getWorld();

    SyncCache getCache();

    void requestBlockEntity(BlockPos pos, @Nullable BlockState state);

    void requestEntity(int entityId);

    default void requestBulkData(ChunkPos chunkPos, @Nullable Box boundingBox) {}

    void onReceiveBlockEntity(BlockPos pos, NbtCompound nbt);

    void onReceiveEntity(int entityId, NbtCompound nbt);

    default void onReceiveBulkData(ChunkPos chunkPos, @Nonnull NbtCompound nbtList) {}

    default boolean hasBulkData(ChunkPos pos) { return false; }

    @Nullable FakeBlockEntity getBlockEntity(BlockPos pos);

    @Nullable FakeEntity getEntity(int entityId);
}

package fi.dy.masa.malilib.sync.fc;

import java.util.Map;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;

public abstract class FakeChunk
{
    private final World world;
    protected final ChunkPos pos;
    protected final Map<BlockPos, NbtCompound> blockEntityNbts = Maps.newHashMap();
    protected final Map<BlockPos, BlockEntity> blockEntities = new Object2ObjectOpenHashMap();

    public FakeChunk(World world, ChunkPos pos)
    {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld()
    {
        return this.world;
    }

    public ChunkPos getPos()
    {
        return this.pos;
    }

    public abstract ChunkStatus getStatus();

    public abstract void setBlockEntity(BlockEntity blockEntity);

    public abstract void addEntity(Entity entity);

    public abstract void removeBlockEntity(BlockPos pos);

    public void addPendingBlockEntityNbt(NbtCompound nbt)
    {
        this.blockEntityNbts.put(BlockEntity.posFromNbt(nbt), nbt);
    }

    public BlockState getBlockState(BlockPos pos)
    {
        return this.getWorld().getBlockState(pos);
    }

    @Nullable
    public NbtCompound getBlockEntityNbt(BlockPos pos)
    {
        return this.blockEntityNbts.get(pos);
    }

    @Nullable
    public abstract NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries);

}

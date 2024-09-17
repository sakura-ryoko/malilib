package fi.dy.masa.malilib.sync.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.sync.fbe.FakeBlockEntity;

public class FakeChunkCache implements AutoCloseable
{
    private final Object2ObjectLinkedOpenHashMap<BlockPos, FakeBlockEntity> blockEntities = new Object2ObjectLinkedOpenHashMap<>();
    private World world;
    private ChunkPos pos;

    public FakeChunkCache(World world, ChunkPos pos)
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

    public FakeBlockEntity createBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        if (this.blockEntities.containsKey(pos))
        {
            if (this.blockEntities.get(pos).isLoaded())
            {
                return this.blockEntities.get(pos);
            }

            this.blockEntities.get(pos).clear();
            this.blockEntities.get(pos).setType(type);
            this.blockEntities.get(pos).setPos(pos);
            this.blockEntities.get(pos).setState(state);
            this.blockEntities.get(pos).setWorld(this.getWorld());
        }
        else
        {
            this.blockEntities.put(pos, new FakeBlockEntity(type, pos, state));
        }

        return this.blockEntities.get(pos);
    }

    public boolean hasBlockEntity(BlockPos pos)
    {
        return this.blockEntities.containsKey(pos);
    }

    @Nullable
    public FakeBlockEntity getBlockEntity(BlockPos pos)
    {
        if (this.blockEntities.containsKey(pos))
        {
            return this.blockEntities.get(pos);
        }

        return null;
    }

    public void clear()
    {
        this.blockEntities.clear();
        this.world = null;
        this.pos = ChunkPos.ORIGIN;
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
    }
}

package fi.dy.masa.malilib.sync.cache;

import javax.annotation.Nonnull;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;

import fi.dy.masa.malilib.sync.fbe.FakeBlockEntity;
import fi.dy.masa.malilib.sync.fe.FakeEntity;

public class SyncCache implements AutoCloseable
{
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Object2ObjectLinkedOpenHashMap<ChunkPos, FakeChunkCache> chunks = new Object2ObjectLinkedOpenHashMap<>();
    private final FakeEntityList entityList = new FakeEntityList();
    private final FakeEntityManager<FakeEntity> entityManager = new FakeEntityManager<>(FakeEntity.class, new FakeEntityHandler());
    @Nullable
    private World world;

    private SyncCache()
    {
        if (mc.world != null)
        {
            this.world = mc.world;
        }
        else
        {
            this.world = null;
        }
    }

    public SyncCache(@Nonnull World world)
    {
        this();
        this.setWorld(world);
    }

    public @Nullable World getWorld()
    {
        return this.world;
    }

    public void setWorld(@Nullable World world)
    {
        this.world = world;
    }

    public boolean hasChunk(ChunkPos pos)
    {
        if (this.world == null)
        {
            return false;
        }

        return this.chunks.containsKey(pos);
    }

    @Nullable
    public FakeChunkCache getChunk(ChunkPos pos)
    {
        if (this.world == null)
        {
            return null;
        }

        if (this.chunks.containsKey(pos))
        {
            return this.chunks.get(pos);
        }

        this.chunks.put(pos, new FakeChunkCache(this.getWorld(), pos));
        return this.chunks.get(pos);
    }

    @Nullable
    public FakeBlockEntity getBlockEntity(ChunkPos chunkPos, BlockPos pos)
    {
        if (this.world == null)
        {
            return null;
        }

        if (this.chunks.containsKey(chunkPos))
        {
            return this.chunks.get(chunkPos).getBlockEntity(pos);
        }

        this.chunks.put(chunkPos, new FakeChunkCache(this.getWorld(), chunkPos));
        return this.chunks.get(chunkPos).getBlockEntity(pos);
    }

    @Nullable
    public FakeBlockEntity createBlockEntity(ChunkPos chunkPos, BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        FakeChunkCache cache = this.getChunk(chunkPos);

        if (cache == null)
        {
            return null;
        }

        return cache.createBlockEntity(type, pos, state);
    }

    protected EntityLookup<FakeEntity> getEntityLookup()
    {
        return this.entityManager.getLookup();
    }

    public Iterable<FakeEntity> getEntities()
    {
        return this.getEntityLookup().iterate();
    }

    public boolean hasEntity(FakeEntity entity)
    {
        return this.entityList.has(entity);
    }

    public int getRegularEntityCount()
    {
        return this.entityManager.getEntityCount();
    }

    public void addEntity(FakeEntity entity)
    {
        this.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        this.entityManager.addEntity(entity);
    }

    public void removeEntity(int entityId, Entity.RemovalReason removalReason)
    {
        FakeEntity entity = this.getEntityLookup().get(entityId);

        if (entity != null)
        {
            entity.setRemoved(removalReason);
            entity.onRemoved();
        }
    }

    @Nullable
    public FakeEntity getEntityById(int id)
    {
        return this.getEntityLookup().get(id);
    }

    public String asString()
    {
        return "§6[FEC]§r C: " + this.chunks.size() + ", E: " + this.entityManager.getDebugString();
    }

    public void clear()
    {
        this.getEntities().forEach(FakeEntity::clear);
        this.entityManager.clear();
        this.entityList.clear();
        this.chunks.forEach((chunkPos, cache) -> cache.clear());
        this.chunks.clear();
        this.setWorld(null);
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
    }

    private final class FakeEntityHandler implements EntityHandler<FakeEntity>
    {
        @Override
        public void create(FakeEntity entity)
        {
            SyncCache.this.entityList.add(entity);
        }

        @Override
        public void destroy(FakeEntity entity)
        {
            SyncCache.this.entityList.remove(entity);
        }

        @Override
        public void startTicking(FakeEntity entity) {}

        @Override
        public void stopTicking(FakeEntity entity) {}

        @Override
        public void startTracking(FakeEntity entity) {}

        @Override
        public void stopTracking(FakeEntity entity) {}

        @Override
        public void updateLoadStatus(FakeEntity entity) {}
    }
}

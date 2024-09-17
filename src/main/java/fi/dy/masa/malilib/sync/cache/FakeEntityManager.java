package fi.dy.masa.malilib.sync.cache;

import net.minecraft.world.entity.*;

public class FakeEntityManager<T extends EntityLike> implements AutoCloseable
{
    private final EntityHandler<T> handler;
    private final EntityIndex<T> index = new EntityIndex<>();
    private final SectionedEntityCache<T> cache;
    private final EntityLookup<T> lookup;

    public FakeEntityManager(Class<T> entityClass, EntityHandler<T> handler)
    {
        this.cache = new SectionedEntityCache<>(entityClass, (pos) -> EntityTrackingStatus.TRACKED);
        this.handler = handler;
        this.lookup = new SimpleEntityLookup<>(this.index, this.cache);
    }

    public EntityLookup<T> getLookup()
    {
        return this.lookup;
    }

    public void addEntity(T entity)
    {
        this.index.add(entity);
        this.handler.create(entity);
    }

    public void removeEntity(T entity)
    {
        this.handler.destroy(entity);
        this.index.remove(entity);
    }

    public int getEntityCount()
    {
        return this.index.size();
    }

    public String getDebugString()
    {
        return this.index.size() + "," + this.cache.sectionCount();
    }

    public void clear()
    {
        this.cache.getChunkPositions().clear();
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
    }
}

package fi.dy.masa.malilib.sync.cache;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import fi.dy.masa.malilib.sync.fe.FakeEntity;

public class FakeEntityList implements AutoCloseable
{
    private Int2ObjectMap<FakeEntity> entities = new Int2ObjectLinkedOpenHashMap<>();

    public FakeEntityList() {}

    public void add(FakeEntity entity)
    {
        this.entities.put(entity.getId(), entity);
    }

    public void remove(FakeEntity entity)
    {
        this.entities.remove(entity.getId());
    }

    public boolean has(FakeEntity entity)
    {
        return this.entities.containsKey(entity.getId());
    }

    @Nullable
    public FakeEntity get(int id)
    {
        if (this.entities.containsKey(id))
        {
            return this.entities.get(id);
        }

        return null;
    }

    public void forEach(Consumer<FakeEntity> action)
    {
        for (FakeEntity entity : this.entities.values())
        {
            action.accept(entity);
        }
    }

    public void clear()
    {
        if (this.entities != null)
        {
            this.entities.forEach((i, e) -> e.clear());
            this.entities.clear();
            this.entities = null;
        }
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
    }
}

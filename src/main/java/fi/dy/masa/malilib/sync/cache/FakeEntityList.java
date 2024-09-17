package fi.dy.masa.malilib.sync.cache;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import fi.dy.masa.malilib.sync.fe.FakeEntity;

public class FakeEntityList implements AutoCloseable
{
    private Int2ObjectMap<FakeEntity> entities = new Int2ObjectLinkedOpenHashMap<>();
    private Int2ObjectMap<FakeEntity> safe = new Int2ObjectLinkedOpenHashMap<>();
    private Int2ObjectMap<FakeEntity> iterate = new Int2ObjectLinkedOpenHashMap<>();

    public FakeEntityList() {}

    private void checkSafe()
    {
        if (this.iterate == this.entities)
        {
            this.safe.clear();

            for (Int2ObjectMap.Entry<FakeEntity> fakeEntityEntry : Int2ObjectMaps.fastIterable(this.entities))
            {
                this.safe.put(fakeEntityEntry.getIntKey(), fakeEntityEntry.getValue());
            }

            Int2ObjectMap<FakeEntity> int2ObjectMap = this.entities;
            this.entities = this.safe;
            this.safe = int2ObjectMap;
        }
    }

    public void add(FakeEntity entity)
    {
        this.checkSafe();
        this.entities.put(entity.getId(), entity);
    }

    public void remove(FakeEntity entity)
    {
        this.checkSafe();
        this.entities.remove(entity.getId());
    }

    public boolean has(FakeEntity entity)
    {
        return this.entities.containsKey(entity.getId());
    }

    @Nullable
    public FakeEntity get(int id)
    {
        this.checkSafe();
        if (this.entities.containsKey(id))
        {
            return this.entities.get(id);
        }

        return null;
    }

    public void forEach(Consumer<FakeEntity> action)
    {
        if (this.iterate == null)
        {
            this.iterate = this.entities;

            try
            {
                for (FakeEntity entity : this.entities.values())
                {
                    action.accept(entity);
                }
            }
            finally
            {
                this.iterate = null;
            }
        }
    }

    public void clear()
    {
        if (this.iterate != null)
        {
            this.iterate.forEach((i, e) -> e.clear());
            this.iterate.clear();
            this.iterate = null;
        }
        if (this.safe != null)
        {
            this.safe.forEach((i, e) -> e.clear());
            this.safe.clear();
            this.safe = null;
        }
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

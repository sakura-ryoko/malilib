package fi.dy.masa.malilib.util.data.palette;

import javax.annotation.Nullable;

public abstract class BasePalette<T> implements Palette<T>
{
    protected int currentSize;
    protected T[] values;

    @SuppressWarnings("unchecked")
    public BasePalette(int startingSize)
    {
        this.values = (T[]) new Object[startingSize];
    }

    @Override
    public int getSize()
    {
        return this.currentSize;
    }

    @Override
    public int getMaxSize()
    {
        return this.values.length;
    }

    @Override
    @Nullable
    public T getValue(int id)
    {
        return id >= 0 && id < this.currentSize ? this.values[id] : null;
    }
}

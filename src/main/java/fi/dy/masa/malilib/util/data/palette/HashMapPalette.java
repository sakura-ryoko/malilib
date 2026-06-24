package fi.dy.masa.malilib.util.data.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class HashMapPalette<T> extends BasePalette<T> implements Palette<T>
{
    protected final Object2IntOpenHashMap<T> valueToIdMap;
    protected final PaletteResizeHandler<T> paletteResizer;
    protected final int entryWidthBits;

    public HashMapPalette(int entryWidthBits, PaletteResizeHandler<T> paletteResizer)
    {
        super(1 << entryWidthBits);

        this.entryWidthBits = entryWidthBits;
        this.paletteResizer = paletteResizer;
        this.valueToIdMap = new Object2IntOpenHashMap<>();
        this.valueToIdMap.defaultReturnValue(-1);
    }

    @Override
    public int idFor(T value)
    {
        int id = this.valueToIdMap.getInt(value);

        if (id == -1)
        {
            id = this.addNewValue(value);
        }

        return id;
    }

    protected int addNewValue(T value)
    {
        if (this.currentSize < this.values.length)
        {
            int id = this.currentSize;
            this.valueToIdMap.put(value, id);
            this.values[id] = value;
            ++this.currentSize;
            return id;
        }
        else
        {
            return this.paletteResizer.onResize(this.entryWidthBits + 1, value, this);
        }
    }

    @Override
    public List<T> getMapping()
    {
        final int size = this.currentSize;
        List<T> list = new ArrayList<>(size);

        for (int id = 0; id < size; ++id)
        {
            list.add(this.values[id]);
        }

        return list;
    }

    @Override
    public boolean setMapping(List<T> list)
    {
        final int size = list.size();

        if (size <= this.values.length)
        {
            this.valueToIdMap.clear();
            Arrays.fill(this.values, null);

            for (int id = 0; id < size; ++id)
            {
                T val = list.get(id);
                this.valueToIdMap.put(val, id);
                this.values[id] = val;
            }

            this.currentSize = size;

            return true;
        }

        return false;
    }

    @Override
    public boolean overrideMapping(int id, T value)
    {
        if (id >= 0 && id < this.currentSize)
        {
            this.values[id] = value;
            this.valueToIdMap.put(value, id);
            return true;
        }

        return false;
    }

    @Override
    public HashMapPalette<T> copy(PaletteResizeHandler<T> paletteResizer)
    {
        HashMapPalette<T> copy = new HashMapPalette<>(this.entryWidthBits, paletteResizer);
        copy.setMapping(this.getMapping());
        return copy;
    }
}

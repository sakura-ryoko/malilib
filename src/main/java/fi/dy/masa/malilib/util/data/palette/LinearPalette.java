package fi.dy.masa.malilib.util.data.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinearPalette<T> extends BasePalette<T> implements Palette<T>
{
    protected final PaletteResizeHandler<T> paletteResizer;
    protected final int entryWidthBits;

    public LinearPalette(int entryWidthBits, PaletteResizeHandler<T> paletteResizer)
    {
        super(1 << entryWidthBits);

        this.entryWidthBits = entryWidthBits;
        this.paletteResizer = paletteResizer;
    }

    @Override
    public int idFor(T value)
    {
        final int currentSize = this.currentSize;
        T[] values = this.values;

        for (int i = 0; i < currentSize; ++i)
        {
            if (values[i] == value)
            {
                return i;
            }
        }

        if (currentSize < this.values.length)
        {
            this.values[currentSize] = value;
            ++this.currentSize;
            return currentSize;
        }
        else
        {
            return this.paletteResizer.onResize(this.entryWidthBits + 1, value, this);
        }
    }

    @Override
    public List<T> getMapping()
    {
        List<T> list = new ArrayList<>(this.currentSize);
        list.addAll(Arrays.asList(this.values).subList(0, this.currentSize));
        return list;
    }

    @Override
    public boolean setMapping(List<T> list)
    {
        final int size = list.size();

        if (size <= this.values.length)
        {
            Arrays.fill(this.values, null);

            for (int id = 0; id < size; ++id)
            {
                this.values[id] = list.get(id);
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
            return true;
        }

        return false;
    }

    @Override
    public LinearPalette<T> copy(PaletteResizeHandler<T> paletteResizer)
    {
        LinearPalette<T> copy = new LinearPalette<>(this.entryWidthBits, paletteResizer);
        copy.setMapping(this.getMapping());
        return copy;
    }
}

package fi.dy.masa.malilib.util.data.palette;

public interface PaletteResizeHandler<T>
{
    /**
     * Called when a palette runs out of IDs in the current entry width,
     * and the underlying container needs to be resized for the new entry bit width.
     * @return the ID for the new value being added when the resize happens
     */
    int onResize(int newSizeBits, T valueBeingAdded, Palette<T> oldPalette);
}

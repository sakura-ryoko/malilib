package fi.dy.masa.malilib.util.data.palette;

import java.util.List;
import javax.annotation.Nullable;

public interface Palette<T>
{
    /**
     * @return the current number of entries in this palette
     */
    int getSize();

    /**
     * @return the maximum size of the palette
     */
    int getMaxSize();

    /**
     * @return the palette ID for the given value (and add
     *         the value to the palette if it doesn't exist there yet)
     */
    int idFor(T value);

    /**
     * @return the value corresponding to the given palette ID, if the ID exists in the palette, or otherwise null
     */
    @Nullable
    T getValue(int id);

    /**
     * @return the current full mappings of IDs to values.
     *         The ID is the position in the returned list.
     */
    List<T> getMapping();

    /**
     * Sets the current ID to value mapping of the palette.
     * This is meant for reading the palette from file.
     * @return true if the mapping was set successfully, false if it failed
     */
    boolean setMapping(List<T> list);

    /**
     * Overrides the mapping for the given ID.
     * @return true if the ID was found in the palette and thus was possible to override
     */
    boolean overrideMapping(int id, T value);

    /**
     * Creates a copy of this palette, using the provided resize handler
     */
    Palette<T> copy(PaletteResizeHandler<T> resizeHandler);
}

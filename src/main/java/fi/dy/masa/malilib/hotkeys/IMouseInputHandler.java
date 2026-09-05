package fi.dy.masa.malilib.hotkeys;

import java.io.FileFilter;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.client.input.MouseButtonEvent;

public interface IMouseInputHandler
{
    /**
     * Called on mouse button events with the key and whether the key was pressed or released.
     * @param click ()
     * @param eventButtonState ()
     * @return true if further processing of this mouse button event should be cancelled
     */
    default boolean onMouseClick(final MouseButtonEvent click, final boolean eventButtonState)
    {
        return false;
    }

    /**
     * Called when the mouse wheel is scrolled
     * @param mouseX ()
     * @param mouseY ()
     * @param amount ()
     * @return ()
     */
    default boolean onMouseScroll(final double mouseX, final double mouseY, final double amount)
    {
        return false;
    }

    /**
     * Called when the mouse is moved
     * @param mouseX ()
     * @param mouseY ()
     */
    default void onMouseMove(final double mouseX, final double mouseY) {}

    /**
     * Called when someone drags files into the Window.<br>
     * This Bypasses the current screen check; so that it can work InGame also.<br>
     * Returned list can be filtered by <b>dropFileFilter()</b>
     * @param files ()
     */
    default boolean onMouseFilesDrop(final double mouseX, final double mouseY, final List<Path> files) { return false; }

    /**
     * Filter any dropped-in files
     * @return -
     */
    default FileFilter dropFileFilter() { return null; }
}

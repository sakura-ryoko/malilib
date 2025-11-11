package fi.dy.masa.malilib.hotkeys;

import net.minecraft.client.gui.Click;

public interface IMouseInputHandler
{
    /**
     * Called on mouse button events with the key and whether the key was pressed or released.
     * @param click ()
     * @param eventButtonState ()
     * @return true if further processing of this mouse button event should be cancelled
     */
    default boolean onMouseClick(Click click, boolean eventButtonState)
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
    default boolean onMouseScroll(double mouseX, double mouseY, double amount)
    {
        return false;
    }

    /**
     * Called when the mouse is moved
     * @param mouseX ()
     * @param mouseY ()
     */
    default void onMouseMove(double mouseX, double mouseY) {}
}

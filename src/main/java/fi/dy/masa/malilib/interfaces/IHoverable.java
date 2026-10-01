package fi.dy.masa.malilib.interfaces;

import java.util.List;

public interface IHoverable {

    default boolean hasHoverText(int mouseX, int mouseY)
    {
        return this.getHoverStrings(mouseX, mouseY).isEmpty() == false;
    }

    List<String> getHoverStrings(int mouseX, int mouseY);
}

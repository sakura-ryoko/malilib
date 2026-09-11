package fi.dy.masa.malilib.interfaces;

import java.util.List;

public interface IHoverable {

    default boolean hasHoverText()
    {
        return this.getHoverStrings().isEmpty() == false;
    }

    List<String> getHoverStrings();
}

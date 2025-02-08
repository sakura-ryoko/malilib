package fi.dy.masa.malilib.gui.interfaces;

import java.nio.file.Path;
import javax.annotation.Nullable;

public interface IDirectoryCache
{
    @Nullable
    Path getCurrentDirectoryForContext(String context);

    void setCurrentDirectoryForContext(String context, Path dir);
}

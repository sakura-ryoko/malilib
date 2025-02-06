package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import javax.annotation.Nullable;

public interface IDirectoryCache
{
    // TODO -- Remove the file system; needs to deal with the FileFilter mechanism to make it compat with Path
    @Nullable
    File getCurrentDirectoryForContext(String context);

    void setCurrentDirectoryForContext(String context, File dir);
}

package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;

public interface IDirectoryNavigator
{
    // TODO -- Remove the file system; needs to deal with the FileFilter mechanism to make it compat with Path
    File getCurrentDirectory();

    void switchToDirectory(File dir);

    void switchToParentDirectory();

    void switchToRootDirectory();
}

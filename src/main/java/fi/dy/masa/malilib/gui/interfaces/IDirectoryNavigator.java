package fi.dy.masa.malilib.gui.interfaces;

import java.nio.file.Path;

public interface IDirectoryNavigator
{
    Path getCurrentDirectory();

    void switchToDirectory(Path dir);

    void switchToParentDirectory();

    void switchToRootDirectory();
}

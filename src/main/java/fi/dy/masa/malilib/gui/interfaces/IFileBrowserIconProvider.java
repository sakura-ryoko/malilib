package fi.dy.masa.malilib.gui.interfaces;

import java.nio.file.Path;
import javax.annotation.Nullable;

public interface IFileBrowserIconProvider
{
    IGuiIcon getIconRoot();

    IGuiIcon getIconUp();

    IGuiIcon getIconCreateDirectory();

	IGuiIcon getIconSearch();

    IGuiIcon getIconDirectory();

    @Nullable
    IGuiIcon getIconForFile(Path file);
}

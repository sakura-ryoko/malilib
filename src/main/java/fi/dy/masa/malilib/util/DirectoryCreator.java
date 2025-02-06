package fi.dy.masa.malilib.util;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public class DirectoryCreator implements IStringConsumerFeedback
{
    // TODO -- Remove the file system; needs to deal with the FileFilter mechanism to make it compat with Path
    protected final File dir;
    @Nullable protected final IDirectoryNavigator navigator;

    public DirectoryCreator(File dir, @Nullable IDirectoryNavigator navigator)
    {
        this.dir = dir;
        this.navigator = navigator;
    }

    @Override
    public boolean setString(String string)
    {
        if (string.isEmpty())
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.invalid_directory", string);
            return false;
        }

        File file = new File(this.dir, string);

        if (file.exists())
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.file_or_directory_already_exists", file.getAbsolutePath());
            return false;
        }

        if (file.mkdirs() == false)
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.failed_to_create_directory", file.getAbsolutePath());
            return false;
        }

        if (this.navigator != null)
        {
            this.navigator.switchToDirectory(file);
        }

        InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.directory_created", string);

        return true;
    }
}

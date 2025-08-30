package fi.dy.masa.malilib.util;

import java.io.File;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

/**
 * Used to create a Directory via the GUI
 *
 * @param dir
 * @param navigator
// * @param feedback
 */
public record DirectoryCreator(File dir, @Nullable IDirectoryNavigator navigator) implements IStringConsumerFeedback
{
    @Override
    public boolean setString(String string)
    {
        if (string.isEmpty())
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_create_directory", string);
            MaLiLib.LOGGER.warn("DirectoryCreator: Failed to create directory; Directory is invalid/empty.");
            return false;
        }

        File newDir = new File(this.dir, string);

        if (newDir.exists())
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_already_exists", newDir.getAbsolutePath());
            MaLiLib.LOGGER.warn("DirectoryCreator: Failed to create directory '{}'; Destination already exists.", this.dir().getAbsolutePath());
            return false;
        }

        try
        {
            if (newDir.mkdirs() == false)
            {
                InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.failed_to_create_directory", newDir.getAbsolutePath());
                MaLiLib.LOGGER.error("DirectoryCreator: Error creating directory '{}'", this.dir().getAbsolutePath());
                return false;
            }
        }
        catch (Exception err)
        {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_create_directory", newDir.getAbsolutePath());
            MaLiLib.LOGGER.error("DirectoryCreator: Exception creating directory '{}'; {}", this.dir().getAbsolutePath(), err.getLocalizedMessage());        }

        if (this.navigator != null)
        {
            this.navigator.switchToDirectory(newDir);
        }

//		if (this.feedback())
//		{
            InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.directory_created", string);
//		}

        MaLiLib.debugLog("DirectoryCreator: Created directory '{}'", newDir.getAbsolutePath());

        return true;
    }
}

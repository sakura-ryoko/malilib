package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
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
public record DirectoryCreator(Path dir, @Nullable IDirectoryNavigator navigator) implements IStringConsumerFeedback
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

		Path newDir = this.dir().resolve(string);

		if (Files.exists(newDir))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_already_exists", newDir.toAbsolutePath());
			MaLiLib.LOGGER.warn("DirectoryCreator: Failed to create directory '{}'; Destination already exists.", this.dir().toAbsolutePath());
			return false;
		}

		try
		{
			Files.createDirectory(newDir);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_create_directory", newDir.toAbsolutePath());
			MaLiLib.LOGGER.error("DirectoryCreator: Exception creating directory '{}'; {}", this.dir().toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (this.navigator() != null)
		{
			this.navigator().switchToDirectory(newDir);
		}

//		if (this.feedback())
//		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.directory_created", string);
//		}

		MaLiLib.debugLog("DirectoryCreator: Created directory '{}'", newDir.toAbsolutePath());
		return true;
	}
}

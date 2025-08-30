package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;

/**
 * Used to Delete Files via the GUI
 *
 * @param file
 * @param navigator
 * @param feedback
 */
public record FileDeleter(Path file, @Nullable IDirectoryNavigator navigator, boolean feedback) implements IConfirmationListener
{
	@Override
	public boolean onActionConfirmed()
	{
		if (this.file() == null)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.invalid_file_or_directory");
			MaLiLib.debugLog("FileDeleter: Failed to delete file; File is invalid/empty.");
			return false;
		}

		if (!Files.exists(this.file()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist", this.file().toAbsolutePath());
			MaLiLib.debugLog("FileDeleter: Failed to delete file '{}'; File does not exist", this.file().toAbsolutePath());
			return false;
		}

		if (this.navigator() != null && this.navigator().getCurrentDirectory().equals(this.file))
		{
			this.navigator().switchToParentDirectory();
		}

		try
		{
			Files.deleteIfExists(this.file);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_delete_file", this.file().toAbsolutePath());
			MaLiLib.debugLog("FileDeleter: Failed to delete file '{}'; {}", this.file().toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (feedback())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_or_directory_deleted", this.file().getFileName());
		}

		MaLiLib.debugLog("FileDeleter: File '{}' deleted", this.file().toAbsolutePath());
		return true;
	}

	@Override
	public boolean onActionCancelled()
	{
		// They clicked on Cancel, so we're safe.
		return false;
	}
}

package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;

/**
 * Used to Rename / Move files via the GUI (EXPERIMENTAL)
 *
 * @param navigator
 * @param feedback
 */
@ApiStatus.Experimental
public record FileRenamerDualInput(Path dir, @Nullable IDirectoryNavigator navigator, boolean feedback)
		implements IStringDualConsumerFeedback
{
	@Override
	public boolean setStrings(String string1, String string2)
	{
		if (string1.isEmpty() || string2.isEmpty())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.invalid_file_or_directory");
			MaLiLib.debugLog("FileRenamer: Failed to rename file; File is invalid/empty.");
			return false;
		}

		MaLiLib.LOGGER.error("string1: [{}], string2: [{}]", string1, string2);

		final Path file = this.dir().resolve(FileNameUtils.generateSafeFileName(string1)).normalize();
		final Path newFile = this.dir().resolve(FileNameUtils.generateSafeFileName(string2)).normalize();

//	    MaLiLib.LOGGER.error("RENAME: [{}] --> [{}] (dir: '{}')", this.file.toAbsolutePath(), newFile.toAbsolutePath(), dir.toAbsolutePath());

		if (file.getFileName().equals(newFile.getFileName()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_rename.same_name", newFile.toAbsolutePath());
			MaLiLib.debugLog("FileRenamer: Failed to rename file '{}'; Destination is the same.", file.toAbsolutePath());
			return true;        // Closes Dialog box
		}

		if (!Files.exists(file))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist",
			                                    file.toAbsolutePath());
			MaLiLib.debugLog("FileRenamer: Failed to rename file '{}'; Source does not exist.", file.toAbsolutePath());
			return false;
		}

		if (Files.exists(newFile))
		{
			// Hold Shift to Overwrite destination file
			if (GuiBase.isShiftDown())
			{
				try
				{
					Files.delete(newFile);
				}
				catch (Exception err)
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.failed_to_delete_file", file.toAbsolutePath());
					MaLiLib.debugLog("FileRenamer: Failed to delete file '{}'; {}", file.toAbsolutePath(), err.getLocalizedMessage());
					return false;
				}
			}
			else
			{
				InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exists",
				                                    file.toAbsolutePath(), newFile.toAbsolutePath());
				MaLiLib.debugLog("FileRenamer: Failed to rename file '{}'; Destination file exists.", file.toAbsolutePath());
				return false;
			}
		}

		try
		{
			Files.move(file, newFile);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exception",
			                                    file.toAbsolutePath(), newFile.toAbsolutePath(), err.getLocalizedMessage());
			MaLiLib.debugLog("FileRenamer: Exception renaming file '{}'; {}", file.toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (feedback())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_or_directory_renamed", file.getFileName(), newFile.getFileName());
		}

		MaLiLib.debugLog("FileRenamer: Renamed file '{}' -> '{}'", file.toAbsolutePath(), newFile.toAbsolutePath());
		return true;
	}
}

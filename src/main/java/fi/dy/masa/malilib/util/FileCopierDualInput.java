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
 * Used to Copy Files via the GUI (EXPERIMENTAL)
 *
 * @param navigator
 * @param feedback
 */
@ApiStatus.Experimental
public record FileCopierDualInput(Path dir, @Nullable IDirectoryNavigator navigator, boolean feedback)
		implements IStringDualConsumerFeedback
{
	@Override
	public boolean setStrings(String string1, String string2)
	{
		if (string1.isEmpty() || string2.isEmpty())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.invalid_file_or_directory");
			MaLiLib.LOGGER.warn("FileCopier: Failed to copy file; File is invalid/empty.");
			return false;
		}

		final Path file = this.dir().resolve(FileNameUtils.generateSafeFileName(string1)).normalize();
		final Path newFile = this.dir().resolve(FileNameUtils.generateSafeFileName(string2)).normalize();

		if (file.getFileName().equals(newFile.getFileName()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.destination_exists", file.toAbsolutePath(), newFile.toAbsolutePath());
			MaLiLib.LOGGER.warn("FileCopier: Failed to copy file '{}'; Destination is the same.", file.toAbsolutePath());
			return true;        // Closes Dialog box
		}

		if (!Files.exists(file))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist",
			                                    file.toAbsolutePath());
			MaLiLib.LOGGER.warn("FileCopier: Failed to copy file '{}'; Source does not exist.", file.toAbsolutePath());
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
					MaLiLib.debugLog("FileCopier: Failed to delete file '{}'; {}", file.toAbsolutePath(), err.getLocalizedMessage());
					return false;
				}
			}
			else
			{
				InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.destination_exists",
				                                    file.toAbsolutePath(), newFile.toAbsolutePath());
				MaLiLib.debugLog("FileCopier: Failed to copy file '{}'; Destination file exists.", file.toAbsolutePath());
				return false;
			}
		}

		try
		{
			Files.copy(file, newFile);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_copy_file.exception",
			                                    file.toAbsolutePath(), newFile.toAbsolutePath(), err.getLocalizedMessage());
			MaLiLib.debugLog("FileCopier: Exception copying file '{}'; {}", file.toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		if (feedback())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_copied", file.getFileName(), newFile.getFileName());
		}

		MaLiLib.debugLog("FileCopier: Copied file '{}' -> '{}'", file.toAbsolutePath(), newFile.toAbsolutePath());
		return true;
	}
}

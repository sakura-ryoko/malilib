package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public record FileRenamer(Path file, @Nullable IDirectoryNavigator navigator) implements IStringConsumerFeedback
{
	@Override
	public boolean setString(String string)
	{
		if (string.isEmpty() || this.file == null)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.invalid_file_or_directory");
			return false;
		}

		Path dir = this.file.getParent();
		Path newFile = dir.resolve(FileNameUtils.generateSafeFileName(string)).normalize();

//	    MaLiLib.LOGGER.error("RENAME: [{}] --> [{}] (dir: '{}')", this.file.toAbsolutePath(), newFile.toAbsolutePath(), dir.toAbsolutePath());

		if (this.file.getFileName().equals(newFile.getFileName()))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_rename.same_name", newFile.toAbsolutePath());
			return true;        // Closes Dialog box
		}

		if (!Files.exists(this.file))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_does_not_exist",
			                                    this.file.toAbsolutePath());
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
					InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.failed_to_delete_file", this.file.toAbsolutePath());
					MaLiLib.debugLog("FileRenamer: Failed to delete file '{}'; {}", this.file.toAbsolutePath(), err.getLocalizedMessage());
					return false;
				}
			}
			else
			{
				InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exists",
				                                    this.file.toAbsolutePath(), newFile.toAbsolutePath());
				return false;
			}
		}

		try
		{
			Files.move(this.file, newFile);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_rename_file.exception",
			                                    this.file.toAbsolutePath(), newFile.toAbsolutePath(), err.getLocalizedMessage());
			return false;
		}

		InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.file_or_directory_renamed", newFile.getFileName());
		return true;
	}
}

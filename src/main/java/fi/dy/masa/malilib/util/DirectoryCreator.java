package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public record DirectoryCreator(Path dir, @Nullable IDirectoryNavigator navigator) implements IStringConsumerFeedback
{
	@Override
	public boolean setString(String string)
	{
		if (string.isEmpty())
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_create_directory", string);
			return false;
		}

		Path file = this.dir.resolve(string);

		if (Files.exists(file))
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.file_or_directory_already_exists", file.toAbsolutePath());
			return false;
		}

		try
		{
			Files.createDirectory(file);
		}
		catch (Exception err)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.failed_to_create_directory", file.toAbsolutePath());
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

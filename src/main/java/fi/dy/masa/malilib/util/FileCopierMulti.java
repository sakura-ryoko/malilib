package fi.dy.masa.malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.interfaces.IPathListConsumerFeedback;

/**
 * Used to Copy Multiple Files via the GUI (Using the "File Drag" mechanism)
 *
 * @param destDir
 * @param navigator
 * @param feedback
 */
@ApiStatus.Experimental
public record FileCopierMulti(Path destDir, @Nullable IDirectoryNavigator navigator, boolean feedback)
		implements IPathListConsumerFeedback
{
	@Override
	public boolean onSetPathsCompleted(List<Path> sources)
	{
		if (sources.isEmpty() || this.destDir() == null)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.copier_multi.no_sources");
			MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy files; Sources are invalid/empty.");
			return false;
		}

		int copied = 0;
		int failed = 0;

		for (Path source : sources)
		{
			Path eachFile = source.getFileName();
			Path newFile = this.destDir().resolve(eachFile).normalize();

			if (source.toAbsolutePath().equals(newFile.toAbsolutePath()))
			{
				MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy file '{}'; Destination is the same.", source.toAbsolutePath());
				failed++;
				break;
			}

			if (!Files.exists(source))
			{
				MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy file '{}'; Source does not exist.", source.toAbsolutePath());
				failed++;
				break;
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
						MaLiLib.debugLog("FileCopierMulti: Failed to delete file '{}'; {}", source.toAbsolutePath(), err.getLocalizedMessage());
						failed++;
						break;
					}
				}
				else
				{
					MaLiLib.debugLog("FileCopierMulti: Failed to copy file '{}'; Destination file exists.", source.toAbsolutePath());
					failed++;
					break;
				}
			}

			try
			{
				Files.copy(source, newFile);
			}
			catch (Exception err)
			{
				MaLiLib.debugLog("FileCopierMulti: Exception copying file '{}'; {}", source.toAbsolutePath(), err.getLocalizedMessage());
				failed++;
				break;
			}

			copied++;
		}

		if (feedback())
		{
			if (copied > 0)
			{
				if (failed > 0)
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.WARNING, "malilib.message.copier_multi.files_copied_with_errors", copied, failed, this.destDir().toAbsolutePath().toString());
				}
				else
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.copier_multi.files_copied_no_errors", copied, this.destDir().toAbsolutePath().toString());
				}
			}
			else
			{
				if (failed > 0)
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.copier_multi.no_files_copied_with_errors", failed, this.destDir().toAbsolutePath().toString());
				}
				else
				{
					InfoUtils.showGuiOrActionBarMessage(MessageType.INFO, "malilib.message.copier_multi.no_files_copied_no_errors", this.destDir().toAbsolutePath().toString());
				}
			}
		}

		MaLiLib.debugLog("FileCopierMulti: Copied {} files (with {} failed) to directory '{}'", copied, failed, this.destDir().toAbsolutePath().toString());
		return true;
	}
}

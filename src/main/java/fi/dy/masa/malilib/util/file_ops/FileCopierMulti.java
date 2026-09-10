package fi.dy.masa.malilib.util.file_ops;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.interfaces.IPathListConsumerFeedback;
import fi.dy.masa.malilib.util.InfoUtils;

/**
 * Used to Copy Multiple Files via the GUI (Using the "File Drag" mechanism)
 *
 * @param destDir
 * @param navigator
 * @param feedback
 */
public record FileCopierMulti(Path destDir, @Nullable IDirectoryNavigator navigator, boolean feedback)
		implements IPathListConsumerFeedback
{
	private static int copied;
	private static int failed;

	@Override
	public boolean onSetPathsCompleted(List<Path> sources, WidgetFileBrowserBase.FileFilter filter)
	{
		if (sources.isEmpty() || this.destDir() == null)
		{
			InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.message.error.copier_multi.no_sources");
			MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy files; Sources are invalid/empty.");
			return false;
		}

		copied = 0;
		failed = 0;

		for (Path source : sources)
		{
			if (Files.isDirectory(source))
			{
//				this.walkSubDir(this.destDir(), source, filter);
				MaLiLib.LOGGER.warn("FileCopierMulti: Skipping file '{}'; Source is a directory.", source.toAbsolutePath().toString());
				failed++;
				continue;
			}

			this.walkEachFile(source, filter);
		}

		this.displayFeedback();
		MaLiLib.debugLog("FileCopierMulti: Copied {} files (with {} failed) to directory '{}'", copied, failed, this.destDir().toAbsolutePath().toString());

		return true;
	}

	private void walkEachFile(Path source, WidgetFileBrowserBase.FileFilter filter)
	{
		try
		{
			if (Files.isRegularFile(source) && filter.accept(source))
			{
				Path eachFile = source.getFileName();
				Path newFile = this.destDir().resolve(eachFile).normalize();

				if (source.toAbsolutePath().equals(newFile.toAbsolutePath()))
				{
					MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy file '{}'; Destination is the same.", source.toAbsolutePath());
					failed++;
					return;
				}

				if (!Files.exists(source))
				{
					MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy file '{}'; Source does not exist.", source.toAbsolutePath());
					failed++;
					return;
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
							MaLiLib.LOGGER.warn("FileCopierMulti: Failed to delete file '{}'; {}", source.toAbsolutePath(), err.getLocalizedMessage());
							failed++;
							return;
						}
					}
					else
					{
						MaLiLib.LOGGER.warn("FileCopierMulti: Failed to copy file '{}'; Destination file exists.", source.toAbsolutePath());
						failed++;
						return;
					}
				}

				try
				{
					Files.copy(source, newFile);
					copied++;
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.warn("FileCopierMulti: Exception copying file '{}'; {}", source.toAbsolutePath(), err.getLocalizedMessage());
					failed++;
				}
			}
			else
			{
				MaLiLib.debugLog("FileCopierMulti: Skipping copying file '{}'; Filtered (Directory?)", source.toAbsolutePath());
				failed++;
			}
		}
		catch (IOException err)
		{
			MaLiLib.LOGGER.warn("FileCopierMulti: Exception filtering file '{}'; {}", source.toAbsolutePath(), err.getLocalizedMessage());
			failed++;
		}
	}

	// fixme
	private void walkSubDir(Path relativeDir, Path subDir, WidgetFileBrowserBase.FileFilter filter)
	{
		if (Files.isDirectory(subDir))
		{
			Path parent = subDir.getParent();

			if (parent != null)
			{
				Path newRelative = relativeDir.resolve(parent);

				if (!Files.isDirectory(newRelative))
				{
					try
					{
						Files.createDirectory(newRelative);
					}
					catch (IOException err)
					{
						MaLiLib.LOGGER.warn("FileCopierMulti: Failed to create a directory '{}'; {}", newRelative.toAbsolutePath(), err.getLocalizedMessage());
						failed++;
						return;
					}
				}

				try (DirectoryStream<Path> ds = Files.newDirectoryStream(subDir))
				{
					ds.forEach(p ->
					           {
								   if (Files.isDirectory(p))
								   {
									   this.walkSubDir(newRelative, p, filter);
								   }
								   else if (Files.isRegularFile(p))
								   {
									   this.walkEachFile(p, filter);
								   }
					           });
				}
				catch (Exception err)
				{
					MaLiLib.LOGGER.warn("FileCopierMulti: Failed to traverse sub directory '{}'; {}", subDir.toAbsolutePath(), err.getLocalizedMessage());
					failed++;
				}
			}
		}
	}

	private void displayFeedback()
	{
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
	}
}

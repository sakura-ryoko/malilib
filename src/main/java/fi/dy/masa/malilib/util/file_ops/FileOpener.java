package fi.dy.masa.malilib.util.file_ops;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;

/**
 * A Chroot Jail compliant File/Path opener
 * @param jail  The Jail directory
 */
public record FileOpener(@Nonnull Path jail)
{
	public void openPath(@Nonnull Path dir)
	{
		if (!Files.exists(this.jail()) || !Files.isDirectory(this.jail()))
		{
			MaLiLib.LOGGER.warn("openPath: Not a valid jail directory at '{}'", this.jail().toAbsolutePath().toString());
			return;
		}
		if (!Files.exists(dir) || !Files.isDirectory(dir))
		{
			MaLiLib.LOGGER.warn("openPath: Not a valid directory at '{}'", dir.toAbsolutePath().toString());
			return;
		}
		if (!this.isSafe(dir))
		{
			MaLiLib.LOGGER.warn("openPath: Not a safe directory to open (Jailed): '{}'", dir.toAbsolutePath().toString());
			return;
		}

		Path safeDir;

		try
		{
			safeDir = dir.toRealPath();
		}
		catch (IOException e)
		{
			MaLiLib.LOGGER.warn("openPath: Failed to resolve canonical path for '{}'", dir.toAbsolutePath().toString());
			return;
		}

		ProcessBuilder pb;

		switch (MaLiLibReference.OS)
		{
			case WINDOWS -> pb = new ProcessBuilder("explorer.exe", safeDir.toString());
			case MAC -> pb = new ProcessBuilder("open", safeDir.toString());
			default -> pb = new ProcessBuilder("xdg-open", safeDir.toString());
		}

		pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
		pb.redirectError(ProcessBuilder.Redirect.DISCARD);
		pb.redirectInput(ProcessBuilder.Redirect.DISCARD);

		try
		{
			pb.start();
		}
		catch (IOException e)
		{
			MaLiLib.LOGGER.warn("openPath: Exception when opening path '{}'; {}", safeDir.toAbsolutePath().toString(), e.getLocalizedMessage());
		}
	}

	private boolean isSafe(@Nonnull Path dir)
	{
		try
		{
			Path realTarget = dir.toRealPath();
			Path realJail = this.jail().toRealPath();

			return realTarget.startsWith(realJail);
		}
		catch (IOException e)
		{
			return false;
		}
	}
}
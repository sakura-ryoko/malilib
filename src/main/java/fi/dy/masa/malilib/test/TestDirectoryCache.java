package fi.dy.masa.malilib.test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;

public class TestDirectoryCache implements IDirectoryCache
{
	private static final TestDirectoryCache INSTANCE = new TestDirectoryCache();
	public static TestDirectoryCache getInstance() { return INSTANCE; }
	private final Map<String, Path> LAST_DIRECTORIES;

	private TestDirectoryCache()
	{
		this.LAST_DIRECTORIES = new HashMap<>();
	}

	@Override
	public @Nullable Path getCurrentDirectoryForContext(String context)
	{
		return this.LAST_DIRECTORIES.get(context);
	}

	@Override
	public void setCurrentDirectoryForContext(String context, Path dir)
	{
		this.LAST_DIRECTORIES.put(context, dir);
	}

	public void clear()
	{
		this.LAST_DIRECTORIES.clear();
	}
}

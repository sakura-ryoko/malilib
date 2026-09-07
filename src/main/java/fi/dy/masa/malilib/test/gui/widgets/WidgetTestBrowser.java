package fi.dy.masa.malilib.test.gui.widgets;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.spongepowered.asm.mixin.Overwrite;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.test.data.TestDirectoryCache;
import fi.dy.masa.malilib.test.gui.GuiTestFileBrowser;
import fi.dy.masa.malilib.test.gui.TestFileIcons;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.ImmutableCopy;

public class WidgetTestBrowser extends WidgetFileBrowserBase
{
	protected static final FileFilter FILE_FILTER_ANY = new FileFilterAny();
	protected final GuiTestFileBrowser parent;
	protected ImmutableList<String> hoverText;

	public WidgetTestBrowser(int x, int y, int width, int height,
	                         GuiTestFileBrowser parent, @Nullable ISelectionListener<DirectoryEntry> selectionListener)
	{
		super(x, y, width, height,
		      TestDirectoryCache.getInstance(),
		      parent.getBrowserContext(), parent.getDefaultDirectory(),
		      selectionListener, TestFileIcons.FILE_ICON_TEST);

		this.parent = parent;
		TestDirectoryCache.getInstance().clear();
		this.hoverText = ImmutableList.of();
	}

	@Override
	protected Path getRootDirectory()
	{
		return FileUtils.getMinecraftDirectory();
	}

	@Override
	protected FileFilter getFileFilter()
	{
		return FILE_FILTER_ANY;
	}

	@Override
	protected boolean displayHoverInfo()
	{
		return true;
	}

	@Override
	protected String getEntryHoverInfoOverride(DirectoryEntry entry)
	{
		return "This is a test of the File Browser System";
	}

	public static class FileFilterAny extends FileFilter
	{
		@Override
		public boolean accept(Path entry)
		{
			return true;
		}
	}
}

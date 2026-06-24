package fi.dy.masa.malilib.test.gui.widgets;

import java.nio.file.Path;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.test.TestDirectoryCache;
import fi.dy.masa.malilib.test.gui.GuiTestFileBrowser;
import fi.dy.masa.malilib.test.gui.TestFileIcons;

public class WidgetTestBrowser extends WidgetFileBrowserBase
{
	protected static final FileFilter FILE_FILTER_ANY = new FileFilterAny();
	protected final GuiTestFileBrowser parent;

	public WidgetTestBrowser(int x, int y, int width, int height,
	                         GuiTestFileBrowser parent, @Nullable ISelectionListener<DirectoryEntry> selectionListener)
	{
		super(x, y, width, height,
		      TestDirectoryCache.getInstance(),
		      parent.getBrowserContext(), parent.getDefaultDirectory(),
		      selectionListener, TestFileIcons.FILE_ICON_TEST);

		this.parent = parent;
		TestDirectoryCache.getInstance().clear();
	}

	@Override
	protected Path getRootDirectory()
	{
		return MaLiLibReference.GAME_DIR;
	}

	@Override
	protected FileFilter getFileFilter()
	{
		return FILE_FILTER_ANY;
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

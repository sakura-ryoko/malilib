package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class WidgetFileBrowserBase extends WidgetListBase<DirectoryEntry, WidgetDirectoryEntry> implements IDirectoryNavigator
{
    // TODO -- Remove the file system; needs to deal with the FileFilter mechanism to make it compat with Path
    protected static final FileFilter DIRECTORY_FILTER = new FileFilterDirectories();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final IDirectoryCache cache;
    protected File currentDirectory;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider;
    @Nullable protected WidgetDirectoryNavigation directoryNavigationWidget;

    public WidgetFileBrowserBase(int x, int y, int width, int height,
            IDirectoryCache cache, String browserContext, File defaultDirectory,
            @Nullable ISelectionListener<DirectoryEntry> selectionListener, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, selectionListener);

        this.cache = cache;
        this.browserContext = browserContext;
        this.currentDirectory = this.cache.getCurrentDirectoryForContext(this.browserContext);
        this.iconProvider = iconProvider;
        this.allowKeyboardNavigation = true;

        if (this.currentDirectory == null)
        {
            this.currentDirectory = defaultDirectory;
        }

        this.setSize(width, height);
        this.updateDirectoryNavigationWidget();
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if ((keyCode == KeyCodes.KEY_BACKSPACE || keyCode == KeyCodes.KEY_LEFT) && this.currentDirectoryIsRoot() == false)
        {
            this.switchToParentDirectory();
            return true;
        }
        else if ((keyCode == KeyCodes.KEY_RIGHT || keyCode == KeyCodes.KEY_ENTER) &&
                  this.getLastSelectedEntry() != null && this.getLastSelectedEntry().getType() == DirectoryEntryType.DIRECTORY)
        {
            this.switchToDirectory(new File(this.getLastSelectedEntry().getDirectory(), this.getLastSelectedEntry().getName()));
            return true;
        }

        return false;
    }

    @Override
    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        // Draw an outline around the entire file browser
        RenderUtils.drawOutlinedBox(this.posX, this.posY, this.browserWidth, this.browserHeight, 0xB0000000, COLOR_HORIZONTAL_BAR);

        super.drawContents(drawContext, mouseX, mouseY, partialTicks);

        this.drawAdditionalContents(mouseX, mouseY, drawContext);
    }

    protected void drawAdditionalContents(int mouseX, int mouseY, DrawContext drawContext)
    {
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.browserWidth = this.getBrowserWidthForTotalWidth(width);
        this.browserEntryWidth = this.browserWidth - 14;
    }

    protected int getBrowserWidthForTotalWidth(int width)
    {
        return width - 6;
    }

    protected void updateDirectoryNavigationWidget()
    {
        int x = this.posX + 2;
        int y = this.posY + 4;

        this.directoryNavigationWidget = new WidgetDirectoryNavigation(x, y, this.browserEntryWidth, 14,
                this.currentDirectory, this.getRootDirectory(), this, this.iconProvider);
        this.browserEntriesOffsetY = this.directoryNavigationWidget.getHeight() + 3;
        this.widgetSearchBar = this.directoryNavigationWidget;
    }

    @Override
    public void refreshEntries()
    {
        this.updateDirectoryNavigationWidget();
        this.refreshBrowserEntries();
    }

    @Override
    protected void refreshBrowserEntries()
    {
        this.listContents.clear();

        File dir = this.currentDirectory;

        if (dir.isDirectory() && dir.canRead())
        {
            if (this.hasFilter())
            {
                this.addFilteredContents(dir);
            }
            else
            {
                this.addNonFilteredContents(dir);
            }
        }

        this.reCreateListEntryWidgets();
    }

    protected void addNonFilteredContents(File dir)
    {
        List<DirectoryEntry> list = new ArrayList<>();

        // Show directories at the top
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, null, null);
        Collections.sort(list);
        this.listContents.addAll(list);
        list.clear();

        this.addMatchingEntriesToList(this.getFileFilter(), dir, list, null, null);
        Collections.sort(list);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir)
    {
        String filterText = this.widgetSearchBar.getFilter();
        List<DirectoryEntry> list = new ArrayList<>();
        this.addFilteredContents(dir, filterText, list, null);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir, String filterText, List<DirectoryEntry> listOut, @Nullable String prefix)
    {
        List<DirectoryEntry> list = new ArrayList<>();
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
        list.clear();

        for (File subDir : this.getSubDirectories(dir))
        {
            String pre;

            if (prefix != null)
            {
                pre = prefix + subDir.getName() + "/";
            }
            else
            {
                pre = subDir.getName() + "/";
            }

            this.addFilteredContents(subDir, filterText, list, pre);
            Collections.sort(list);
            listOut.addAll(list);
            list.clear();
        }

        this.addMatchingEntriesToList(this.getFileFilter(), dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
    }

    protected void addMatchingEntriesToList(FileFilter filter, File dir, List<DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix)
    {
        for (File file : dir.listFiles(filter))
        {
            String name = FileUtils.getNameWithoutExtension(file.getName().toLowerCase());

            if (filterText == null || this.matchesFilter(name, filterText))
            {
                list.add(new DirectoryEntry(DirectoryEntryType.fromFile(file), dir, file.getName(), displayNamePrefix));
            }
        }
    }

    protected List<File> getSubDirectories(File dir)
    {
        List<File> dirs = new ArrayList<>();

        for (File file : dir.listFiles(DIRECTORY_FILTER))
        {
            dirs.add(file);
        }

        return dirs;
    }

    protected abstract File getRootDirectory();

    protected FileFilter getDirectoryFilter()
    {
        return DIRECTORY_FILTER;
    }

    protected abstract FileFilter getFileFilter();

    @Override
    protected WidgetDirectoryEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, DirectoryEntry entry)
    {
        return new WidgetDirectoryEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry),
                isOdd, entry, listIndex, this, this.iconProvider);
    }

    protected boolean currentDirectoryIsRoot()
    {
        return this.currentDirectory.equals(this.getRootDirectory());
    }

    @Override
    public File getCurrentDirectory()
    {
        return this.currentDirectory;
    }

    @Override
    public void switchToDirectory(File dir)
    {
        this.clearSelection();

        this.currentDirectory = FileUtils.getCanonicalFileIfPossible(dir);
        this.cache.setCurrentDirectoryForContext(this.browserContext, dir);

        this.refreshEntries();
        this.resetScrollbarPosition();
    }

    @Override
    public void switchToRootDirectory()
    {
        this.switchToDirectory(this.getRootDirectory());
    }

    @Override
    public void switchToParentDirectory()
    {
        File parent = this.currentDirectory.getParentFile();

        if (this.currentDirectoryIsRoot() == false &&
            parent != null &&
            this.currentDirectory.getAbsolutePath().contains(this.getRootDirectory().getAbsolutePath()))
        {
            this.switchToDirectory(parent);
        }
        else
        {
            this.switchToRootDirectory();
        }
    }

    public static class DirectoryEntry implements Comparable<DirectoryEntry>
    {
        private final DirectoryEntryType type;
        private final File dir;
        private final String name;
        @Nullable private final String displaynamePrefix;

        public DirectoryEntry(DirectoryEntryType type, File dir, String name, @Nullable String displaynamePrefix)
        {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.displaynamePrefix = displaynamePrefix;
        }

        public DirectoryEntryType getType()
        {
            return this.type;
        }

        public File getDirectory()
        {
            return this.dir;
        }

        public String getName()
        {
            return this.name;
        }

        @Nullable
        public String getDisplayNamePrefix()
        {
            return this.displaynamePrefix;
        }

        public String getDisplayName()
        {
            return this.displaynamePrefix != null ? this.displaynamePrefix + this.name : this.name;
        }

        public File getFullPath()
        {
            return new File(this.dir, this.name);
        }

        @Override
        public int compareTo(DirectoryEntry other)
        {
            return this.name.toLowerCase(Locale.US).compareTo(other.getName().toLowerCase(Locale.US));
        }
    }

    public enum DirectoryEntryType
    {
        INVALID,
        DIRECTORY,
        FILE;

        public static DirectoryEntryType fromFile(File file)
        {
            if (file.exists() == false)
            {
                return INVALID;
            }
            else if (file.isDirectory())
            {
                return DIRECTORY;
            }
            else
            {
                return FILE;
            }
        }
    }

    public static class FileFilterDirectories implements FileFilter
    {
        @Override
        public boolean accept(File pathName)
        {
            return pathName.isDirectory() && pathName.getName().startsWith(".") == false;
        }
    }
}

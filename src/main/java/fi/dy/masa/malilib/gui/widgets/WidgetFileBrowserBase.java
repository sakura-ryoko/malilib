package fi.dy.masa.malilib.gui.widgets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class WidgetFileBrowserBase extends WidgetListBase<DirectoryEntry, WidgetDirectoryEntry>
		implements IDirectoryNavigator
{
    protected static final PathFilter DIRECTORY_FILTER = new PathFilter();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final IDirectoryCache cache;
    protected Path currentDirectory;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider;
    @Nullable protected WidgetDirectoryNavigation directoryNavigationWidget;

    public WidgetFileBrowserBase(int x, int y, int width, int height,
            IDirectoryCache cache, String browserContext, Path defaultDirectory,
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
    public boolean onKeyTyped(KeyInput input)
    {
        if (super.onKeyTyped(input))
        {
            return true;
        }

        if ((input.key() == KeyCodes.KEY_BACKSPACE || input.key() == KeyCodes.KEY_LEFT) && this.currentDirectoryIsRoot() == false)
        {
            this.switchToParentDirectory();
            return true;
        }
        else if ((input.key() == KeyCodes.KEY_RIGHT || input.key() == KeyCodes.KEY_ENTER) &&
                  this.getLastSelectedEntry() != null && this.getLastSelectedEntry().getType() == DirectoryEntryType.DIRECTORY)
        {
            this.switchToDirectory(this.getLastSelectedEntry().getDirectory().resolve(this.getLastSelectedEntry().getName()));
            return true;
        }

        return false;
    }

    @Override
    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        // Draw an outline around the entire file browser
        RenderUtils.drawOutlinedBox(drawContext, this.posX, this.posY, this.browserWidth, this.browserHeight, 0xB0000000, COLOR_HORIZONTAL_BAR);

        super.drawContents(drawContext, mouseX, mouseY, partialTicks);

        this.drawAdditionalContents(drawContext, mouseX, mouseY);
    }

    protected void drawAdditionalContents(DrawContext drawContext, int mouseX, int mouseY)
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

        Path dir = this.currentDirectory;

        if (Files.isDirectory(dir))
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

    protected void addNonFilteredContents(Path dir)
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

    protected void addFilteredContents(Path dir)
    {
        String filterText = this.widgetSearchBar.getFilter();
        List<DirectoryEntry> list = new ArrayList<>();
        this.addFilteredContents(dir, filterText, list, null);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(Path dir, String filterText, List<DirectoryEntry> listOut, @Nullable String prefix)
    {
        List<DirectoryEntry> list = new ArrayList<>();
        List<Path> subDirs = this.getSubDirectories(dir);
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
        list.clear();

        for (Path subDir : subDirs)
        {
            String pre;

            if (prefix != null)
            {
                pre = prefix + subDir.getFileName().toString() + "/";
            }
            else
            {
                pre = subDir.getFileName().toString() + "/";
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

    protected void addMatchingEntriesToList(PathFilter filter, Path dir, List<DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix)
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter))
        {
            for (Path file : stream)
            {
                String name = FileUtils.getNameWithoutExtension(file.getFileName().toString().toLowerCase());

                if (filterText == null || this.matchesFilter(name, filterText))
                {
                    list.add(new DirectoryEntry(DirectoryEntryType.fromFile(file), dir, file.getFileName().toString(), displayNamePrefix));
                }
            }
        }
        catch (Exception ignored) { }
    }

    protected void addMatchingEntriesToList(FileFilter filter, Path dir, List<DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix)
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter))
        {
            for (Path file : stream)
            {
                String name = FileUtils.getNameWithoutExtension(file.getFileName().toString().toLowerCase());

                if (filterText == null || this.matchesFilter(name, filterText))
                {
                    list.add(new DirectoryEntry(DirectoryEntryType.fromFile(file), dir, file.getFileName().toString(), displayNamePrefix));
                }
            }
        }
        catch (Exception ignored) { }
    }

    protected List<Path> getSubDirectories(Path dir)
    {
        List<Path> dirs = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, DIRECTORY_FILTER))
        {
            for (Path file : stream)
            {
                dirs.add(file);
            }
        }
        catch (Exception ignored) { }

        return dirs;
    }

    protected abstract Path getRootDirectory();

    protected PathFilter getDirectoryFilter()
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
    public Path getCurrentDirectory()
    {
        return this.currentDirectory;
    }

    @Override
    public void switchToDirectory(Path dir)
    {
        this.clearSelection();

        this.currentDirectory = FileUtils.getRealPathIfPossible(dir);
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
        Path parent = this.currentDirectory.getParent();

        if (this.currentDirectoryIsRoot() == false &&
            parent != null &&
            this.currentDirectory.toAbsolutePath().toString().contains(this.getRootDirectory().toAbsolutePath().toString()))
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
        //private final File dir;
        private final Path dir;
        private final String name;
        @Nullable private final String displaynamePrefix;

        public DirectoryEntry(DirectoryEntryType type, Path dir, String name, @Nullable String displaynamePrefix)
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

        public Path getDirectory()
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

        public Path getFullPath()
        {
            return this.dir.resolve(this.name);
        }

        @Override
        public int compareTo(DirectoryEntry other)
        {
            //return this.name.toLowerCase(Locale.US).compareTo(other.getName().toLowerCase(Locale.US));
            return this.name.toLowerCase().compareTo(other.getName().toLowerCase());
        }
    }

    public enum DirectoryEntryType
    {
        INVALID,
        DIRECTORY,
        FILE;

        public static DirectoryEntryType fromFile(Path file)
        {
            if (!Files.exists(file))
            {
                return INVALID;
            }
            else if (Files.isDirectory(file))
            {
                return DIRECTORY;
            }
            else if (Files.isRegularFile(file))
            {
                return FILE;
            }
            else
            {
                return INVALID;
            }
        }
    }

    public static class PathFilter implements DirectoryStream.Filter<Path>
    {
        @Override
        public boolean accept(Path entry) throws IOException
        {
            try
            {
                return Files.isDirectory(entry) && !entry.getFileName().startsWith(".");
            }
            catch (Exception err)
            {
                throw new IOException(err.getMessage());
            }
        }
    }

    public static class FileFilter implements DirectoryStream.Filter<Path>
    {
        @Override
        public boolean accept(Path entry) throws IOException
        {
            try
            {
                return Files.isRegularFile(entry) && !entry.getFileName().startsWith(".");
            }
            catch (Exception err)
            {
                throw new IOException(err.getMessage());
            }
        }
    }

}

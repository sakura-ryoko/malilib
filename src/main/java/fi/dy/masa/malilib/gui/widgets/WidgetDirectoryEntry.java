package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntryType;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileNameUtils;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

public class WidgetDirectoryEntry extends WidgetListEntryBase<DirectoryEntry>
{
    protected final IDirectoryNavigator navigator;
    protected final DirectoryEntry entry;
    protected final IFileBrowserIconProvider iconProvider;
    protected final boolean isOdd;

    public WidgetDirectoryEntry(int x, int y, int width, int height, boolean isOdd, DirectoryEntry entry,
            int listIndex, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, entry, listIndex);

        this.isOdd = isOdd;
        this.entry = entry;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.navigator.switchToDirectory(this.entry.getDirectory().resolve(this.entry.getName()));
        }
        else
        {
            return super.onMouseClickedImpl(click, doubleClick);
        }

        return true;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY))
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x70FFFFFF);
        }
        else if (this.isOdd)
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x38FFFFFF);
        }

        IGuiIcon icon = null;

        switch (this.entry.getType())
        {
            case DIRECTORY:
                icon = this.iconProvider.getIconDirectory();
                break;

            default:
                icon = this.iconProvider.getIconForFile(this.entry.getFullPath());
        }

        int iconWidth = icon != null ? icon.getWidth() : 0;
        int xOffset = iconWidth + 2;

        if (icon != null)
        {
            icon.renderAt(drawContext, this.x, this.y + (this.height - icon.getHeight()) / 2, this.zLevel + 10, false, false);
        }

        // Draw an outline if this is the currently selected entry
        if (selected)
        {
            RenderUtils.drawOutline(drawContext, this.x, this.y, this.width, this.height, 0xEEEEEEEE);
        }

        int yOffset = (this.height - this.fontHeight) / 2 + 1;
        this.drawString(drawContext, this.x + xOffset + 2, this.y + yOffset, 0xFFFFFFFF, this.getDisplayName());

        super.render(drawContext, mouseX, mouseY, selected);
    }

    protected String getDisplayName()
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            return this.entry.getDisplayName();
        }
        else
        {
            return FileNameUtils.getFileNameWithoutExtension(this.entry.getDisplayName());
        }
    }
}

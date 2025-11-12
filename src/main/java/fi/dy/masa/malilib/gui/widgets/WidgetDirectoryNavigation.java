package fi.dy.masa.malilib.gui.widgets;

import java.nio.file.Path;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextInputFeedback;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.*;

public class WidgetDirectoryNavigation extends WidgetSearchBar
{
    protected final Path currentDir;
    protected final Path rootDir;
    protected final IDirectoryNavigator navigator;
    protected final WidgetIcon iconRoot;
    protected final WidgetIcon iconUp;
    protected final WidgetIcon iconCreateDir;

    public WidgetDirectoryNavigation(int x, int y, int width, int height,
                                     Path currentDir, Path rootDir,
                                     IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, 0, iconProvider.getIconSearch(), LeftRight.RIGHT);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.navigator = navigator;
        this.iconRoot = new WidgetIcon(x, y + 1, iconProvider.getIconRoot());
        x += this.iconRoot.getWidth() + 2;

        this.iconUp = new WidgetIcon(x, y + 1, iconProvider.getIconUp());
        x += this.iconUp.getWidth() + 2;

        this.iconCreateDir = new WidgetIcon(x, y + 1, iconProvider.getIconCreateDirectory());
//	    x += this.iconCreateDir.getWidth() + 2;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon((int) click.x(), (int) click.y());

            if (hoveredIcon == this.iconRoot)
            {
                this.navigator.switchToRootDirectory();
                return true;
            }
            else if (hoveredIcon == this.iconUp)
            {
                this.navigator.switchToParentDirectory();
                return true;
            }
            else if (hoveredIcon == this.iconCreateDir)
            {
                String title = "malilib.gui.title.create_directory";
                DirectoryCreator creator = new DirectoryCreator(this.currentDir, this.navigator);
                GuiTextInputFeedback gui = new GuiTextInputFeedback(256, title, "", GuiUtils.getCurrentScreen(), creator);
                GuiBase.openGui(gui);
                return true;
            }
        }

        return super.onMouseClickedImpl(click, doubleClick);
    }

    @Nullable
    protected WidgetIcon getHoveredIcon(int mouseX, int mouseY)
    {
        if (this.searchOpen == false)
        {
            if (this.iconRoot.isMouseOver(mouseX, mouseY))
            {
                return this.iconRoot;
            }
            else if (this.iconUp.isMouseOver(mouseX, mouseY))
            {
                return this.iconUp;
            }
            else if (this.iconCreateDir.isMouseOver(mouseX, mouseY))
            {
                return this.iconCreateDir;
            }
        }

        return null;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            this.iconRoot.render(drawContext, false, hoveredIcon == this.iconRoot);
            this.iconUp.render(drawContext, false, hoveredIcon == this.iconUp);
            this.iconCreateDir.render(drawContext, false, hoveredIcon == this.iconCreateDir);

            int pathStartX = this.iconCreateDir.x + this.iconCreateDir.getWidth() + 6;

            // Draw the directory path text background
            RenderUtils.drawRect(drawContext, pathStartX, this.y, this.width - pathStartX - 2, this.height, 0x20FFFFFF);

            int textColor = 0xC0C0C0C0;
            int maxLen = (this.width - 40) / this.getStringWidth("a") - 4; // FIXME
            String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
            this.drawString(drawContext, pathStartX + 3, this.y + 3, textColor, path);
        }
    }

    @Override
    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(drawContext, mouseX, mouseY, selected);

        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            if (hoveredIcon == this.iconRoot)
            {
                RenderUtils.drawHoverText(drawContext, mouseX, mouseY, Collections.singletonList(StringUtils.translate("malilib.gui.button.hover.directory_widget.root")));
            }
            else if (hoveredIcon == this.iconUp)
            {
                RenderUtils.drawHoverText(drawContext, mouseX, mouseY, Collections.singletonList(StringUtils.translate("malilib.gui.button.hover.directory_widget.up")));
            }
            else if (hoveredIcon == this.iconCreateDir)
            {
                RenderUtils.drawHoverText(drawContext, mouseX, mouseY, Collections.singletonList(StringUtils.translate("malilib.gui.button.hover.directory_widget.create_directory")));
            }
        }
    }
}

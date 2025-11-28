package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class WidgetSearchBar extends WidgetBase
{
    protected final WidgetIcon iconSearch;
    protected final LeftRight iconAlignment;
    protected final GuiTextFieldGeneric searchBox;
    protected boolean searchOpen;

    public WidgetSearchBar(int x, int y, int width, int height,
            int searchBarOffsetX, IGuiIcon iconSearch, LeftRight iconAlignment)
    {
        super(x, y, width, height);

        int iw = iconSearch.getWidth();
        int ix = iconAlignment == LeftRight.RIGHT ? x + width - iw - 1 : x + 2;
        int tx = iconAlignment == LeftRight.RIGHT ? x - searchBarOffsetX + 1 : x + iw + 6 + searchBarOffsetX;
        this.iconSearch = new WidgetIcon(ix, y + 1, iconSearch);
        this.iconAlignment = iconAlignment;
        this.searchBox = new GuiTextFieldGeneric(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), height, this.textRenderer);
        this.searchBox.setZLevel(this.zLevel);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getValue().toLowerCase() : "";
    }

    public boolean hasFilter()
    {
        return this.searchOpen && this.searchBox.getValue().isEmpty() == false;
    }

    public boolean isSearchOpen()
    {
        return this.searchOpen;
    }

    public void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (this.searchOpen)
        {
            this.searchBox.setFocused(true);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        if (this.searchOpen && this.searchBox.mouseClicked(click, doubleClick))
        {
            return true;
        }
        else if (this.iconSearch.isMouseOver((int) click.x(), (int) click.y()))
        {
            this.setSearchOpen(! this.searchOpen);
			this.searchBox.onClick(click, false);
            return true;
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(KeyEvent input)
    {
        if (this.searchOpen)
        {
            if (this.searchBox.keyPressed(input))
            {
                return true;
            }
            else if (input.key() == KeyCodes.KEY_ESCAPE)
            {
                if (input.hasShiftDown() && this.mc.screen != null)
                {
                    this.mc.screen.onClose();
                }

                this.searchOpen = false;
                this.searchBox.setFocused(false);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        if (this.searchOpen)
        {
	        return this.searchBox.charTyped(input);
        }
        else if (input.isAllowedChatCharacter())
        {
            this.searchOpen = true;
            this.searchBox.setFocused(true);
            this.searchBox.setValue("");
            this.searchBox.charTyped(input);

            return true;
        }

        return false;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
        this.iconSearch.render(ctx, false, this.iconSearch.isMouseOver(mouseX, mouseY));

        if (this.searchOpen)
        {
            this.searchBox.render(ctx.getGuiGraphics(), mouseX, mouseY, 0);
        }
    }
}

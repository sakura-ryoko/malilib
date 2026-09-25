package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.util.input.ScanCodes;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.MathUtils;

/**
 * A dropdown selection widget for entries in the given list.
 * If the entries extend {@link fi.dy.masa.malilib.interfaces.IStringValue}, then the {@link fi.dy.masa.malilib.interfaces.IStringValue#getStringValue()}
 * method is used for the display string, otherwise {@link #toString()} is used.
 * @author masa
 *
 * @param <T>
 */
public class WidgetDropDownList<T> extends WidgetBase
{
    protected final GuiScrollBar scrollBar = new GuiScrollBar();
    protected final List<T> entries;
    protected final List<T> filteredEntries;
    protected final TextFieldWrapper<GuiTextFieldGeneric> searchBar;
    protected final int maxVisibleEntries;
    protected final int totalHeight;
    protected boolean isOpen;
    protected boolean useScrollbar = true;
    protected int scrollbarWidth = 6;
    @Nullable protected final IStringRetriever<T> stringRetriever;
    @Nullable protected T selectedEntry;
    @Nullable protected Consumer<T> callback;
    protected int keyboardSelectionIndex = -1;

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries)
    {
        this(x, y, width, height, maxHeight, maxVisibleEntries, entries, null);
    }

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries, @Nullable IStringRetriever<T> stringRetriever)
    {
        super(x, y, width, height);

        this.width = this.getRequiredWidth(width, entries);
        this.entries = entries;
        this.filteredEntries = new ArrayList<>();
        this.stringRetriever = stringRetriever;

        int v = MathUtils.min(maxVisibleEntries, entries.size());
        v = MathUtils.min(v, maxHeight / height);
        v = MathUtils.min(v, (GuiUtils.getScaledWindowHeight() - y) / height);
        v = MathUtils.max(v, 1);

        this.maxVisibleEntries = v;
        this.totalHeight = (v + 1) * height;
        this.scrollBar.setMaxValue(entries.size() - this.maxVisibleEntries);

        TextFieldListener listener = new TextFieldListener(this);
        this.searchBar = new TextFieldWrapper<>(new GuiTextFieldGeneric(x + 1, y - 18, this.width - 2, 16, this.textRenderer), listener, TextFieldType.STRING);
        this.searchBar.textField().setFocused(true);

        this.updateFilteredEntries();

        if (this.entries.size() <= this.maxVisibleEntries)
        {
            this.useScrollbar = false;
            this.scrollbarWidth = 0;
        }
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);

        this.searchBar.textField().setX(x + 1);
        this.searchBar.textField().setY(y - 18);
    }

    protected int getRequiredWidth(int width, List<T> entries)
    {
        if (width == -1)
        {
            width = 0;

            for (T entry : entries)
            {
                width = MathUtils.max(width, this.getStringWidth(this.getDisplayString(entry)) + 20);
            }
        }

        return width;
    }

    @Nullable
    public T getSelectedEntry()
    {
        return this.selectedEntry;
    }

    public WidgetDropDownList<T> setSelectedEntry(T entry)
    {
        if (this.entries.contains(entry))
        {
            this.selectedEntry = entry;
        }

        return this;
    }

    protected void setSelectedEntry(int index)
    {
        if (index >= 0 && index < this.filteredEntries.size())
        {
            this.selectedEntry = this.filteredEntries.get(index);
        }

        if (this.callback != null)
        {
            this.callback.accept(this.selectedEntry);
        }
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int maxY = this.isOpen ? this.y + this.totalHeight : this.y + this.height;
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < maxY;
    }

    @Override
    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick) {
        if (this.isOpen && this.isMouseOver((int) click.x(), (int) click.y()) == false)
        {
            setOpen(false);
            return false;
        }
        return super.onMouseClicked(click, doubleClick);
    }

    private void setOpen(boolean open) {
        this.isOpen = open;

        if (open)
        {
            this.keyboardSelectionIndex = this.entries.indexOf(this.selectedEntry);
            this.scrollBar.setValue(this.keyboardSelectionIndex - this.maxVisibleEntries / 2);
        }
        else
        {
            this.keyboardSelectionIndex = -1;
        }
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
		int mouseX = (int) click.x();
		int mouseY = (int) click.y();

        if (this.isOpen && mouseY > this.y + this.height)
        {
            if (this.useScrollbar)
            {
                if (mouseX < this.x + this.width - this.scrollbarWidth-1)
                {
                    int relIndex = (mouseY - this.y - this.height) / this.height;
                    this.setSelectedEntry(this.scrollBar.getValue() + relIndex);
                }
                else
                {
                    if (this.scrollBar.wasMouseOver() == false)
                    {
                        int relY = mouseY - this.y - this.height;
                        int ddHeight = this.height * this.maxVisibleEntries;
                        int newPos = (int) (((double) relY / (double) ddHeight) * this.scrollBar.getMaxValue());

                        this.scrollBar.setValue(newPos);
                        this.scrollBar.handleDrag(mouseY, 123);
                    }

                    this.scrollBar.setIsDragging(true);
                }
            }
            else
            {
                if (mouseX < this.x + this.width)
                {
                    int relIndex = (mouseY - this.y - this.height) / this.height;
                    this.setSelectedEntry(this.scrollBar.getValue() + relIndex);
                }
            }
        }

        if (this.isOpen == false || (mouseX < this.x + this.width - this.scrollbarWidth-1 || mouseY < this.y + this.height))
        {
            setOpen(!this.isOpen);

            if (this.isOpen == false)
            {
                this.searchBar.textField().setValue("");
                this.updateFilteredEntries();
            }
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(MouseButtonEvent click)
    {
        this.scrollBar.setIsDragging(false);
    }

    @Override
    public boolean onMouseScrolledImpl(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.isOpen)
        {
            int amount = verticalAmount < 0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(KeyEvent input)
    {
        if (this.isOpen)
        {
            if (input.isEscape())
            {
                setOpen(false);
                return true;
            }
            else if (input.key() == ScanCodes.SCAN_RETURN)
            {
                if (this.keyboardSelectionIndex >= 0 && this.keyboardSelectionIndex < this.filteredEntries.size())
                {
                    this.setSelectedEntry(this.keyboardSelectionIndex);
                    setOpen(false);
                    this.searchBar.textField().setValue("");
                    return true;
                }
            }
            else if (input.key() == ScanCodes.SCAN_UP ||
                    input.key() == ScanCodes.SCAN_DOWN ||
                    input.key() == ScanCodes.SCAN_PAGE_UP ||
                    input.key() == ScanCodes.SCAN_PAGE_DOWN)
            {
                int changeAmount;

                if (input.key() == ScanCodes.SCAN_UP)
                    changeAmount = -1;
                else if (input.key() == ScanCodes.SCAN_DOWN)
                    changeAmount = 1;
                else if (input.key() == ScanCodes.SCAN_PAGE_UP)
                    changeAmount = -5;
                else
                    changeAmount = 5;

                this.onKeyboardNavigationChange(changeAmount);

                return true;
            }

            return this.searchBar.onKeyTyped(input);
        }

        return false;
    }

    protected void onKeyboardNavigationChange(int changeAmount)
    {
        this.keyboardSelectionIndex += changeAmount;

        if (this.keyboardSelectionIndex < 0)
        {
            this.keyboardSelectionIndex = 0;
        }
        else if (this.keyboardSelectionIndex >= this.filteredEntries.size())
        {
            this.keyboardSelectionIndex = this.filteredEntries.size() - 1;
        }

        int maxEntries = this.maxVisibleEntries;

        if (this.keyboardSelectionIndex < this.scrollBar.getValue())
        {
            this.scrollBar.setValue(this.keyboardSelectionIndex - maxEntries + 1);
        }
        else if (this.keyboardSelectionIndex >= this.scrollBar.getValue() + maxEntries)
        {
            this.scrollBar.setValue(this.keyboardSelectionIndex);
        }
    }

    @Override
    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        if (this.isOpen)
        {
            return this.searchBar.onCharTyped(input);
        }

        return false;
    }

    protected void updateFilteredEntries()
    {
        this.filteredEntries.clear();
        String filterText = this.searchBar.textField().getValue();

        if (this.isOpen && filterText.isEmpty() == false)
        {
            for (T entry : this.entries)
            {
                if (this.entryMatchesFilter(entry, filterText))
                {
                    this.filteredEntries.add(entry);
                }
            }

            this.scrollBar.setValue(0);
        }
        else
        {
            this.filteredEntries.addAll(this.entries);
        }

        this.useScrollbar = this.filteredEntries.size() > this.maxVisibleEntries;
        this.scrollBar.setMaxValue(this.filteredEntries.size() - this.maxVisibleEntries);
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().contains(filterText);
    }

    protected String getDisplayString(T entry)
    {
        if (entry != null)
        {
            if (this.stringRetriever != null)
            {
                return this.stringRetriever.getStringValue(entry);
            }

            return entry.toString();
        }

        return "-";
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.translate(0, 0, 10);
        Matrix3x2fStack matrixStackIn = ctx.pose();
        matrixStackIn.pushMatrix();
        // 10
        matrixStackIn.translate(0, 0);
        //RenderSystem.applyModelViewMatrix();

        ctx.drawOutlinedBox(this.x + 1, this.y, this.width - 2, this.height - 1, 0xFF101010, 0xFFC0C0C0);

        String str = this.getDisplayString(this.getSelectedEntry());
        int txtX = this.x + 4;
        int txtY = this.y + this.height / 2 - this.fontHeight / 2;
        // 100
        matrixStackIn.translate(0, 0);
        this.drawString(ctx, txtX, txtY, 0xFFE0E0E0, str);

        if (this.isOpen)
        {
            this.renderOpen(ctx, mouseX, mouseY, txtX, txtY);

            MaLiLibIcons i = MaLiLibIcons.ARROW_UP;
            ctx.drawTexturedRect(MaLiLibIcons.TEXTURE, this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
        }
        else
        {
            MaLiLibIcons i = MaLiLibIcons.ARROW_DOWN;
            ctx.drawTexturedRect(MaLiLibIcons.TEXTURE, this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
        }

        matrixStack.popMatrix();
        matrixStackIn.popMatrix();
    }

    private void renderOpen(GuiContext ctx, int mouseX, int mouseY, int txtX, int txtY)
    {
        List<T> list = this.filteredEntries;
        int visibleEntries = MathUtils.min(this.maxVisibleEntries, list.size());
        String str;

        txtY += this.height + 1;

        if (this.searchBar.textField().getValue().isEmpty() == false)
        {
            this.searchBar.draw(ctx, mouseX, mouseY);
        }

//        RenderUtils.drawOutline(ctx, this.x, this.y + this.height, this.width, visibleEntries * this.height + 2, 0xFFE0E0E0);
        ctx.drawOutlinedBox(this.x + 1, this.y + this.height, this.width - 2, visibleEntries * this.height, 0xFF101010, 0xFFE0E0E0);

        int y = this.y + this.height;
        int startIndex = Math.max(0, this.scrollBar.getValue());
        int max = Math.min(startIndex + this.maxVisibleEntries, list.size());

        int maxWidthEntry;

        if (this.useScrollbar)
        {
            maxWidthEntry = this.width - this.scrollbarWidth;
        }
        else
        {
            maxWidthEntry = this.width + 1;
        }

        for (int i = startIndex; i < max; ++i)
        {
            int bg = (i & 0x1) != 0 ? 0x20FFFFFF : 0x30FFFFFF;

            if (mouseX >= this.x && mouseX < this.x + maxWidthEntry-1 &&
                mouseY >= y && mouseY < y + this.height)
            {
                bg = 0x60FFFFFF;
            }

            ctx.drawRect(this.x, y, maxWidthEntry-2, this.height, bg);

            if (this.keyboardSelectionIndex == i && this.filteredEntries.size() > 1)
            {
                ctx.drawRect(this.x+1, y, 2, this.height, 0xFFEE1111);
                ctx.drawRect(this.x+maxWidthEntry-4, y, 2, this.height, 0xFFEE1111);
            }

            str = this.getDisplayString(list.get(i));
            this.drawString(ctx, txtX, txtY, 0xFFE0E0E0, str);
            y += this.height;
            txtY += this.height;
        }

        if (this.useScrollbar)
        {
            int x = this.x + maxWidthEntry-1;
            y = this.y + this.height;
            int h = visibleEntries * this.height + 1;
            int totalHeight = Math.max(h, list.size() * this.height);

            ctx.drawRect(x-1, y, 1, h, 0xFFE0E0E0);
            this.scrollBar.render(ctx, mouseX, mouseY, 0, x, y, this.scrollbarWidth, h, totalHeight);
        }
    }

    @Override
    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);

        // Draw it again to cover up other elements, when open
        if (this.isOpen)
        {
            int txtX = this.x + 4;
            int txtY = this.y + this.height / 2 - this.fontHeight / 2;

            ctx.elementUp();
            this.renderOpen(ctx, mouseX, mouseY, txtX, txtY);
        }
    }

    // TODO: figure out if a custom interface is better
	public void setSelectedEntryChangeCallback(Consumer<T> callback)
    {
        this.callback = callback;
	}

    protected record TextFieldListener(WidgetDropDownList<?> widget) implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
            this.widget.keyboardSelectionIndex = 0;
			this.widget.updateFilteredEntries();
			return true;
		}
	}
}

package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;

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
    protected final int maxHeight;
    protected final int maxVisibleEntries;
    protected final int totalHeight;
    protected boolean isOpen;
    protected int selectedIndex;
    protected int scrollbarWidth = 10;
    @Nullable protected final IStringRetriever<T> stringRetriever;
    @Nullable protected T selectedEntry;

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries)
    {
        this(x, y, width, height, maxHeight, maxVisibleEntries, entries, null);
    }

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, List<T> entries, @Nullable IStringRetriever<T> stringRetriever)
    {
        super(x, y, width, height);

        this.width = this.getRequiredWidth(width, entries, this.mc);
        this.maxHeight = maxHeight;
        this.entries = entries;
        this.filteredEntries = new ArrayList<>();
        this.stringRetriever = stringRetriever;

        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.min(v, (GuiUtils.getScaledWindowHeight() - y) / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.totalHeight = (v + 1) * height;
        this.scrollBar.setMaxValue(entries.size() - this.maxVisibleEntries);

        TextFieldListener listener = new TextFieldListener(this);
        this.searchBar = new TextFieldWrapper<>(new GuiTextFieldGeneric(x + 1, y - 18, this.width - 2, 16, this.textRenderer), listener);
        this.searchBar.getTextField().setFocused(true);

        this.updateFilteredEntries();
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);

        this.searchBar.getTextField().setX(x + 1);
        this.searchBar.getTextField().setY(y - 18);
    }

    protected int getRequiredWidth(int width, List<T> entries, MinecraftClient mc)
    {
        if (width == -1)
        {
            width = 0;

            for (int i = 0; i < entries.size(); ++i)
            {
                width = Math.max(width, this.getStringWidth(this.getDisplayString(entries.get(i))) + 20);
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
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int maxY = this.isOpen ? this.y + this.totalHeight : this.y + this.height;
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < maxY;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
		int mouseX = (int) click.x();
		int mouseY = (int) click.y();

        if (this.isOpen && mouseY > this.y + this.height)
        {
            if (mouseX < this.x + this.width - this.scrollbarWidth)
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

        if (this.isOpen == false || (mouseX < this.x + this.width - this.scrollbarWidth || mouseY < this.y + this.height))
        {
            this.isOpen = ! this.isOpen;

            if (this.isOpen == false)
            {
                this.searchBar.getTextField().setText("");
                this.updateFilteredEntries();
            }
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(Click click)
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
    protected boolean onKeyTypedImpl(KeyInput input)
    {
        if (this.isOpen)
        {
            return this.searchBar.onKeyTyped(input);
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharInput input)
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
        String filterText = this.searchBar.getTextField().getText();

        if (this.isOpen && filterText.isEmpty() == false)
        {
            for (int i = 0; i < this.entries.size(); ++i)
            {
                T entry = this.entries.get(i);

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

        this.scrollBar.setMaxValue(this.filteredEntries.size() - this.maxVisibleEntries);
    }

    protected boolean entryMatchesFilter(T entry, String filterText)
    {
        return filterText.isEmpty() || this.getDisplayString(entry).toLowerCase().indexOf(filterText) != -1;
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
//        RenderUtils.color(1f, 1f, 1f, 1f);

        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.translate(0, 0, 10);
        Matrix3x2fStack matrixStackIn = drawContext.getMatrices();
        matrixStackIn.pushMatrix();
        // 10
        matrixStackIn.translate(0, 0);
        //RenderSystem.applyModelViewMatrix();

        List<T> list = this.filteredEntries;
        int visibleEntries = Math.min(this.maxVisibleEntries, list.size());

//        RenderUtils.depthMask(true);
//        RenderUtils.depthTest(true);
        RenderUtils.drawOutlinedBox(drawContext, this.x + 1, this.y, this.width - 2, this.height - 1, 0xFF101010, 0xFFC0C0C0);

        String str = this.getDisplayString(this.getSelectedEntry());
        int txtX = this.x + 4;
        int txtY = this.y + this.height / 2 - this.fontHeight / 2;
        // 100
        matrixStackIn.translate(0, 0);
        this.drawString(drawContext, txtX, txtY, 0xFFE0E0E0, str);
        txtY += this.height + 1;
        int scrollWidth = 10;

        if (this.isOpen)
        {
            if (this.searchBar.getTextField().getText().isEmpty() == false)
            {
                this.searchBar.draw(drawContext, mouseX, mouseY);
            }

//            RenderUtils.depthMask(true);
//            RenderUtils.depthTest(true);
            RenderUtils.drawOutline(drawContext, this.x, this.y + this.height, this.width, visibleEntries * this.height + 2, 0xFFE0E0E0);

            int y = this.y + this.height + 1;
            int startIndex = Math.max(0, this.scrollBar.getValue());
            int max = Math.min(startIndex + this.maxVisibleEntries, list.size());

            for (int i = startIndex; i < max; ++i)
            {
                int bg = (i & 0x1) != 0 ? 0x20FFFFFF : 0x30FFFFFF;

                if (mouseX >= this.x && mouseX < this.x + this.width - scrollWidth &&
                    mouseY >= y && mouseY < y + this.height)
                {
                    bg = 0x60FFFFFF;
                }

//                RenderUtils.depthMask(true);
//                RenderUtils.depthTest(true);
                RenderUtils.drawRect(drawContext, this.x, y, this.width - scrollWidth, this.height, bg);
                str = this.getDisplayString(list.get(i));
                this.drawString(drawContext, txtX, txtY, 0xFFE0E0E0, str);
                y += this.height;
                txtY += this.height;
            }

            int x = this.x + this.width - this.scrollbarWidth - 1;
            y = this.y + this.height + 1;
            int h = visibleEntries * this.height;
            int totalHeight = Math.max(h, list.size() * this.height);

//            RenderUtils.depthMask(true);
//            RenderUtils.depthTest(true);
            this.scrollBar.render(drawContext, mouseX, mouseY, 0, x, y, this.scrollbarWidth, h, totalHeight);

//            VertexConsumer buffer = this.bindTexture(MaLiLibIcons.TEXTURE, drawContext);
//            Matrix4f posMatrix = drawContext.getMatrices().peek().getPositionMatrix();

            MaLiLibIcons i = MaLiLibIcons.ARROW_UP;
            RenderUtils.drawTexturedRect(drawContext, MaLiLibIcons.TEXTURE, this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
        }
        else
        {
//            VertexConsumer buffer = this.bindTexture(MaLiLibIcons.TEXTURE, drawContext);
//            Matrix4f posMatrix = drawContext.getMatrices().peek().getPositionMatrix();

            MaLiLibIcons i = MaLiLibIcons.ARROW_DOWN;
            RenderUtils.drawTexturedRect(drawContext, MaLiLibIcons.TEXTURE, this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
        }

//        RenderUtils.depthMask(false);
//        RenderUtils.depthTest(false);
        matrixStack.popMatrix();
        matrixStackIn.popMatrix();
    }

    @Override
    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(drawContext, mouseX, mouseY, selected);

        // Draw it again to cover up other elements, when open
        if (this.isOpen)
        {
            this.render(drawContext, mouseX, mouseY, selected);
            //RenderUtils.forceDraw(drawContext);
        }
    }

    protected static class TextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        protected final WidgetDropDownList<?> widget;

        protected TextFieldListener(WidgetDropDownList<?> widget)
        {
            this.widget = widget;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            this.widget.updateFilteredEntries();
            return true;
        }
    }
}

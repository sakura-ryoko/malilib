package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class GuiListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>, WIDGETLIST extends WidgetListBase<TYPE, WIDGET>> extends GuiBase
{
    private int listX;
    private int listY;
    private WIDGETLIST widget;

    protected GuiListBase(int listX, int listY)
    {
        this.setListPosition(listX, listY);
    }

    protected void setListPosition(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected int getListX()
    {
        return this.listX;
    }

    protected int getListY()
    {
        return this.listY;
    }

    protected abstract WIDGETLIST createListWidget(int listX, int listY);

    protected abstract int getBrowserWidth();

    protected abstract int getBrowserHeight();

    @Nullable
    protected ISelectionListener<TYPE> getSelectionListener()
    {
        return null;
    }

    @Nullable
    protected WIDGETLIST getListWidget()
    {
        if (this.widget == null)
        {
            this.reCreateListWidget();
        }

        return this.widget;
    }

    protected void reCreateListWidget()
    {
        this.widget = this.createListWidget(this.listX, this.listY);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        if (this.getListWidget() != null)
        {
            this.getListWidget().setSize(this.getBrowserWidth(), this.getBrowserHeight());
            this.getListWidget().initGui();
        }
    }

    @Override
    public void removed()
    {
        super.removed();

        if (this.getListWidget() != null)
        {
            this.getListWidget().removed();
        }
    }

    @Override
    public boolean onMouseClicked(Click click, boolean doubleClick)
    {
        if (super.onMouseClicked(click, doubleClick))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseClicked(click, doubleClick);
    }

    @Override
    public boolean onMouseReleased(Click click)
    {
        if (super.onMouseReleased(click))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseReleased(click);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean onKeyTyped(KeyInput input)
    {
        // Try to handle everything except ESC in the parent first
        if (input.key() != KeyCodes.KEY_ESCAPE && super.onKeyTyped(input))
        {
            return true;
        }

        if (this.getListWidget() != null && this.getListWidget().onKeyTyped(input))
        {
            return true;
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        if (input.key() == KeyCodes.KEY_ESCAPE && super.onKeyTyped(input))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean onCharTyped(CharInput input)
    {
        // Try to handle everything except ESC in the parent first
        if (super.onCharTyped(input))
        {
            return true;
        }

        if (this.getListWidget() != null && this.getListWidget().onCharTyped(input))
        {
            return true;
        }

        return super.onCharTyped(input);
    }

    @Override
    public void resize(MinecraftClient mc, int width, int height)
    {
        super.resize(mc, width, height);

        if (this.getListWidget() != null)
        {
            this.getListWidget().resize(mc, width, height);
        }
    }

    @Override
    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getListWidget() != null)
        {
            this.getListWidget().drawContents(drawContext, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void drawHoveredWidget(DrawContext drawContext, int mouseX, int mouseY)
    {
        super.drawHoveredWidget(drawContext, mouseX, mouseY);

        if (this.getListWidget() != null && this.shouldRenderHoverStuff())
        {
            this.getListWidget().drawHoveredWidget(drawContext, mouseX, mouseY);
            this.getListWidget().drawButtonHoverTexts(drawContext, mouseX, mouseY, 0f);
        }
    }
}

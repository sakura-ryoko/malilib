package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double horizontalAmount, double verticalAmount)
    {
        if (super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        // Try to handle everything except ESC in the parent first
        if (keyCode != KeyCodes.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if (this.getListWidget() != null && this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        if (keyCode == KeyCodes.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        // Try to handle everything except ESC in the parent first
        if (super.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        if (this.getListWidget() != null && this.getListWidget().onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
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
    protected void drawHoveredWidget(int mouseX, int mouseY, DrawContext drawContext)
    {
        super.drawHoveredWidget(mouseX, mouseY, drawContext);

        if (this.getListWidget() != null && this.shouldRenderHoverStuff())
        {
            this.getListWidget().drawHoveredWidget(mouseX, mouseY, drawContext);
            this.getListWidget().drawButtonHoverTexts(mouseX, mouseY, 0f, drawContext);
        }
    }
}

package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public abstract class WidgetContainer extends WidgetBase
{
    protected final List<WidgetBase> subWidgets = new ArrayList<>();
    @Nullable protected WidgetBase hoveredSubWidget = null;

    public WidgetContainer(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    protected <T extends WidgetBase> T addWidget(T widget)
    {
        this.subWidgets.add(widget);

        return widget;
    }

    protected <T extends ButtonBase> T addButton(T button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.addWidget(button);

        return button;
    }

    protected void addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        if (lines != null && lines.length >= 1)
        {
            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, this.getStringWidth(line));
                }
            }

            WidgetLabel label = new WidgetLabel(x, y, width, height, textColor, lines);
            this.addWidget(label);
        }
    }

    @Override
    public boolean onMouseClicked(Click click, boolean doubleClick)
    {
        boolean handled = false;

        if (this.isMouseOver((int) click.x(), (int) click.y()))
        {
            if (this.subWidgets.isEmpty() == false)
            {
                for (WidgetBase widget : this.subWidgets)
                {
                    if (widget.isMouseOver((int) click.x(), (int) click.y()) && widget.onMouseClicked(click, doubleClick))
                    {
                        // Don't call super if the button press got handled
                        handled = true;
                    }
                }
            }

            if (handled == false)
            {
                handled = this.onMouseClickedImpl(click, doubleClick);
            }
        }

        return handled;
    }

    @Override
    public void onMouseReleased(Click click)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                widget.onMouseReleased(click);
            }
        }

        this.onMouseReleasedImpl(click);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        if (this.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (this.subWidgets.isEmpty() == false)
            {
                for (WidgetBase widget : this.subWidgets)
                {
                    if (widget.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
                    {
                        return true;
                    }
                }
            }

            return this.onMouseScrolledImpl(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(KeyInput input)
    {
        boolean handled = false;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                if (widget.onKeyTyped(input))
                {
                    // Don't call super if the key press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            handled = this.onKeyTypedImpl(input);
        }

        return handled;
    }

    @Override
    public boolean onCharTyped(CharInput input)
    {
        boolean handled = false;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                if (widget.onCharTyped(input))
                {
                    // Don't call super if the key press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            handled = this.onCharTypedImpl(input);
        }

        return handled;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
        this.drawSubWidgets(drawContext, mouseX, mouseY);
    }

    @Override
    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(drawContext, mouseX, mouseY, selected);
        this.drawHoveredSubWidget(drawContext, mouseX, mouseY);
    }

    protected void drawSubWidgets(DrawContext drawContext, int mouseX, int mouseY)
    {
        this.hoveredSubWidget = null;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                widget.render(drawContext, mouseX, mouseY, false);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredSubWidget = widget;
                }
            }
        }
    }

    protected void drawHoveredSubWidget(DrawContext drawContext, int mouseX, int mouseY)
    {
        if (this.hoveredSubWidget != null)
        {
            this.hoveredSubWidget.postRenderHovered(drawContext, mouseX, mouseY, false);
        }
    }
}

package fi.dy.masa.malilib.gui;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import fi.dy.masa.malilib.mixin.render.IMixinAbstractWidget;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class GuiTextFieldMultiLine extends MultiLineEditBox
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean hasScrollbar;

    private GuiTextFieldMultiLine(int x, int y, int width, int height, String defaultText,
                                  Color4f textColor, Color4f cursorColor,
                                  boolean shadow, boolean background, boolean decorations, boolean scrollbar,
                                  Font textRenderer)
    {
        super(textRenderer, x, y, width, height,
              CommonComponents.EMPTY, Component.literal(defaultText),
              textColor.getIntValue(), shadow,
              cursorColor.getIntValue(), background, decorations);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hasScrollbar = scrollbar;
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent click, boolean doubleClick)
    {
        boolean ret = super.mouseClicked(click, doubleClick);

        if (this.isMouseOver((int) click.x(), (int) click.y()))
        {
            if (click.input() == 1)
            {
                this.setValue("");
            }

            this.setFocused(true);
            return true;
        }
        else
        {
            this.setFocused(false);
        }

        return ret;
    }

    @Override
    public int getX()
    {
        return this.x;
    }

    @Override
    public int getY()
    {
        return this.y;
    }

    @Override
    public void setX(int x)
    {
        this.x = x;
    }

    @Override
    public void setY(int y)
    {
        this.y = y;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + (this.hasScrollbar() ? this.width + AbstractScrollArea.SCROLLBAR_WIDTH : this.width) &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public boolean hasScrollbar()
    {
        return this.hasScrollbar;
    }

//    @Override
//    public void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta)
//    {
//            super.renderWidget(context, mouseX, mouseY, delta);
//    }

	/**
	 * Render a hover tooltip for this Text Field
	 * @param translationKey ()
	 * @param args ()
	 */
	public void setHoverTooltip(String translationKey, Object... args)
	{
		if (translationKey != null && !translationKey.isEmpty())
		{
			if (StringUtils.hasTranslation(translationKey))
			{
				this.setTooltip(Tooltip.create(StringUtils.translateAsText(translationKey, args)));
			}
			else
			{
				if (args != null && args.length > 0)
				{
					this.setTooltip(Tooltip.create(Component.nullToEmpty(String.format(translationKey, args))));
				}
				else
				{
					this.setTooltip(Tooltip.create(Component.nullToEmpty(translationKey)));
				}
			}
		}
	}

    public boolean hasTooltip()
    {
        return ((IMixinAbstractWidget) this).malilib_getTooltipHolder().get() != null;
    }

	/**
	 * Clear the Hover tooltip
	 */
	public void clearHoverTooltip()
	{
		this.setTooltip(null);
	}

	/**
     * For Compat/Crash prevention reasons
     * @param text ()
     */
    public void setValueWrapper(String text)
    {
        this.setValue(text);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public String getValueWrapper()
    {
        return this.getValue();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param focus ()
     */
    public void setFocusedWrapper(boolean focus)
    {
        this.setFocused(focus);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public boolean isFocusedWrapper()
    {
        return this.isFocused();
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getXWrapper()
    {
        return this.getX();
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getYWrapper()
    {
        return this.getY();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param x ()
     */
    public void setXWrapper(int x)
    {
        this.setX(x);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param y ()
     */
    public void setYWrapper(int y)
    {
        this.setY(y);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getWidthWrapper()
    {
        return this.getWidth();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param context ()
     * @param mouseX ()
     * @param mouseY ()
     * @param delta ()
     */
    public void renderWrapper(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        this.render(context, mouseX, mouseY, delta);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param input ()
     * @return ()
     */
    public boolean keyPressedWrapper(KeyEvent input)
    {
        return this.keyPressed(input);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param input ()
     * @return ()
     */
    public boolean charTypedWrapper(CharacterEvent input)
    {
        return this.charTyped(input);
    }

    /**
     * For Compat/Crash prevention reasons
     * @param click ()
     * @return ()
     */
    public boolean mouseClickedWrapper(MouseButtonEvent click, boolean doubleClick)
    {
        return this.mouseClicked(click, doubleClick);
    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width;
        private int height;
        private Color4f textColor = Color4f.WHITE;
        private Color4f cursorColor = this.textColor;
        private boolean shadow = true;
        private boolean background = true;
        private boolean decorations = true;
        private boolean scrollbar = false;

        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        public Builder setTextColor(Color4f color)
        {
            this.textColor = color;
            return this;
        }

        public Builder setCursorColor(Color4f color)
        {
            this.cursorColor = color;
            return this;
        }

        public Builder setShadow(boolean shadow)
        {
            this.shadow = shadow;
            return this;
        }

        public Builder setBackground(boolean background)
        {
            this.background = background;
            return this;
        }

        public Builder setDecorations(boolean decorations)
        {
            this.decorations = decorations;
            return this;
        }

        public Builder setScrollbar(boolean toggle)
        {
            this.scrollbar = toggle;
            return this;
        }

        public GuiTextFieldMultiLine build(Font textRenderer, String defaultText)
        {
            if (this.scrollbar)
            {
                this.width = this.width - AbstractScrollArea.SCROLLBAR_WIDTH;
            }
            return new GuiTextFieldMultiLine(this.x, this.y, this.width, this.height, defaultText,
                                             this.textColor, this.cursorColor,
                                             this.shadow, this.background, this.decorations, this.scrollbar,
                                             textRenderer);
        }
    }
}

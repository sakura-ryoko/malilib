package fi.dy.masa.malilib.gui;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import fi.dy.masa.malilib.mixin.render.IMixinAbstractWidget;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GuiTextFieldGeneric extends EditBox
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zLevel;

    public GuiTextFieldGeneric(int x, int y, int width, int height, Font textRenderer)
    {
        super(textRenderer, x, y, width, height, CommonComponents.EMPTY);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setMaxLength(256);
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
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public GuiTextFieldGeneric setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        if (this.zLevel != 0)
        {
            Matrix3x2fStack matrixStack = context.pose();
            matrixStack.pushMatrix();
            // this.zLevel
            matrixStack.translate(0, 0);

            super.renderWidget(context, mouseX, mouseY, delta);

            matrixStack.popMatrix();
        }
        else
        {
            super.renderWidget(context, mouseX, mouseY, delta);
        }
    }

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

    @Deprecated
    public void setTextWrapper(String text)
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

    @Deprecated
    public String getTextWrapper()
    {
        return this.getValue();
    }

    /**
     * For Compat/Crash prevention reasons
     * @param length ()
     */
    public void setMaxLengthWrapper(int length)
    {
        this.setMaxLength(length);
    }

    /**
     * For Compat/Crash prevention reasons
     * @return ()
     */
    public int getCursorWrapper()
    {
        return this.getCursorPosition();
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
}

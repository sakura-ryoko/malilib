package fi.dy.masa.malilib.gui.wrappers;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public record TextFieldWrapper<T extends GuiTextFieldGeneric>(T textField, ITextFieldListener<T> listener)
{
	public boolean isFocused()
	{
		return this.textField.isFocused();
	}

	public void setFocused(boolean isFocused)
	{
		this.textField.setFocused(isFocused);
	}

	public void onGuiClosed()
	{
		if (this.listener != null)
		{
			this.listener.onGuiClosed(this.textField);
		}
	}

	public void draw(GuiContext ctx, int mouseX, int mouseY)
	{
		this.textField.render(ctx.getGuiGraphics(), mouseX, mouseY, 0f);
	}

	public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick)
	{
		if (this.textField.mouseClicked(click, doubleClick))
		{
			return true;
		}

		if (this.textField.isMouseOver(click.x(), click.y()) == false)
		{
			this.textField.setFocused(false);
		}

		return false;
	}

	public boolean onKeyTyped(KeyEvent input)
	{
		String textPre = this.textField.getValue();

		if (this.textField.isFocused() && this.textField.keyPressed(input))
		{
			if (this.listener != null &&
					(input.key() == KeyCodes.KEY_ENTER || input.key() == KeyCodes.KEY_TAB ||
							this.textField.getValue().equals(textPre) == false))
			{
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
	}

	public boolean onCharTyped(CharacterEvent input)
	{
		String textPre = this.textField.getValue();

		if (this.textField.isFocused() && this.textField.charTyped(input))
		{
			if (this.listener != null && this.textField.getValue().equals(textPre) == false)
			{
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
	}
}

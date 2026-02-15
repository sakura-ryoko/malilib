package fi.dy.masa.malilib.gui.wrappers;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.gui.GuiTextFieldMultiLine;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldMultiLineListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;

@ApiStatus.Experimental
public class TextFieldMultiLineWrapper<T extends GuiTextFieldMultiLine>
{
	private final T textField;
	private final ITextFieldMultiLineListener<T> listener;

	public TextFieldMultiLineWrapper(T textField, int lines, ITextFieldMultiLineListener<T> listener)
	{
		this.textField = textField;
		this.listener = listener;
		this.textField.setLineLimit(lines);
	}

	public T textField()
	{
		return this.textField;
	}

	public ITextFieldMultiLineListener<T> listener()
	{
		return this.listener;
	}

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

	// Using the GuiContext here breaks the Hover Text from working
	public void draw(GuiContext ctx, int mouseX, int mouseY)
	{
		this.textField.renderWidget(ctx.getGuiGraphics(), mouseX, mouseY, 0f);
	}

	public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		if (this.textField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
		{
			return true;
		}

		if (this.textField.isMouseOver(mouseX, mouseY) == false)
		{
			this.textField.setFocused(false);
		}

		return false;
	}

	public boolean onMouseDragged(@NonNull MouseButtonEvent click, double dragXAmount, double dragYAmount)
	{
		if (this.textField.mouseDragged(click, dragXAmount, dragYAmount))
		{
			return true;
		}

		if (this.textField.isMouseOver(click.x(), click.y()) == false)
		{
			this.textField.setFocused(false);
		}

		return false;
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
			if (this.listener != null && !this.textField.getValue().equals(textPre))
			{
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
	}
}

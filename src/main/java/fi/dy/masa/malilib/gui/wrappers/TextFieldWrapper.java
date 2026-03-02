package fi.dy.masa.malilib.gui.wrappers;

import java.util.Optional;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.game.BlockUtils;

public class TextFieldWrapper<T extends GuiTextFieldGeneric>
{
	private final T textField;
	private final ITextFieldListener<T> listener;
	private final TextFieldType type;

	public TextFieldWrapper(T textField, ITextFieldListener<T> listener)
	{
		this(textField, listener, TextFieldType.STRING);
	}

	public TextFieldWrapper(T textField, ITextFieldListener<T> listener, TextFieldType type)
	{
		this.textField = textField;
		this.listener = listener;
		this.type = type;

		if (type.getMaxLength() > 0 && type.getMaxLength() < textField.getMaxLength())
		{
			textField.setMaxLength(type.getMaxLength());
		}
		else if (textField.getMaxLength() > 0 && textField.getMaxLength() < type.getMaxLength())
		{
			this.type.setMaxLength(textField.getMaxLength());
		}
	}

	public T textField()
	{
		return this.textField;
	}

	public ITextFieldListener<T> listener()
	{
		return this.listener;
	}

	public TextFieldType type()
	{
		return this.type;
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
			if (this.textField().hasTooltip() && input.key() == KeyCodes.KEY_BACKSPACE ||
				!this.textField.getValueWrapper().equals(textPre))
			{
				this.validateType();
			}

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
			if (!this.textField.getValue().equals(textPre))
			{
				this.validateType();
			}

			if (this.listener != null && !this.textField.getValue().equals(textPre))
			{
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
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

	public void validateType()
	{
		switch (this.type)
		{
			case DOUBLE ->
			{
				try
				{
					Double.parseDouble(this.textField.getValue());
					this.textField.clearHoverTooltip();
				}
				catch (Exception e)
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_double");
				}
			}
			case FLOAT ->
			{
				try
				{
					Float.parseFloat(this.textField.getValue());
					this.textField.clearHoverTooltip();
				}
				catch (Exception e)
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_float");
				}
			}
			case INTEGER ->
			{
				try
				{
					Integer.parseInt(this.textField.getValue());
					this.textField.clearHoverTooltip();
				}
				catch (Exception e)
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_integer");
				}
			}
			case BLOCK_ID ->
			{
				Identifier id = Identifier.tryParse(this.textField.getValue());

				if (id != null && BuiltInRegistries.BLOCK.getOptional(id).isPresent())
				{
					this.textField.clearHoverTooltip();
				}
				else
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_block_id");
				}
			}
			case BLOCK_STATE ->
			{
				Optional<BlockState> opt = BlockUtils.getBlockStateFromString(this.textField.getValue());

				if (opt.isPresent())
				{
					this.textField.clearHoverTooltip();
				}
				else
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_block_state");
				}
			}
			case VALID_STRING ->
			{
				final String val = this.textField.getValue();

				if (!this.type.getValidStrings().isEmpty() && this.type.getValidStrings().contains(val))
				{
					this.textField.clearHoverTooltip();
				}
				else
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_string", val);
				}
			}
			default ->
			{
				if (this.textField.getValue().length() > this.type.getMaxLength())
				{
					this.textField.setHoverTooltip("malilib.gui.text_field.invalid_length", this.type.getMaxLength());
				}
				else
				{
					this.textField.clearHoverTooltip();
				}
			}
		}
	}
}

package fi.dy.masa.malilib.gui.wrappers;

import java.util.Optional;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.game.BlockUtils;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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

    public T getTextField()
    {
        return this.textField;
    }

    public ITextFieldListener<T> getListener()
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

    public void draw(DrawContext drawContext, int mouseX, int mouseY)
    {
        this.textField.render(drawContext, mouseX, mouseY, 0f);
    }

    public boolean mouseClicked(Click click, boolean doubleClick)
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

    public boolean onKeyTyped(KeyInput input)
    {
        String textPre = this.textField.getText();

        if (this.textField.isFocused() && this.textField.keyPressed(input))
        {
            if (this.listener != null &&
                (input.key() == KeyCodes.KEY_ENTER || input.key() == KeyCodes.KEY_TAB ||
                 this.textField.getText().equals(textPre) == false))
            {
                this.listener.onTextChange(this.textField);
            }

            return true;
        }

        return false;
    }

    public boolean onCharTyped(CharInput input)
    {
        String textPre = this.textField.getText();

        if (this.textField.isFocused() && this.textField.charTyped(input))
        {
            if (this.listener != null && this.textField.getText().equals(textPre) == false)
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

    public boolean onMouseDragged(Click click, double dragXAmount, double dragYAmount)
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
                    Double.parseDouble(this.textField.getText());
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
                    Float.parseFloat(this.textField.getText());
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
                    Integer.parseInt(this.textField.getText());
                    this.textField.clearHoverTooltip();
                }
                catch (Exception e)
                {
                    this.textField.setHoverTooltip("malilib.gui.text_field.invalid_integer");
                }
            }
            case BLOCK_ID ->
            {
                Identifier id = Identifier.tryParse(this.textField.getText());

                if (id != null && Registries.BLOCK.getOptionalValue(id).isPresent())
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
                Optional<BlockState> opt = BlockUtils.getBlockStateFromString(this.textField.getText());

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
                final String val = this.textField.getText();

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
                if (this.textField.getText().length() > this.type.getMaxLength())
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

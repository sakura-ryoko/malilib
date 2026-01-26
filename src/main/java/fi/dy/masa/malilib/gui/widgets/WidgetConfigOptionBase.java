package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class WidgetConfigOptionBase<TYPE> extends WidgetListEntryBase<TYPE>
{
    protected final WidgetListConfigOptionsBase<?, ?> parent;
    @Nullable protected TextFieldWrapper<? extends GuiTextFieldGeneric> textField = null;
    @Nullable protected String initialStringValue;
    protected int maxTextfieldTextLength = 65535;
    /**
     * The last applied value for any textfield-based configs.
     * Button based (boolean, option-list) values get applied immediately upon clicking the button.
     */
    protected String lastAppliedValue;

    public WidgetConfigOptionBase(int x, int y, int width, int height,
            WidgetListConfigOptionsBase<?, ?> parent, TYPE entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.parent = parent;
    }

    public abstract boolean wasConfigModified();

    public boolean hasPendingModifications()
    {
        if (this.textField != null)
        {
            return this.textField.textField().getValue().equals(this.lastAppliedValue) == false;
        }

        return false;
    }

    public abstract void applyNewValueToConfig();

    protected GuiTextFieldGeneric createTextField(int x, int y, int width, int height)
    {
        return new GuiTextFieldGeneric(x + 2, y, width, height, this.textRenderer);
    }

    @Deprecated
    protected void addTextField(GuiTextFieldGeneric field, ConfigOptionChangeListenerTextField listener)
    {
        this.addTextField(field, listener, TextFieldType.STRING);
    }

    protected void addTextField(GuiTextFieldGeneric field, ConfigOptionChangeListenerTextField listener, TextFieldType type)
    {
        TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = new TextFieldWrapper<>(field, listener, type);
        this.textField = wrapper;
        this.parent.addTextField(wrapper);
    }

    protected ButtonGeneric createResetButton(int x, int y, IConfigResettable config)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(config.isModified());

        return resetButton;
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        if (super.onMouseClickedImpl(click, doubleClick))
        {
            return true;
        }

        boolean ret = false;

        if (this.textField != null)
        {
            ret |= this.textField.textField().mouseClicked(click, doubleClick);
        }

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                ret |= widget.isMouseOver((int) click.x(), (int) click.y()) && widget.onMouseClicked(click, doubleClick);
            }
        }

        return ret;
    }

    @Override
    public boolean onKeyTypedImpl(KeyEvent input)
    {
        if (this.textField != null && this.textField.isFocused())
        {
            if (input.key() == KeyCodes.KEY_ENTER)
            {
                this.applyNewValueToConfig();
                return true;
            }
            else
            {
                return this.textField.onKeyTyped(input);
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        if (this.textField != null && this.textField.onCharTyped(input))
        {
            return true;
        }

        return super.onCharTypedImpl(input);
    }

    @Override
    public boolean canSelectAt(MouseButtonEvent click)
    {
        return false;
    }

    protected void drawTextFields(GuiContext ctx, int mouseX, int mouseY)
    {
        if (this.textField != null)
        {
            this.textField.textField().render(ctx.getGuiGraphics(), mouseX, mouseY, 0f);
        }
    }
}

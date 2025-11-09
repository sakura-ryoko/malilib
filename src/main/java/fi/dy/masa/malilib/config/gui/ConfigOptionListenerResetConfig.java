package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.EditBox;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ConfigOptionListenerResetConfig implements IButtonActionListener
{
    private final IConfigResettable config;
    private final ButtonGeneric buttonReset;
    @Nullable private final ConfigResetterBase reset;
    @Nullable private final ButtonPressDirtyListenerSimple dirtyListener;

    public ConfigOptionListenerResetConfig(IConfigResettable config, @Nullable ConfigResetterBase reset,
            ButtonGeneric buttonReset, @Nullable ButtonPressDirtyListenerSimple dirtyListener)
    {
        this.config = config;
        this.reset = reset;
        this.buttonReset = buttonReset;
        this.dirtyListener = dirtyListener;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        this.config.resetToDefault();
        this.buttonReset.setEnabled(this.config.isModified());

        if (this.reset != null)
        {
            this.reset.resetConfigOption();
        }

        if (this.dirtyListener != null)
        {
            this.dirtyListener.actionPerformedWithButton(button, mouseButton);
        }
    }

    public abstract static class ConfigResetterBase
    {
        public abstract void resetConfigOption();
    }

    public static class ConfigResetterButton extends ConfigResetterBase
    {
        private final ButtonBase button;

        public ConfigResetterButton(ButtonBase button)
        {
            this.button = button;
        }

        @Override
        public void resetConfigOption()
        {
            this.button.updateDisplayString();
        }
    }

    public static class ConfigResetterTextField extends ConfigResetterBase
    {
        private final IStringRepresentable config;
        private final EditBox textField;

        public ConfigResetterTextField(IStringRepresentable config, EditBox textField)
        {
            this.config = config;
            this.textField = textField;
        }

        @Override
        public void resetConfigOption()
        {
            this.textField.setValue(this.config.getStringValue());
        }
    }
}

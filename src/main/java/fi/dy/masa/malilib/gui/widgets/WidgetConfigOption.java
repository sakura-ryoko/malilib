package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.config.gui.*;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.config.options.BooleanHotkeyGuiWrapper;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.GuiUtils;

public class WidgetConfigOption extends WidgetConfigOptionBase<ConfigOptionWrapper>
{
    protected final ConfigOptionWrapper wrapper;
    protected final IKeybindConfigGui host;
    @Nullable protected final KeybindSettings initialKeybindSettings;
    @Nullable protected ImmutableList<String> initialStringList;
    protected int colorDisplayPosX;
    private boolean initialBoolean;

    public WidgetConfigOption(int x, int y, int width, int height, int labelWidth, int configWidth,
            ConfigOptionWrapper wrapper, int listIndex, IKeybindConfigGui host, WidgetListConfigOptionsBase<?, ?> parent)
    {
        super(x, y, width, height, parent, wrapper, listIndex);

        this.host = host;
        this.wrapper = wrapper;

        if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = wrapper.getConfig();

            switch (config)
            {
                case BooleanHotkeyGuiWrapper booleanHotkey ->
                {
                    this.initialBoolean = booleanHotkey.getBooleanValue();
                    this.initialStringValue = booleanHotkey.getKeybind().getStringValue();
                    this.initialKeybindSettings = booleanHotkey.getKeybind().getSettings();
                }
                case ConfigBooleanHotkeyed booleanHotkey ->
                {
                    this.initialBoolean = booleanHotkey.getBooleanValue();
                    this.initialStringValue = booleanHotkey.getKeybind().getStringValue();
                    this.initialKeybindSettings = booleanHotkey.getKeybind().getSettings();
                }
                case IStringRepresentable configStr ->
                {
                    this.initialStringValue = configStr.getStringValue();
                    this.lastAppliedValue = configStr.getStringValue();
                    this.initialKeybindSettings = config.getType() == ConfigType.HOTKEY ? ((IHotkey) config).getKeybind().getSettings() : null;
                }
                case null, default ->
                {
                    this.initialStringValue = null;
                    this.lastAppliedValue = null;
                    this.initialKeybindSettings = null;

                    if (config instanceof IConfigStringList)
                    {
                        this.initialStringList = ImmutableList.copyOf(((IConfigStringList) config).getStrings());
                    }
                }
            }

			if (config != null)
			{
				this.addConfigOption(x, y, labelWidth, configWidth, config);
			}
        }
        else
        {
            this.initialStringValue = null;
            this.lastAppliedValue = null;
            this.initialKeybindSettings = null;

            this.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, wrapper.getLabel());
        }
    }

    protected void addConfigOption(int x, int y, int labelWidth, int configWidth, IConfigBase config)
    {
        ConfigType type = config.getType();

        y += 1;
        int configHeight = 20;

        String configName = config.getConfigGuiDisplayName();

        // DEBUG ONLY
        /*
        if (config.getConfigGuiDisplayName().contains(".config"))
        {
            configName = StringUtils.getTranslatedOrFallback(config.getConfigGuiDisplayName(), config.getConfigGuiDisplayName());

            if (configName != null)
            {
                int calc = super.getWidth() - configName.length();

                MaLiLib.logger.error("addConfigOption(): configName [{}] (length: {}) // super.width [{}] // calc [{}] // old labelWidth [{}]", configName, configName.length(), super.getWidth(), calc, labelWidth);

                if (labelWidth > super.getWidth() && configName.length() <= calc)
                {
                    labelWidth = Math.max(calc, this.getStringWidth(configName));
                }

                MaLiLib.logger.error("addConfigOption(): configName [{}] (length: {}) // new labelWidth [{}]", configName, configName.length(), labelWidth);
            }
            else
            {
                configName = config.getConfigGuiDisplayName();
            }
        }
        else
        {
            configName = config.getConfigGuiDisplayName();
        }
        MaLiLib.logger.error("addConfigOption(): configName [{}] (width: {}), labelWidth [{}]", configName, configName.length(), labelWidth);
         */

        this.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, configName);

        String comment;
        IConfigInfoProvider infoProvider = this.host.getHoverInfoProvider();

        if (infoProvider != null)
        {
            comment = infoProvider.getHoverInfo(config);
        }
        else
        {
            comment = config.getComment();
        }

        if (comment != null)
        {
            this.addConfigComment(x, y + 5, labelWidth, 12, comment);
        }

        x += labelWidth + 10;

        if (config instanceof BooleanHotkeyGuiWrapper wrapper)
        {
            IConfigBoolean booleanConfig = wrapper.getBooleanConfig();
            IKeybind keybind = wrapper.getKeybind();
            this.addBooleanAndHotkeyWidgets(x, y, configWidth, wrapper, booleanConfig, keybind);
        }
        else if (config instanceof ConfigBooleanHotkeyed hotkeyedBoolean)
        {
            IKeybind keybind = hotkeyedBoolean.getKeybind();
            this.addBooleanAndHotkeyWidgets(x, y, configWidth, hotkeyedBoolean, hotkeyedBoolean, keybind);
        }
        else if (type == ConfigType.BOOLEAN)
        {
            ConfigButtonBoolean optionButton = new ConfigButtonBoolean(x, y, configWidth, configHeight, (IConfigBoolean) config);
            this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.OPTION_LIST)
        {
            ConfigButtonOptionList optionButton = new ConfigButtonOptionList(x, y, configWidth, configHeight, (IConfigOptionList) config);
            this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.STRING_LIST)
        {
            ConfigButtonStringList optionButton = new ConfigButtonStringList(x, y, configWidth, configHeight, (IConfigStringList) config, this.host, this.host.getDialogHandler());
            this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.LOCKED_LIST)
        {
            ConfigButtonLockedList optionButton = new ConfigButtonLockedList(x, y, configWidth, configHeight, (IConfigLockedList) config, this.host, this.host.getDialogHandler());
            this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.COLOR_LIST)
        {
            ConfigButtonColorList optionButton = new ConfigButtonColorList(x, y, configWidth, configHeight, (IConfigColorList) config, this.host, this.host.getDialogHandler());
            this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.HOTKEY)
        {
            this.addHotkeyConfigElements(x, y, configWidth, config.getName(), (IHotkey) config);
        }
        else if (type == ConfigType.STRING ||
                 type == ConfigType.COLOR ||
                 type == ConfigType.INTEGER ||
                 type == ConfigType.DOUBLE ||
                 type == ConfigType.FLOAT)
        {
            int resetX = x + configWidth + 2;

            if (type == ConfigType.COLOR)
            {
                configWidth -= 22; // adjust the width to match other configs due to the color display
                this.colorDisplayPosX = x + configWidth + 2;
                this.addWidget(new WidgetColorIndicator(this.colorDisplayPosX, y + 1, 19, 19, (IConfigColor) config));
            }
            else if (type == ConfigType.INTEGER || type == ConfigType.DOUBLE || type == ConfigType.FLOAT)
            {
                configWidth -= 18;
                this.colorDisplayPosX = x + configWidth + 2;
            }

            if ((type == ConfigType.INTEGER || type == ConfigType.DOUBLE || type == ConfigType.FLOAT) &&
                config instanceof IConfigSlider && ((IConfigSlider) config).shouldUseSlider())
            {
                this.addConfigSliderEntry(x, y, resetX, configWidth, configHeight, (IConfigSlider) config);
            }
            else
            {
                this.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, (IConfigValue) config);
            }

            if (type != ConfigType.COLOR && config instanceof IConfigSlider)
            {
                IGuiIcon icon = ((IConfigSlider) config).shouldUseSlider() ? MaLiLibIcons.BTN_TXTFIELD : MaLiLibIcons.BTN_SLIDER;
                ButtonGeneric toggleBtn = new ButtonGeneric(this.colorDisplayPosX, y + 2, icon);
                this.addButton(toggleBtn, new ListenerSliderToggle((IConfigSlider) config));
            }
        }
		else if (type == ConfigType.TABLE)
        {
			ConfigButtonTable optionButton = new ConfigButtonTable(x, y, configWidth, configHeight, (IConfigTable) config, this.host, this.host.getDialogHandler());
	        this.addConfigButtonEntry(x + configWidth + 2, y, (IConfigResettable) config, optionButton);
        }
    }

    @Override
    public boolean wasConfigModified()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = this.wrapper.getConfig();
            boolean modified = false;

            if (config instanceof BooleanHotkeyGuiWrapper booleanHotkey)
            {
                IKeybind keybind = booleanHotkey.getKeybind();
                return this.initialBoolean != booleanHotkey.getBooleanValue() ||
                       this.initialStringValue.equals(keybind.getStringValue()) == false ||
                       this.initialKeybindSettings.equals(keybind.getSettings()) == false;
            }
            else if (config instanceof ConfigBooleanHotkeyed booleanHotkey)
            {
                IKeybind keybind = booleanHotkey.getKeybind();
                return this.initialBoolean != booleanHotkey.getBooleanValue() ||
                       this.initialStringValue.equals(keybind.getStringValue()) == false ||
                       this.initialKeybindSettings.equals(keybind.getSettings()) == false;
            }
            else if (config instanceof IStringRepresentable)
            {
                if (this.textField != null)
                {
                    modified |= this.initialStringValue.equals(this.textField.getTextField().getText()) == false;
                }

                if (this.initialKeybindSettings != null && this.initialKeybindSettings.equals(((IHotkey) config).getKeybind().getSettings()) == false)
                {
                    modified = true;
                }

                return modified || this.initialStringValue.equals(((IStringRepresentable) config).getStringValue()) == false;
            }
            else if (this.initialStringList != null && config instanceof IConfigStringList)
            {
                return this.initialStringList.equals(((IConfigStringList) config).getStrings()) == false;
            }
        }

        return false;
    }

    @Override
    public void applyNewValueToConfig()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG &&
            this.wrapper.getConfig() instanceof IStringRepresentable config)
        {
            if (this.textField != null && this.hasPendingModifications())
            {
                config.setValueFromString(this.textField.getTextField().getText());
            }

            this.lastAppliedValue = config.getStringValue();
        }
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        this.addWidget(new WidgetHoverInfo(x, y, width, height, comment));
    }

    protected void addHotkeyConfigElements(int x, int y, int configWidth, String configName, IHotkey hotkey)
    {
        configWidth -= 22; // adjust the width to match other configs due to the settings widget
        IKeybind keybind = hotkey.getKeybind();
        ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
        x += configWidth + 2;

        this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, configName, this.parent, this.host.getDialogHandler()));
        x += 22;

        this.addButton(keybindButton, this.host.getButtonPressListener());
        this.addKeybindResetButton(x, y, keybind, keybindButton);
    }

    protected void addBooleanAndHotkeyWidgets(int x, int y, int configWidth,
                                              IConfigResettable resettableConfig,
                                              IConfigBoolean booleanConfig,
                                              IKeybind keybind)
    {
        int booleanBtnWidth = 60;
        ConfigButtonBoolean booleanButton = new ConfigButtonBoolean(x, y, booleanBtnWidth, 20, booleanConfig);
        x += booleanBtnWidth + 2;
        configWidth -= booleanBtnWidth + 2 + 22;

        ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
        x += configWidth + 2;

        this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, booleanConfig.getName(), this.parent, this.host.getDialogHandler()));
        x += 22;

        ButtonGeneric resetButton = this.createResetButton(x, y, resettableConfig);

        ConfigOptionChangeListenerButton booleanChangeListener = new ConfigOptionChangeListenerButton(resettableConfig, resetButton, null);
        HotkeyedBooleanResetListener resetListener = new HotkeyedBooleanResetListener(resettableConfig, booleanButton, keybindButton, resetButton, this.host);

        this.host.addKeybindChangeListener(resetListener::updateButtons);

        this.addButton(booleanButton, booleanChangeListener);
        this.addButton(keybindButton, this.host.getButtonPressListener());
        this.addButton(resetButton, resetListener);
    }

    protected void addConfigButtonEntry(int xReset, int yReset, IConfigResettable config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(xReset, yReset, config);
        ConfigOptionChangeListenerButton listenerChange = new ConfigOptionChangeListenerButton(config, resetButton, null);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, null);

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigValue config)
    {
        GuiTextFieldGeneric field = this.createTextField(x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(this.maxTextfieldTextLength);
        field.setText(config.getStringValue());

        ButtonGeneric resetButton = this.createResetButton(resetX, y, config);
        ConfigOptionChangeListenerTextField listenerChange = new ConfigOptionChangeListenerTextField(config, field, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, field), resetButton, null);

        this.addTextField(field, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigSliderEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigSlider config)
    {
        ButtonGeneric resetButton = this.createResetButton(resetX, y, config);
        ISliderCallback callback;

        switch (config)
        {
            case IConfigDouble iConfigDouble -> callback = new SliderCallbackDouble(iConfigDouble, resetButton);
            case IConfigFloat iConfigFloat -> callback = new SliderCallbackFloat(iConfigFloat, resetButton);
            case IConfigInteger iConfigInteger -> callback = new SliderCallbackInteger(iConfigInteger, resetButton);
            default ->
            {
                return;
            }
        }

        WidgetSlider slider = new WidgetSlider(x, y, configWidth, configHeight, callback);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, null, resetButton, null);

        this.addWidget(slider);
        this.addButton(resetButton, listenerReset);
    }

    protected void addKeybindResetButton(int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey)
    {
        ButtonGeneric button = this.createResetButton(x, y, keybind);

        ConfigOptionChangeListenerKeybind listener = new ConfigOptionChangeListenerKeybind(keybind, buttonHotkey, button, this.host);
        this.host.addKeybindChangeListener(listener::updateButtons);
        this.addButton(button, listener);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

        this.drawSubWidgets(ctx, mouseX, mouseY);

        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            this.drawTextFields(ctx, mouseX, mouseY);
            super.render(ctx, mouseX, mouseY, selected);
        }
    }

	public record ListenerSliderToggle(IConfigSlider config) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			this.config.toggleUseSlider();

			Screen gui = GuiUtils.getCurrentScreen();

			if (gui instanceof GuiBase)
			{
				((GuiBase) gui).initGui();
			}
		}
	}

	public record HotkeyedBooleanResetListener(IConfigResettable config, ButtonGeneric booleanButton,
	                                           ConfigButtonKeybind hotkeyButton, ButtonGeneric resetButton,
	                                           IKeybindConfigGui host) implements IButtonActionListener
	{

		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			this.config.resetToDefault();
			this.host.getButtonPressListener().actionPerformedWithButton(button, mouseButton);
			this.updateButtons();
		}

		public void updateButtons()
		{
			this.booleanButton.updateDisplayString();
			this.hotkeyButton.updateDisplayString();
			this.resetButton.setEnabled(this.config.isModified());
		}
	}
}

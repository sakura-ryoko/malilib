package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.config.gui.*;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.GuiUtils;

public class WidgetConfigOption extends WidgetConfigOptionBase<ConfigOptionWrapper>
{
    protected final ConfigOptionWrapper wrapper;
    public final IKeybindConfigGui host;
    @Nullable protected final KeybindSettings initialKeybindSettings;
    @Nullable protected ImmutableList<@NotNull String> initialStringList;
    public int colorDisplayPosX;
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
				config.addConfigOption(x, y, labelWidth, configWidth, this);
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

    @Override
    public boolean wasConfigModified()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = this.wrapper.getConfig();
            boolean modified = false;

	        switch (config)
	        {
		        case BooleanHotkeyGuiWrapper booleanHotkey ->
		        {
			        IKeybind keybind = booleanHotkey.getKeybind();
			        return  this.initialBoolean != booleanHotkey.getBooleanValue() ||
					        this.initialStringValue.equals(keybind.getStringValue()) == false ||
					        this.initialKeybindSettings.equals(keybind.getSettings()) == false ||
					        config.isDirty();
		        }
		        case ConfigBooleanHotkeyed booleanHotkey ->
		        {
			        IKeybind keybind = booleanHotkey.getKeybind();
			        return  this.initialBoolean != booleanHotkey.getBooleanValue() ||
					        this.initialStringValue.equals(keybind.getStringValue()) == false ||
					        this.initialKeybindSettings.equals(keybind.getSettings()) == false ||
					        config.isDirty();
		        }
		        case IStringRepresentable iStringRepresentable ->
		        {
			        if (this.textField != null)
			        {
				        modified |= this.initialStringValue.equals(this.textField.textField().getValue()) == false ||
						        config.isDirty();
			        }

			        if (this.initialKeybindSettings != null && this.initialKeybindSettings.equals(((IHotkey) config).getKeybind().getSettings()) == false)
			        {
				        modified = true;
			        }

			        return  modified || this.initialStringValue.equals(iStringRepresentable.getStringValue()) == false ||
					        config.isDirty();
		        }
		        case IConfigStringList iConfigStringList when this.initialStringList != null ->
		        {
			        return  this.initialStringList.equals(iConfigStringList.getStrings()) == false ||
					        config.isDirty();
		        }
		        case null, default ->
		        {
			        return config != null && config.isDirty();
		        }
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
                config.setValueFromString(this.textField.textField().getValue());
            }

            this.lastAppliedValue = config.getStringValue();
        }
    }
	
	public void addConfigComment(int x, int y, int width, int height, String comment)
    {
        this.addWidget(new WidgetHoverInfo(x, y, width, height, comment));
    }
	
	public void addHotkeyConfigElements(int x, int y, int configWidth, String configName, IHotkey hotkey)
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
	
	public void addBooleanAndHotkeyWidgets(int x, int y, int configWidth,
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
	
	public void addConfigButtonEntry(int xReset, int yReset, IConfigResettable config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(xReset, yReset, config);
        ConfigOptionChangeListenerButton listenerChange = new ConfigOptionChangeListenerButton(config, resetButton, null);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, null);

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    public void addConfigTextFieldEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigValue config, TextFieldType type)
    {
        GuiTextFieldGeneric field = this.createTextField(x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(type.getMaxLength() > 0 ? type.getMaxLength() : this.maxTextfieldTextLength);
        field.setValue(config.getStringValue());

        ButtonGeneric resetButton = this.createResetButton(resetX, y, config);
        ConfigOptionChangeListenerTextField listenerChange = new ConfigOptionChangeListenerTextField(config, field, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, field), resetButton, null);

        this.addTextField(field, listenerChange, type);
        this.addButton(resetButton, listenerReset);
    }

    public void addConfigSliderEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigSlider config)
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
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
//        super.render(ctx, mouseX, mouseY, selected);

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

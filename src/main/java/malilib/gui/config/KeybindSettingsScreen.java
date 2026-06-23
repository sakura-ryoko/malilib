package malilib.gui.config;

import java.util.List;

import malilib.config.option.BaseConfigOption;
import malilib.config.option.BooleanConfig;
import malilib.config.option.IntegerConfig;
import malilib.config.option.OptionListConfig;
import malilib.gui.BaseScreen;
import malilib.gui.widget.IntegerEditWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.BooleanConfigButton;
import malilib.gui.widget.button.OptionListConfigButton;
import malilib.input.KeyBind;
import malilib.input.KeyBindSettingsConfigs;
import malilib.util.StringUtils;

public class KeybindSettingsScreen extends BaseScreen
{
    protected final KeyBind keybind;
    protected final String keybindName;
    protected final KeyBindSettingsConfigs configs;
    protected final List<BaseConfigOption<?>> configList;
    protected int labelWidth;
    protected int configWidth;

    public KeybindSettingsScreen(KeyBind keybind, String name)
    {
        this.keybind = keybind;
        this.keybindName = name;

        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.configs = new KeyBindSettingsConfigs(keybind, this::initScreen);
        this.configList = this.configs.getConfigList();
        this.labelWidth = StringUtils.getMaxStringRenderWidthOfObjects(this.configList, BaseConfigOption::getPrettyName);
        this.configWidth = 120;
        this.titleY = 12;

        this.addPreScreenCloseListener(this::saveSettings);
        this.setTitle("malilib.title.screen.keybind_settings.advanced", this.keybindName);

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.titleText.renderWidth + 20);
        int totalHeight = this.configList.size() * 18 + 40;

        this.setScreenWidthAndHeight(totalWidth, totalHeight);
        this.centerOnScreen();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        // TODO fix this stuff when there is a proper element placer/helper available
        this.clearWidgets();
        this.addWidget(this.closeButton);

        int x = this.x + 10;
        int y = this.y + 34;

        for (BaseConfigOption<?> config : this.configList)
        {
            this.createLabelAndConfigWidget(x, y, this.labelWidth, this.configWidth, config);
            y += 18;
        }
    }

    protected void createLabelAndConfigWidget(int x, int y, int labelWidth, int configWidth, BaseConfigOption<?> config)
    {
        int color = config.isModified() ? 0xFFFFFF55 : 0xFFAAAAAA;
        LabelWidget label = new LabelWidget(color).setLines(config.getPrettyName());
        label.setSize(labelWidth + 4, 16);
        label.setPosition(x, y);
        config.getComment().ifPresent(label::addHoverStrings);
        label.getPadding().setTop(3);
        this.addWidget(label);
        x += labelWidth + 10;

        if (config instanceof BooleanConfig)
        {
            BooleanConfigButton btn = new BooleanConfigButton(-1, 16, (BooleanConfig) config);
            btn.setPosition(x, y);
            this.addWidget(btn);
        }
        else if (config instanceof OptionListConfig)
        {
            OptionListConfigButton btn = new OptionListConfigButton(configWidth, 16, (OptionListConfig<?>) config);
            btn.setPosition(x, y);
            this.addWidget(btn);
        }
        else if (config instanceof IntegerConfig)
        {
            IntegerConfig intConfig = (IntegerConfig) config;
            IntegerEditWidget widget = new IntegerEditWidget(80, 16, intConfig);
            widget.setAddSlider(true);
            widget.setPosition(x, y);
            this.addWidget(widget);
        }
    }

    protected void saveSettings()
    {
        this.keybind.setSettings(this.configs.getCurrentSettings());
    }
}

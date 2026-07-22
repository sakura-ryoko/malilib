package malilib.gui.config.indicator;

import java.util.Collection;

import malilib.MaLiLibReference;
import malilib.config.value.OptionConfigValue;
import malilib.config.value.ScreenLocation;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.edit.EdgeIntEditScreen;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ColorIndicatorWidget;
import malilib.gui.widget.DoubleEditWidget;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.IntegerEditWidget;
import malilib.gui.widget.KeybindSettingsWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.button.OnOffButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.ConfigStatusIndicatorEntryWidget;
import malilib.input.KeyBind;
import malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import malilib.registry.Registry;
import malilib.render.text.MultiLineTextRenderSettings;
import malilib.util.data.ConfigOnTab;

public class ConfigStatusIndicatorGroupEditScreen extends BaseListScreen<DataListWidget<BaseConfigStatusIndicatorWidget<?>>>
{
    protected final ConfigStatusIndicatorContainerWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget backgroundLabelWidget;
    protected final LabelWidget oddBackgroundLabelWidget;
    protected final LabelWidget priorityLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget textScaleLabelWidget;
    protected final LabelWidget toggleKeyLabelWidget;
    protected final GenericButton groupEnabledToggleButton;
    protected final GenericButton backgroundEnabledToggleButton;
    protected final GenericButton oddEvenBackgroundToggleButton;
    protected final GenericButton renderNameToggleButton;
    protected final GenericButton marginEditButton;
    protected final GenericButton paddingEditButton;
    protected final GenericButton addConfigsButton;
    protected final ColorIndicatorWidget backgroundColorWidget;
    protected final ColorIndicatorWidget oddBackgroundColorWidget;
    protected final IntegerEditWidget priorityEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final DoubleEditWidget textScaleEditWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;

    public ConfigStatusIndicatorGroupEditScreen(ConfigStatusIndicatorContainerWidget widget)
    {
        super(10, 120, 20, 128);

        this.widget = widget;
        this.useTitleHierarchy = false;
        this.setTitle("malilib.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);

        this.locationDropdownWidget = new DropDownListWidget<>(16, 10, ScreenLocation.VALUES,
                                                               OptionConfigValue::getDisplayName);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget("malilib.label.misc.name.colon");
        this.priorityLabelWidget = new LabelWidget("malilib.label.misc.sort_index");
        this.lineHeightLabelWidget = new LabelWidget("malilib.label.misc.line_height");
        this.backgroundLabelWidget = new LabelWidget("malilib.label.misc.background");
        this.textScaleLabelWidget = new LabelWidget("malilib.label.misc.text_scale");
        this.toggleKeyLabelWidget = new LabelWidget("malilib.label.config_status_indicator.toggle_hotkey");
        this.oddBackgroundLabelWidget = new LabelWidget("malilib.label.config_status_indicator.background_odd");
        this.oddBackgroundLabelWidget.translateAndAddHoverString("malilib.hover.config_status_indicator.background_odd");

        this.nameTextField = new BaseTextFieldWidget(160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.priorityEditWidget = new IntegerEditWidget(72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.lineHeightEditWidget = new IntegerEditWidget(72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);
        this.textScaleEditWidget = new DoubleEditWidget(72, 16, widget.getScale(), 0.25, 20, widget::setScale);
        this.textScaleEditWidget.setBaseScrollAdjustAmount(0.5);

        this.addConfigsButton = GenericButton.create(16, "malilib.button.csi_edit.add_configs", this::openAddConfigsScreen);
        this.addConfigsButton.translateAndAddHoverString("malilib.hover.button.csi_edit.add_configs_to_widget");

        this.marginEditButton = GenericButton.create(16, "malilib.button.misc.margin", this::openMarginEditScreen);
        this.marginEditButton.setHoverStringProvider("tooltip", this.widget.getMargin()::getHoverTooltip);

        this.paddingEditButton = GenericButton.create(16, "malilib.button.misc.padding", this::openPaddingEditScreen);
        this.paddingEditButton.setHoverStringProvider("tooltip", this.widget.getPadding()::getHoverTooltip);

        final MultiLineTextRenderSettings textSettings = widget.getTextSettings();
        this.groupEnabledToggleButton = OnOffButton.simpleSlider(16, widget::isEnabled, widget::toggleEnabled);
        this.backgroundEnabledToggleButton = OnOffButton.simpleSlider(16, textSettings::getBackgroundEnabled, textSettings::toggleUseBackground);
        this.oddEvenBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getOddEvenBackgroundEnabled, textSettings::toggleUseOddEvenBackground);
        this.oddEvenBackgroundToggleButton.translateAndAddHoverString("malilib.hover.config_status_indicator.background_odd");
        this.renderNameToggleButton = OnOffButton.simpleSlider(16, widget::getRenderName, widget::toggleRenderName);

        this.backgroundColorWidget = new ColorIndicatorWidget(16, 16, textSettings::getBackgroundColor, textSettings::setBackgroundColor);
        this.oddBackgroundColorWidget = new ColorIndicatorWidget(16, 16, textSettings::getOddRowBackgroundColor, textSettings::setOddRowBackgroundColor);

        KeyBind keyBind = widget.getHotkey().getKeyBind();
        this.keybindButton = new KeyBindConfigButton(120, 20, keyBind);
        this.settingsWidget = new KeybindSettingsWidget(keyBind, widget.getHotkey().getDisplayName());

        this.addPostInitListener(this::updateHoverStrings);
        this.addPreScreenCloseListener(this::applyChanges);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.groupEnabledToggleButton);
        this.addWidget(this.marginEditButton);
        this.addWidget(this.paddingEditButton);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.renderNameToggleButton);

        this.addWidget(this.textScaleLabelWidget);
        this.addWidget(this.textScaleEditWidget);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.priorityLabelWidget);
        this.addWidget(this.priorityEditWidget);

        this.addWidget(this.backgroundLabelWidget);
        this.addWidget(this.backgroundColorWidget);
        this.addWidget(this.backgroundEnabledToggleButton);

        this.addWidget(this.oddBackgroundLabelWidget);
        this.addWidget(this.oddBackgroundColorWidget);
        this.addWidget(this.oddEvenBackgroundToggleButton);

        this.addWidget(this.toggleKeyLabelWidget);
        this.addWidget(this.keybindButton);
        this.addWidget(this.settingsWidget);

        this.addWidget(this.addConfigsButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;
        int tmpX;

        this.locationDropdownWidget.setPosition(x, y);
        this.groupEnabledToggleButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);

        this.marginEditButton.setPosition(this.groupEnabledToggleButton.getRight() + 6, y);
        this.paddingEditButton.setPosition(this.marginEditButton.getRight() + 6, y);

        y += 20;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
        this.renderNameToggleButton.setPosition(this.nameTextField.getRight() + 6, y);

        y += 19;
        this.textScaleLabelWidget.setPosition(x, y + 4);
        this.lineHeightLabelWidget.setPosition(x, y + 23);
        this.priorityLabelWidget.setPosition(x, y + 42);

        tmpX = Math.max(this.textScaleLabelWidget.getRight(), this.lineHeightLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.priorityLabelWidget.getRight()) + 6;
        this.textScaleEditWidget.setPosition(tmpX, y);
        this.lineHeightEditWidget.setPosition(tmpX, y + 19);
        this.priorityEditWidget.setPosition(tmpX, y + 38);

        tmpX = this.textScaleEditWidget.getRight() + 6;
        this.backgroundLabelWidget.setPosition(tmpX, y + 4);
        this.oddBackgroundLabelWidget.setPosition(tmpX, y + 23);
        this.toggleKeyLabelWidget.setPosition(tmpX, y + 42);

        tmpX = Math.max(this.backgroundLabelWidget.getRight(), this.oddBackgroundLabelWidget.getRight()) + 6;
        this.backgroundColorWidget.setPosition(tmpX, y);
        this.oddBackgroundColorWidget.setPosition(tmpX, y + 19);

        this.keybindButton.setPosition(this.toggleKeyLabelWidget.getRight() + 6, y + 36);
        this.settingsWidget.setPosition(this.keybindButton.getRight() + 2, y + 36);

        tmpX += 22;
        this.backgroundEnabledToggleButton.setPosition(tmpX, y);
        this.oddEvenBackgroundToggleButton.setPosition(tmpX, y + 19);

        tmpX = this.x + this.screenWidth - this.addConfigsButton.getWidth() - 9;
        this.addConfigsButton.setPosition(tmpX, y + 38);
    }

    protected void applyChanges()
    {
        this.widget.setStatusIndicatorWidgets(this.getListWidget().getNonFilteredDataList());
        Registry.HOTKEY_MANAGER.updateUsedKeys();
    }

    protected void addConfigsToWidget(Collection<ConfigOnTab> configs)
    {
        this.widget.addWidgetsForConfigs(configs);
        this.getListWidget().fetchCurrentEntriesFromSupplier();
    }

    protected void updateHoverStrings()
    {
        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();

        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();
    }

    protected void changeWidgetLocation(ScreenLocation location)
    {
        Registry.INFO_OVERLAY.getOrCreateInfoArea(this.widget.getScreenLocation()).removeWidget(this.widget);
        // This also sets the location in the widget
        Registry.INFO_OVERLAY.getOrCreateInfoArea(location).addWidget(this.widget);
    }

    protected void openAddConfigsScreen()
    {
        BaseScreen.openScreenWithParent(new ConfigStatusIndicatorGroupAddConfigsScreen(this.widget, this::addConfigsToWidget));
    }

    protected void openMarginEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getMargin(), false,
                                                         "malilib.title.screen.edit_margin",
                                                         "malilib.label.misc.margin");
        BaseScreen.openPopupScreenWithParent(screen);
    }

    protected void openPaddingEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getPadding(), false,
                                                         "malilib.title.screen.edit_padding",
                                                         "malilib.label.misc.padding");
        BaseScreen.openPopupScreenWithParent(screen);
    }

    @Override
    protected DataListWidget<BaseConfigStatusIndicatorWidget<?>> createListWidget()
    {
        DataListWidget<BaseConfigStatusIndicatorWidget<?>> listWidget
                = new DataListWidget<>(this.widget::getStatusIndicatorWidgets, false);

        listWidget.setListEntryWidgetFixedHeight(16);
        listWidget.setDataListEntryWidgetFactory((data, constructData) ->
                                         new ConfigStatusIndicatorEntryWidget(data, constructData, this.widget));

        return listWidget;
    }
}

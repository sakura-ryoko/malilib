package malilib.gui.edit.overlay;

import malilib.config.value.OptionConfigValue;
import malilib.config.value.ScreenLocation;
import malilib.gui.BaseScreen;
import malilib.gui.edit.EdgeIntEditScreen;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ColorIndicatorWidget;
import malilib.gui.widget.DoubleEditWidget;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.IntegerEditWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.OnOffButton;
import malilib.overlay.widget.StringListRendererWidget;
import malilib.registry.Registry;
import malilib.render.text.MultiLineTextRenderSettings;

public class StringListRendererWidgetEditScreen extends BaseScreen
{
    protected final StringListRendererWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget textColorLabelWidget;
    protected final LabelWidget backgroundLabelWidget;
    protected final LabelWidget oddBackgroundLabelWidget;
    protected final LabelWidget evenWidthBackgroundLabelWidget;
    protected final LabelWidget sortIndexLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget textScaleLabelWidget;
    protected final LabelWidget textShadowLabelWidget;
    protected final GenericButton enabledToggleButton;
    protected final GenericButton backgroundEnabledToggleButton;
    protected final GenericButton oddEvenBackgroundToggleButton;
    protected final GenericButton evenWidthBackgroundToggleButton;
    protected final GenericButton renderNameToggleButton;
    protected final GenericButton marginEditButton;
    protected final GenericButton paddingEditButton;
    protected final GenericButton textShadowToggleButton;
    protected final ColorIndicatorWidget textColorWidget;
    protected final ColorIndicatorWidget backgroundColorWidget;
    protected final ColorIndicatorWidget oddBackgroundColorWidget;
    protected final IntegerEditWidget sortIndexEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final DoubleEditWidget textScaleEditWidget;
    protected final BaseTextFieldWidget nameTextField;

    public StringListRendererWidgetEditScreen(StringListRendererWidget widget)
    {
        this.widget = widget;
        this.useTitleHierarchy = false;

        this.setTitle("malilib.title.screen.string_list_renderer_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(16, 10, ScreenLocation.VALUES,
                                                               OptionConfigValue::getDisplayName);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget("malilib.label.misc.name.colon");
        this.sortIndexLabelWidget = new LabelWidget("malilib.label.misc.sort_index");
        this.lineHeightLabelWidget = new LabelWidget("malilib.label.misc.line_height");
        this.textColorLabelWidget = new LabelWidget("malilib.label.misc.text_color");
        this.textScaleLabelWidget = new LabelWidget("malilib.label.misc.text_scale");
        this.textShadowLabelWidget = new LabelWidget("malilib.label.misc.text_shadow");
        this.backgroundLabelWidget = new LabelWidget("malilib.label.misc.background");
        this.evenWidthBackgroundLabelWidget = new LabelWidget("malilib.label.misc.even_width_background.short");
        this.oddBackgroundLabelWidget = new LabelWidget("malilib.label.config_status_indicator.background_odd");
        this.oddBackgroundLabelWidget.translateAndAddHoverString("malilib.hover.config_status_indicator.background_odd");

        this.nameTextField = new BaseTextFieldWidget(160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.sortIndexEditWidget = new IntegerEditWidget(72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.lineHeightEditWidget = new IntegerEditWidget(72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);
        this.textScaleEditWidget = new DoubleEditWidget(72, 16, widget.getScale(), 0.25, 20, widget::setScale);
        this.textScaleEditWidget.setBaseScrollAdjustAmount(0.5);

        this.marginEditButton = GenericButton.create(16, "malilib.label.misc.margin", this::openMarginEditScreen);
        this.marginEditButton.setHoverStringProvider("tooltip", this.widget.getMargin()::getHoverTooltip);

        this.paddingEditButton = GenericButton.create(16, "malilib.label.misc.padding", this::openPaddingEditScreen);
        this.paddingEditButton.setHoverStringProvider("tooltip", this.widget.getPadding()::getHoverTooltip);

        this.enabledToggleButton = OnOffButton.simpleSlider(16, widget::isEnabled, widget::toggleEnabled);

        final MultiLineTextRenderSettings textSettings = widget.getTextSettings();

        this.backgroundEnabledToggleButton = OnOffButton.simpleSlider(16, textSettings::getBackgroundEnabled, textSettings::toggleUseBackground);
        this.oddEvenBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getOddEvenBackgroundEnabled, textSettings::toggleUseOddEvenBackground);
        this.oddEvenBackgroundToggleButton.translateAndAddHoverString("malilib.hover.config_status_indicator.background_odd");

        this.evenWidthBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getEvenWidthBackgroundEnabled, textSettings::toggleUseEvenWidthBackground);
        this.textShadowToggleButton = OnOffButton.simpleSlider(16, textSettings::getTextShadowEnabled, textSettings::toggleUseTextShadow);
        this.renderNameToggleButton = OnOffButton.simpleSlider(16, widget::getRenderName, widget::toggleRenderName);

        this.textColorWidget = new ColorIndicatorWidget(16, 16, textSettings::getTextColor, textSettings::setTextColor);
        this.backgroundColorWidget = new ColorIndicatorWidget(16, 16, textSettings::getBackgroundColor, textSettings::setBackgroundColor);
        this.oddBackgroundColorWidget = new ColorIndicatorWidget(16, 16, textSettings::getOddRowBackgroundColor, textSettings::setOddRowBackgroundColor);

        // Unconditionally save here, since we don't track the individual info container widget changes
        this.addPreScreenCloseListener(Registry.INFO_WIDGET_MANAGER::saveToFile);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.enabledToggleButton);
        this.addWidget(this.marginEditButton);
        this.addWidget(this.paddingEditButton);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.renderNameToggleButton);

        this.addWidget(this.textScaleLabelWidget);
        this.addWidget(this.textScaleEditWidget);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.sortIndexLabelWidget);
        this.addWidget(this.sortIndexEditWidget);

        this.addWidget(this.textShadowLabelWidget);
        this.addWidget(this.textShadowToggleButton);

        this.addWidget(this.textColorLabelWidget);
        this.addWidget(this.textColorWidget);

        this.addWidget(this.backgroundLabelWidget);
        this.addWidget(this.backgroundColorWidget);
        this.addWidget(this.backgroundEnabledToggleButton);

        this.addWidget(this.oddBackgroundLabelWidget);
        this.addWidget(this.oddBackgroundColorWidget);
        this.addWidget(this.oddEvenBackgroundToggleButton);

        this.addWidget(this.evenWidthBackgroundLabelWidget);
        this.addWidget(this.evenWidthBackgroundToggleButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;
        int tmpX;

        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();

        this.locationDropdownWidget.setPosition(x, y);
        this.enabledToggleButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);

        this.marginEditButton.setPosition(this.enabledToggleButton.getRight() + 6, y);
        this.paddingEditButton.setPosition(this.marginEditButton.getRight() + 6, y);

        y += 20;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
        this.renderNameToggleButton.setPosition(this.nameTextField.getRight() + 6, y);

        y += 20;
        this.textScaleLabelWidget.setPosition(x, y + 4);
        this.lineHeightLabelWidget.setPosition(x, y + 24);
        this.sortIndexLabelWidget.setPosition(x, y + 44);
        this.textShadowLabelWidget.setPosition(x, y + 64);

        tmpX = Math.max(this.textScaleLabelWidget.getRight(), this.lineHeightLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.sortIndexLabelWidget.getRight()) + 6;
        this.textScaleEditWidget.setPosition(tmpX, y);
        this.lineHeightEditWidget.setPosition(tmpX, y + 20);
        this.sortIndexEditWidget.setPosition(tmpX, y + 40);

        tmpX = this.textShadowLabelWidget.getRight() + 6;
        this.textShadowToggleButton.setPosition(tmpX, y + 60);

        tmpX = this.sortIndexEditWidget.getRight() + 6;
        this.textColorLabelWidget.setPosition(tmpX, y + 4);
        this.backgroundLabelWidget.setPosition(tmpX, y + 24);
        this.oddBackgroundLabelWidget.setPosition(tmpX, y + 44);
        this.evenWidthBackgroundLabelWidget.setPosition(tmpX, y + 64);

        int tmpX1 = Math.max(this.textColorLabelWidget.getRight(), this.backgroundLabelWidget.getRight());
        int tmpX2 = Math.max(this.oddBackgroundLabelWidget.getRight(), this.textShadowLabelWidget.getRight());
        tmpX = Math.max(tmpX1, tmpX2);
        tmpX = Math.max(tmpX, this.evenWidthBackgroundLabelWidget.getRight()) + 6;
        this.textColorWidget.setPosition(tmpX, y);
        this.backgroundColorWidget.setPosition(tmpX, y + 20);
        this.oddBackgroundColorWidget.setPosition(tmpX, y + 40);

        tmpX += 22;
        this.backgroundEnabledToggleButton.setPosition(tmpX, y + 20);
        this.oddEvenBackgroundToggleButton.setPosition(tmpX, y + 40);

        tmpX = Math.max(tmpX, this.evenWidthBackgroundLabelWidget.getRight() + 6);
        this.evenWidthBackgroundToggleButton.setPosition(tmpX, y + 60);
    }

    public void setupAsPopup()
    {
        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        int width = this.evenWidthBackgroundToggleButton.getRight() - this.x + 20;
        int height = this.textShadowToggleButton.getBottom() - this.y + 20;
        this.setScreenWidthAndHeight(width, height);
        this.centerOnScreen();
    }

    protected void changeWidgetLocation(ScreenLocation location)
    {
        Registry.INFO_OVERLAY.getOrCreateInfoArea(this.widget.getScreenLocation()).removeWidget(this.widget);
        // This also sets the location in the widget
        Registry.INFO_OVERLAY.getOrCreateInfoArea(location).addWidget(this.widget);
    }

    protected void openMarginEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getMargin(), false,
                                                         "malilib.title.screen.edit_margin", "malilib.label.misc.margin");
        BaseScreen.openPopupScreenWithParent(screen);
    }

    protected void openPaddingEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getPadding(), false,
                                                         "malilib.title.screen.edit_padding", "malilib.label.misc.padding");
        BaseScreen.openPopupScreenWithParent(screen);
    }
}

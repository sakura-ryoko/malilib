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
import malilib.overlay.widget.ToastRendererWidget;
import malilib.registry.Registry;
import malilib.render.text.TextRenderSettings;

public class ToastRendererWidgetEditScreen extends BaseScreen
{
    protected final ToastRendererWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget defaultLifeTimeLabelWidget;
    protected final LabelWidget defaultFadeInTimeLabelWidget;
    protected final LabelWidget defaultFadeOutTimeLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget maxWidthLabelWidget;
    protected final LabelWidget messageGapLabelWidget;
    protected final LabelWidget renderAboveScreenLabelWidget;
    protected final LabelWidget sortIndexLabelWidget;
    protected final LabelWidget textColorLabelWidget;
    protected final LabelWidget zLevelLabelWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final GenericButton marginEditButton;
    protected final GenericButton paddingEditButton;
    protected final GenericButton renderAboveScreenButton;
    protected final ColorIndicatorWidget textColorEditWidget;
    protected final IntegerEditWidget defaultLifeTimeEditWidget;
    protected final IntegerEditWidget defaultFadeInTimeEditWidget;
    protected final IntegerEditWidget defaultFadeOutTimeEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final IntegerEditWidget maxWidthEditWidget;
    protected final IntegerEditWidget messageGapEditWidget;
    protected final IntegerEditWidget sortIndexEditWidget;
    protected final DoubleEditWidget zLevelEditWidget;

    public ToastRendererWidgetEditScreen(ToastRendererWidget widget)
    {
        this.widget = widget;
        this.useTitleHierarchy = false;

        this.setTitle("malilib.title.screen.toast_renderer_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(16, 10, ScreenLocation.VALUES,
                                                               OptionConfigValue::getDisplayName);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget("malilib.label.misc.name.colon");
        this.defaultLifeTimeLabelWidget = new LabelWidget("malilib.label.misc.default_duration");
        this.defaultFadeInTimeLabelWidget = new LabelWidget("malilib.label.misc.default_fade_in_time");
        this.defaultFadeOutTimeLabelWidget = new LabelWidget("malilib.label.misc.default_fade_out_time");
        this.lineHeightLabelWidget = new LabelWidget("malilib.label.misc.line_height");
        this.maxWidthLabelWidget = new LabelWidget("malilib.label.misc.max_width");
        this.messageGapLabelWidget = new LabelWidget("malilib.label.misc.message_gap");
        this.renderAboveScreenLabelWidget = new LabelWidget("malilib.label.edit_screen.render_above_screen");
        this.sortIndexLabelWidget = new LabelWidget("malilib.label.misc.sort_index");
        this.textColorLabelWidget = new LabelWidget("malilib.label.edit_screen.default_text_color");
        this.zLevelLabelWidget = new LabelWidget("malilib.label.misc.z_level");
        //this.textScaleLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.misc.text_scale");

        this.nameTextField = new BaseTextFieldWidget(160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.defaultLifeTimeEditWidget = new IntegerEditWidget(72, 16, widget.getDefaultLifeTime(), 5, 120000, widget::setDefaultLifeTime);
        this.defaultFadeInTimeEditWidget = new IntegerEditWidget(72, 16, widget.getDefaultFadeInTime(), 0, 10000, widget::setDefaultFadeInTime);
        this.defaultFadeOutTimeEditWidget = new IntegerEditWidget(72, 16, widget.getDefaultFadeOutTime(), 0, 10000, widget::setDefaultFadeOutTime);
        this.lineHeightEditWidget = new IntegerEditWidget(72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);
        this.maxWidthEditWidget = new IntegerEditWidget(72, 16, widget.getMaxWidth(), 32, 1920, widget::setMaxWidth);
        this.messageGapEditWidget = new IntegerEditWidget(72, 16, widget.getMessageGap(), 0, 100, widget::setMessageGap);
        this.sortIndexEditWidget = new IntegerEditWidget(72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.zLevelEditWidget = new DoubleEditWidget(72, 16, widget.getZ(), -1000, 1000, (v) -> this.widget.setZ((float) v));
        //this.textScaleEditWidget = new DoubleEditWidget(0, 0, 72, 16, widget.getScale(), 0.25, 20, widget::setScale);
        //this.textScaleEditWidget.setBaseScrollAdjustAmount(0.5);

        this.marginEditButton = GenericButton.create(16, "malilib.label.misc.margin", this::openMarginEditScreen);
        this.marginEditButton.setHoverStringProvider("tooltip", this.widget.getMargin()::getHoverTooltip);

        this.paddingEditButton = GenericButton.create(16, "malilib.label.misc.padding", this::openPaddingEditScreen);
        this.paddingEditButton.setHoverStringProvider("tooltip", this.widget.getPadding()::getHoverTooltip);

        this.renderAboveScreenButton = OnOffButton.simpleSlider(16, this.widget::getRenderAboveScreen, this::toggleRenderAboveScreen);

        final TextRenderSettings textSettings = widget.getTextSettings();
        this.textColorEditWidget = new ColorIndicatorWidget(16, 16, textSettings::getTextColor, textSettings::setTextColor);

        // Unconditionally save here, since we don't track the individual info container widget changes
        this.addPreScreenCloseListener(Registry.INFO_WIDGET_MANAGER::saveToFile);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.locationDropdownWidget);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);

        this.addWidget(this.marginEditButton);
        this.addWidget(this.paddingEditButton);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.renderAboveScreenLabelWidget);
        this.addWidget(this.renderAboveScreenButton);

        this.addWidget(this.messageGapLabelWidget);
        this.addWidget(this.messageGapEditWidget);

        this.addWidget(this.sortIndexLabelWidget);
        this.addWidget(this.sortIndexEditWidget);

        this.addWidget(this.maxWidthLabelWidget);
        this.addWidget(this.maxWidthEditWidget);

        this.addWidget(this.zLevelLabelWidget);
        this.addWidget(this.zLevelEditWidget);

        this.addWidget(this.defaultLifeTimeLabelWidget);
        this.addWidget(this.defaultLifeTimeEditWidget);

        this.addWidget(this.defaultFadeInTimeLabelWidget);
        this.addWidget(this.defaultFadeInTimeEditWidget);

        this.addWidget(this.defaultFadeOutTimeLabelWidget);
        this.addWidget(this.defaultFadeOutTimeEditWidget);

        this.addWidget(this.textColorLabelWidget);
        this.addWidget(this.textColorEditWidget);
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
        this.marginEditButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);
        this.paddingEditButton.setPosition(this.marginEditButton.getRight() + 6, y);

        y += 20;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);

        y += 20;
        this.lineHeightLabelWidget.setPosition(x, y + 4);
        this.messageGapLabelWidget.setPosition(x, y + 24);
        this.sortIndexLabelWidget.setPosition(x, y + 44);
        this.maxWidthLabelWidget.setPosition(x, y + 64);
        this.zLevelLabelWidget.setPosition(x, y + 84);

        tmpX = Math.max(this.lineHeightLabelWidget.getRight(), this.sortIndexLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.maxWidthLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.messageGapLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.zLevelLabelWidget.getRight()) + 6;
        this.lineHeightEditWidget.setPosition(tmpX, y);
        this.messageGapEditWidget.setPosition(tmpX, y + 20);
        this.sortIndexEditWidget.setPosition(tmpX, y + 40);
        this.maxWidthEditWidget.setPosition(tmpX, y + 60);
        this.zLevelEditWidget.setPosition(tmpX, y + 80);

        tmpX += 80;
        this.defaultLifeTimeLabelWidget.setPosition(tmpX, y + 4);
        this.defaultFadeInTimeLabelWidget.setPosition(tmpX, y + 24);
        this.defaultFadeOutTimeLabelWidget.setPosition(tmpX, y + 44);
        this.textColorLabelWidget.setPosition(tmpX, y + 64);
        this.renderAboveScreenLabelWidget.setPosition(tmpX, y + 84);

        tmpX = Math.max(this.defaultLifeTimeLabelWidget.getRight(), this.defaultFadeInTimeLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.defaultFadeOutTimeLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.textColorLabelWidget.getRight()) + 6;
        this.defaultLifeTimeEditWidget.setPosition(tmpX, y);
        this.defaultFadeInTimeEditWidget.setPosition(tmpX, y + 20);
        this.defaultFadeOutTimeEditWidget.setPosition(tmpX, y + 40);
        this.textColorEditWidget.setPosition(tmpX, y + 60);
        this.renderAboveScreenButton.setPosition(this.renderAboveScreenLabelWidget.getRight() + 6, y + 80);
    }

    protected void toggleRenderAboveScreen()
    {
        this.widget.setRenderAboveScreen(! this.widget.getRenderAboveScreen());
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

package malilib.gui.action;

import java.util.List;

import malilib.gui.BaseScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.icon.IconRegistry;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.CheckBoxWidget;
import malilib.gui.widget.ColorIndicatorAndEditWidget;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.FloatEditWidget;
import malilib.gui.widget.IconWidget;
import malilib.gui.widget.IntegerEditWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.registry.Registry;
import malilib.render.text.TextRenderSettings;
import malilib.util.data.EdgeInt;

public class EditActionExecutionWidgetScreen extends BaseScreen
{
    protected final List<BaseActionExecutionWidget> widgets;
    protected final BaseActionExecutionWidget firstWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget nameNormalColorLabelWidget;
    protected final LabelWidget nameHoveredColorLabelWidget;
    protected final LabelWidget nameXOffsetLabelWidget;
    protected final LabelWidget nameYOffsetLabelWidget;
    protected final LabelWidget iconLabelWidget;
    protected final LabelWidget iconXOffsetLabelWidget;
    protected final LabelWidget iconYOffsetLabelWidget;
    protected final LabelWidget iconScaleXLabelWidget;
    protected final LabelWidget iconScaleYLabelWidget;
    protected final LabelWidget hoveredBgColorLabelWidget;
    protected final LabelWidget normalBgColorLabelWidget;
    protected final LabelWidget hoveredBorderColorLabelWidget;
    protected final LabelWidget normalBorderColorLabelWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final DropDownListWidget<Icon> iconDropDownWidget;
    protected final IntegerEditWidget nameXOffsetEditWidget;
    protected final IntegerEditWidget nameYOffsetEditWidget;
    protected final IntegerEditWidget iconXOffsetEditWidget;
    protected final IntegerEditWidget iconYOffsetEditWidget;
    protected final FloatEditWidget iconScaleXEditWidget;
    protected final FloatEditWidget iconScaleYEditWidget;
    protected final CheckBoxWidget nameCenteredOnXCheckbox;
    protected final CheckBoxWidget nameCenteredOnYCheckbox;
    protected final CheckBoxWidget iconCenteredOnXCheckbox;
    protected final CheckBoxWidget iconCenteredOnYCheckbox;
    protected final ColorIndicatorAndEditWidget nameNormalColorEditWidget;
    protected final ColorIndicatorAndEditWidget nameHoveredColorEditWidget;
    protected final ColorIndicatorAndEditWidget hoveredBackgroundColorEditWidget;
    protected final ColorIndicatorAndEditWidget normalBackgroundColorEditWidget;
    protected final ColorIndicatorAndEditWidget hoveredBorderColorEditWidget;
    protected final ColorIndicatorAndEditWidget normalBorderColorEditWidget;
    protected final GenericButton cancelButton;
    protected final GenericButton removeIconButton;
    protected boolean shouldApplyValues = true;

    public EditActionExecutionWidgetScreen(List<BaseActionExecutionWidget> widgets)
    {
        if (widgets.size() > 1)
        {
            this.setTitle("malilib.title.screen.edit_action_execution_widget.multiple", widgets.size());
            this.setScreenWidthAndHeight(240, 164);
        }
        else
        {
            this.setTitle("malilib.title.screen.edit_action_execution_widget");
            this.setScreenWidthAndHeight(240, 324);
        }

        this.useTitleHierarchy = false;
        this.widgets = widgets;
        BaseActionExecutionWidget widget = widgets.get(0);
        this.firstWidget = widget;

        this.nameLabelWidget = new LabelWidget("malilib.label.misc.name_optional");
        this.iconLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_optional");

        this.nameTextField = new BaseTextFieldWidget(140, 16, widget.getName());
        this.nameTextField.setListener(this.firstWidget::setName);

        this.iconDropDownWidget = new DropDownListWidget<>(16, 10,
                                                           Registry.ICON.getAllIcons(),
                                                           IconRegistry::getKeyForIcon, IconWidget::new);
        this.iconDropDownWidget.setAutomaticWidth(false);
        this.iconDropDownWidget.setWidth(120);
        this.iconDropDownWidget.setSelectedEntry(widget.getIcon());
        this.iconDropDownWidget.setSelectionListener(this.firstWidget::setIcon);

        this.nameXOffsetLabelWidget = new LabelWidget("malilib.label.misc.name_x_offset");
        this.nameYOffsetLabelWidget = new LabelWidget("malilib.label.misc.name_y_offset");

        this.iconXOffsetLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_x_offset");
        this.iconYOffsetLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_y_offset");

        this.iconScaleXLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_scale_x");
        this.iconScaleYLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_scale_y");

        this.nameNormalColorLabelWidget = new LabelWidget("malilib.label.misc.name_color_normal");
        this.nameHoveredColorLabelWidget = new LabelWidget("malilib.label.misc.name_color_hovered");

        this.normalBgColorLabelWidget = new LabelWidget("malilib.label.misc.background");
        this.hoveredBgColorLabelWidget = new LabelWidget("malilib.label.misc.hovered_background.short");

        this.normalBorderColorLabelWidget = new LabelWidget("malilib.label.misc.border_color");
        this.hoveredBorderColorLabelWidget = new LabelWidget("malilib.label.misc.hovered_border");

        this.cancelButton = GenericButton.create(16, "malilib.button.misc.cancel", this::cancel);

        this.removeIconButton = GenericButton.create(DefaultIcons.LIST_REMOVE_MINUS_13, this::removeIcon);
        this.removeIconButton.translateAndAddHoverString("malilib.button.misc.remove_icon");

        this.nameXOffsetEditWidget = new IntegerEditWidget(72, 16, widget.getTextOffset().getXOffset(), -512, 512, widget.getTextOffset()::setXOffset);
        this.nameYOffsetEditWidget = new IntegerEditWidget(72, 16, widget.getTextOffset().getYOffset(), -512, 512, widget.getTextOffset()::setYOffset);

        this.nameCenteredOnXCheckbox = new CheckBoxWidget("malilib.checkbox.center", widget.getTextOffset()::getCenterHorizontally, widget.getTextOffset()::setCenterHorizontally);
        this.nameCenteredOnYCheckbox = new CheckBoxWidget("malilib.checkbox.center", widget.getTextOffset()::getCenterVertically, widget.getTextOffset()::setCenterVertically);

        this.iconXOffsetEditWidget = new IntegerEditWidget(72, 16, widget.getIconOffset().getXOffset(), -512, 512, widget.getIconOffset()::setXOffset);
        this.iconYOffsetEditWidget = new IntegerEditWidget(72, 16, widget.getIconOffset().getYOffset(), -512, 512, widget.getIconOffset()::setYOffset);

        this.iconCenteredOnXCheckbox = new CheckBoxWidget("malilib.checkbox.center", widget.getIconOffset()::getCenterHorizontally, widget.getIconOffset()::setCenterHorizontally);
        this.iconCenteredOnYCheckbox = new CheckBoxWidget("malilib.checkbox.center", widget.getIconOffset()::getCenterVertically, widget.getIconOffset()::setCenterVertically);

        this.iconScaleXEditWidget = new FloatEditWidget(72, 16, widget.getIconScaleX(), 0, 100, widget::setIconScaleX);
        this.iconScaleYEditWidget = new FloatEditWidget(72, 16, widget.getIconScaleY(), 0, 100, widget::setIconScaleY);

        TextRenderSettings settings = widget.getTextSettings();
        this.nameNormalColorEditWidget = new ColorIndicatorAndEditWidget(90, 16, settings::getTextColor, settings::setTextColor);
        this.nameHoveredColorEditWidget = new ColorIndicatorAndEditWidget(90, 16, settings::getHoveredTextColor, settings::setHoveredTextColor);

        this.normalBackgroundColorEditWidget    = new ColorIndicatorAndEditWidget(90, 16, widget.getBackgroundRenderer().getNormalSettings()::getColor, widget.getBackgroundRenderer().getNormalSettings()::setColor);
        this.hoveredBackgroundColorEditWidget   = new ColorIndicatorAndEditWidget(90, 16, widget.getBackgroundRenderer().getHoverSettings()::getColor, widget.getBackgroundRenderer().getHoverSettings()::setColor);

        this.normalBorderColorEditWidget        = new ColorIndicatorAndEditWidget(90, 16, widget.getBorderRenderer().getNormalSettings().getColor());
        this.hoveredBorderColorEditWidget       = new ColorIndicatorAndEditWidget(90, 16, widget.getBorderRenderer().getHoverSettings().getColor());

        this.backgroundColor = 0xFF101010;

        this.addPreScreenCloseListener(this::applyValues);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        if (this.widgets.size() == 1)
        {
            this.addWidget(this.nameLabelWidget);
            this.addWidget(this.nameTextField);

            this.addWidget(this.iconLabelWidget);
            this.addWidget(this.iconDropDownWidget);
            this.addWidget(this.removeIconButton);

            this.addWidget(this.nameXOffsetLabelWidget);
            this.addWidget(this.nameXOffsetEditWidget);
            this.addWidget(this.nameCenteredOnXCheckbox);

            this.addWidget(this.nameYOffsetLabelWidget);
            this.addWidget(this.nameYOffsetEditWidget);
            this.addWidget(this.nameCenteredOnYCheckbox);

            this.addWidget(this.iconXOffsetLabelWidget);
            this.addWidget(this.iconXOffsetEditWidget);
            this.addWidget(this.iconCenteredOnXCheckbox);

            this.addWidget(this.iconYOffsetLabelWidget);
            this.addWidget(this.iconYOffsetEditWidget);
            this.addWidget(this.iconCenteredOnYCheckbox);

            this.addWidget(this.iconScaleXLabelWidget);
            this.addWidget(this.iconScaleYLabelWidget);

            this.addWidget(this.iconScaleXEditWidget);
            this.addWidget(this.iconScaleYEditWidget);
        }

        this.addWidget(this.nameNormalColorLabelWidget);
        this.addWidget(this.nameNormalColorEditWidget);

        this.addWidget(this.nameHoveredColorLabelWidget);
        this.addWidget(this.nameHoveredColorEditWidget);

        this.addWidget(this.normalBgColorLabelWidget);
        this.addWidget(this.normalBackgroundColorEditWidget);

        this.addWidget(this.hoveredBgColorLabelWidget);
        this.addWidget(this.hoveredBackgroundColorEditWidget);

        this.addWidget(this.normalBorderColorLabelWidget);
        this.addWidget(this.normalBorderColorEditWidget);

        this.addWidget(this.hoveredBorderColorLabelWidget);
        this.addWidget(this.hoveredBorderColorEditWidget);

        if (this.widgets.size() > 1)
        {
            this.addWidget(this.cancelButton);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        int x = this.x + 10;
        int y = this.y + 24;

        if (this.widgets.size() == 1)
        {
            this.nameLabelWidget.setPosition(x, y + 4);
            this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
            y += 20;

            this.iconLabelWidget.setPosition(x, y + 4);
            this.iconDropDownWidget.setPosition(this.iconLabelWidget.getRight() + 6, y);
            this.removeIconButton.setPosition(this.iconDropDownWidget.getRight() + 2, y + 1);
            y += 20;

            this.nameXOffsetLabelWidget.setPosition(x, y + 4);
            this.nameXOffsetEditWidget.setPosition(this.nameXOffsetLabelWidget.getRight() + 6, y);
            this.nameCenteredOnXCheckbox.setPosition(this.nameXOffsetEditWidget.getRight() + 6, y + 2);
            y += 20;

            this.nameYOffsetLabelWidget.setPosition(x, y + 4);
            this.nameYOffsetEditWidget.setPosition(this.nameYOffsetLabelWidget.getRight() + 6, y);
            this.nameCenteredOnYCheckbox.setPosition(this.nameYOffsetEditWidget.getRight() + 6, y + 2);
            y += 20;

            this.iconXOffsetLabelWidget.setPosition(x, y + 4);
            this.iconXOffsetEditWidget.setPosition(this.iconXOffsetLabelWidget.getRight() + 6, y);
            this.iconCenteredOnXCheckbox.setPosition(this.iconXOffsetEditWidget.getRight() + 6, y + 2);
            y += 20;

            this.iconYOffsetLabelWidget.setPosition(x, y + 4);
            this.iconYOffsetEditWidget.setPosition(this.iconYOffsetLabelWidget.getRight() + 6, y);
            this.iconCenteredOnYCheckbox.setPosition(this.iconYOffsetEditWidget.getRight() + 6, y + 2);
            y += 20;

            this.iconScaleXLabelWidget.setPosition(x, y + 4);
            this.iconScaleXEditWidget.setPosition(this.iconScaleXLabelWidget.getRight() + 6, y);
            y += 20;

            this.iconScaleYLabelWidget.setPosition(x, y + 4);
            this.iconScaleYEditWidget.setPosition(this.iconScaleYLabelWidget.getRight() + 6, y);
            y += 20;
        }

        this.nameNormalColorLabelWidget.setPosition(x, y + 4);
        this.nameNormalColorEditWidget.setY(y);
        y += 20;

        this.nameHoveredColorLabelWidget.setPosition(x, y + 4);
        this.nameHoveredColorEditWidget.setY(y);
        y += 20;

        this.normalBgColorLabelWidget.setPosition(x, y + 4);
        this.normalBackgroundColorEditWidget.setY(y);
        y += 20;

        this.hoveredBgColorLabelWidget.setPosition(x, y + 4);
        this.hoveredBackgroundColorEditWidget.setY(y);
        y += 20;

        this.normalBorderColorLabelWidget.setPosition(x, y + 4);
        this.normalBorderColorEditWidget.setY(y);
        y += 20;

        this.hoveredBorderColorLabelWidget.setPosition(x, y + 4);
        this.hoveredBorderColorEditWidget.setY(y);

        int x1 = Math.max(this.nameNormalColorLabelWidget.getRight(), this.nameHoveredColorLabelWidget.getRight());
        int x2 = Math.max(this.normalBgColorLabelWidget.getRight(), this.hoveredBgColorLabelWidget.getRight());
        int x3 = Math.max(this.normalBorderColorLabelWidget.getRight(), this.hoveredBorderColorLabelWidget.getRight());
        x = Math.max(x1, x2);
        x = Math.max(x, x3) + 6;
        this.nameNormalColorEditWidget.setX(x);
        this.nameHoveredColorEditWidget.setX(x);
        this.normalBackgroundColorEditWidget.setX(x);
        this.hoveredBackgroundColorEditWidget.setX(x);
        this.normalBorderColorEditWidget.setX(x);
        this.hoveredBorderColorEditWidget.setX(x);

        y += 20;
        this.cancelButton.setPosition(this.x + 10, y);
    }

    protected void cancel()
    {
        this.shouldApplyValues = false;
        this.openParentScreen();
    }

    protected void removeIcon()
    {
        this.firstWidget.setIcon(null);
        this.iconDropDownWidget.setSelectedEntry(null);
    }

    protected void applyValues()
    {
        if (this.shouldApplyValues == false)
        {
            return;
        }

        int size = this.widgets.size();

        // Copy the values from the first widget, to which they get set from the edit widgets
        if (size > 1)
        {
            int normalNameColor = this.firstWidget.getTextSettings().getTextColor();
            int hoveredNameColor = this.firstWidget.getTextSettings().getHoveredTextColor();
            int normalBg = this.firstWidget.getBackgroundRenderer().getNormalSettings().getColor();
            int hoverBg = this.firstWidget.getBackgroundRenderer().getHoverSettings().getColor();
            EdgeInt normalBorder = this.firstWidget.getBorderRenderer().getNormalSettings().getColor();
            EdgeInt hoverBorder = this.firstWidget.getBorderRenderer().getHoverSettings().getColor();

            for (int i = 1; i < size; ++i)
            {
                BaseActionExecutionWidget widget = this.widgets.get(i);
                widget.getTextSettings().setTextColor(normalNameColor);
                widget.getTextSettings().setHoveredTextColor(hoveredNameColor);

                widget.getBackgroundRenderer().getNormalSettings().setColor(normalBg);
                widget.getBackgroundRenderer().getHoverSettings().setColor(hoverBg);

                widget.getBorderRenderer().getNormalSettings().getColor().setFrom(normalBorder);
                widget.getBorderRenderer().getHoverSettings().getColor().setFrom(hoverBorder);
            }
        }
    }
}

package malilib.gui.widget;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.widget.button.GenericButton;

public abstract class BaseNumberEditWidget extends ContainerWidget
{
    protected final BaseTextFieldWidget textFieldWidget;
    protected final HorizontalSliderWidget sliderWidget;
    protected final GenericButton sliderToggleButton;
    protected final GenericButton valueAdjustButton;
    protected final LabelWidget labelWidget;
    protected boolean addLabel;
    protected boolean addSlider;
    protected boolean addValueAdjustButton = true;
    protected boolean forceSlider;
    protected boolean showRangeTooltip = true;
    protected boolean sliderActive;
    protected int altModifier = 4;
    protected int shiftModifier = 8;
    protected int labelFixedWidth = -1;
    protected int textFieldFixedWidth = -1;

    public BaseNumberEditWidget(int width, int height)
    {
        super(width, height);

        this.labelWidget = new LabelWidget();
        Supplier<Icon> supplier = () -> this.sliderActive ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER;
        this.sliderToggleButton = GenericButton.create(supplier, this::toggleSliderActive);

        this.valueAdjustButton = GenericButton.create(DefaultIcons.BTN_PLUSMINUS_16);
        this.valueAdjustButton.setActionListener(this::onValueAdjustButtonClick);
        this.valueAdjustButton.translateAndAddHoverString("malilib.hover.button.plus_minus_tip");
        this.valueAdjustButton.setCanScrollToClick(true);

        this.textFieldWidget = new BaseTextFieldWidget(60, height);
        this.textFieldWidget.setListener(this::setValueFromTextField);

        this.sliderWidget = this.createSliderWidget();

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.sliderWidget.setEnabledStatusSupplier(enabledSupplier);
        this.textFieldWidget.setEnabledStatusSupplier(enabledSupplier);
        this.valueAdjustButton.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidgetIf(this.labelWidget, this.addLabel);
        this.addWidgetIf(this.textFieldWidget, this.sliderActive == false);
        this.addWidgetIf(this.sliderWidget, this.sliderActive);
        this.addWidgetIf(this.valueAdjustButton, this.addValueAdjustButton);

        if (this.addSlider && this.forceSlider == false)
        {
            this.addWidget(this.sliderToggleButton);
        }

        if (this.showRangeTooltip)
        {
            this.textFieldWidget.getHoverInfoFactory().setStringListProvider("range", this::getRangeHoverTooltip);
            this.sliderWidget.getHoverInfoFactory().setStringListProvider("range", this::getRangeHoverTooltip);
        }
        else
        {
            this.textFieldWidget.getHoverInfoFactory().removeTextLineProvider("range");
            this.sliderWidget.getHoverInfoFactory().removeTextLineProvider("range");
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();
        int tw = this.getWidth();

        this.textFieldWidget.setY(y);

        if (this.addLabel)
        {
            this.labelWidget.setX(x);
            this.labelWidget.centerVerticallyInside(this.textFieldWidget);

            if (this.labelFixedWidth >= 0)
            {
                this.labelWidget.setAutomaticWidth(false);
                this.labelWidget.setWidth(this.labelFixedWidth);
            }

            int w = this.labelWidget.getWidth() + 2;

            tw -= w;
            x += w;
        }

        if (this.addValueAdjustButton)
        {
            tw -= this.valueAdjustButton.getWidth() + 1;
        }

        if (this.addSlider && this.forceSlider == false)
        {
            tw -= this.sliderToggleButton.getWidth() + 1;
        }

        if (this.textFieldFixedWidth >= 0)
        {
            tw = this.textFieldFixedWidth;
        }

        this.sliderWidget.setWidth(tw);
        this.textFieldWidget.setWidth(tw);
        this.sliderWidget.setPosition(x, y);
        this.textFieldWidget.setX(x);
        x = this.textFieldWidget.getRight() + 1;

        if (this.addValueAdjustButton)
        {
            this.valueAdjustButton.setX(x);
            this.valueAdjustButton.centerVerticallyInside(this.textFieldWidget);
            x = this.valueAdjustButton.getRight() + 1;
        }

        if (this.addSlider && this.forceSlider == false)
        {
            this.sliderToggleButton.setPosition(x, y);
        }

        this.updateWidth();
    }

    @Override
    protected int getRequestedContentWidth()
    {
        if (this.addSlider && this.forceSlider == false)
        {
            return this.sliderToggleButton.getRight() - this.getX();
        }

        if (this.addValueAdjustButton)
        {
            return this.valueAdjustButton.getRight() - this.getX();
        }

        return this.textFieldWidget.getRight() - this.getX();
    }

    public BaseNumberEditWidget setAddLabel(boolean addLabel)
    {
        this.addLabel = addLabel;
        return this;
    }

    public BaseNumberEditWidget setAddSlider(boolean addSlider)
    {
        this.addSlider = addSlider;
        return this;
    }

    public BaseNumberEditWidget setAddValueAdjustButton(boolean useButton)
    {
        this.addValueAdjustButton = useButton;
        return this;
    }

    public BaseNumberEditWidget setForceSlider(boolean forceSlider)
    {
        this.forceSlider = forceSlider;

        if (forceSlider)
        {
            this.sliderActive = true;
        }

        return this;
    }

    public BaseNumberEditWidget setAltModifier(int altModifier)
    {
        this.altModifier = altModifier;
        return this;
    }

    public BaseNumberEditWidget setShiftModifier(int shiftModifier)
    {
        this.shiftModifier = shiftModifier;
        return this;
    }

    public BaseTextFieldWidget getTextField()
    {
        return this.textFieldWidget;
    }

    public int getTextFieldWidgetX()
    {
        return this.sliderActive ? this.sliderWidget.getX() : this.textFieldWidget.getX();
    }

    public int getLabelWidth()
    {
        return this.labelWidget.getWidth();
    }

    protected void toggleSliderActive()
    {
        this.sliderActive = ! this.sliderActive;
        this.updateSubWidgetPositions();
        this.reAddSubWidgets();
    }

    /**
     * Set a fixed width for the label widget. Use -1 for automatic width.
     */
    public void setLabelFixedWidth(int width)
    {
        this.labelFixedWidth = width;
    }

    /**
     * Set a fixed width for the text field or slider widget. Use -1 for automatic width.
     */
    public void setTextFieldFixedWidth(int width)
    {
        this.textFieldFixedWidth = width;
    }

    public void setShowRangeTooltip(boolean showRangeTooltip)
    {
        this.showRangeTooltip = showRangeTooltip;
    }

    public LabelWidget getLabelWidget()
    {
        return this.labelWidget;
    }

    public BaseNumberEditWidget setLabelText(String translationKey, Object... args)
    {
        boolean oldAdd = this.addLabel;
        this.labelWidget.translateSetLines(translationKey, args);
        this.addLabel = true;

        if (oldAdd == false)
        {
            this.reAddSubWidgets();
            this.updateSubWidgetPositions();
        }

        return this;
    }

    protected void setValueFromTextField(String str)
    {
        this.parseClampAndSetValue(str);
        this.updateConsumer();
    }

    protected void updateTextField()
    {
        this.textFieldWidget.setText(this.getValueStringForTextfield());
    }

    protected abstract String getValueStringForTextfield();

    protected abstract void updateConsumer();

    protected abstract void parseClampAndSetValue(String newValueStr);

    protected abstract HorizontalSliderWidget createSliderWidget();

    protected abstract boolean onValueAdjustButtonClick(int mouseButton);

    protected abstract List<String> getRangeHoverTooltip();
}

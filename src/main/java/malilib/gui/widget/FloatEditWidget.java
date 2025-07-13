package malilib.gui.widget;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.gui.BaseScreen;
import malilib.gui.callback.FloatSliderCallback;
import malilib.util.MathUtils;
import malilib.util.StringUtils;
import malilib.util.data.FloatConsumer;
import malilib.util.data.FloatSupplier;
import malilib.util.data.RangedFloatStorage;

public class FloatEditWidget extends BaseNumberEditWidget implements RangedFloatStorage
{
    protected FloatConsumer consumer;
    @Nullable protected FloatSupplier supplier;
    protected float minValue;
    protected float maxValue;
    protected float value;

    public FloatEditWidget(int width, int height, float originalValue,
                           float minValue, float maxValue, FloatConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;

        this.setValidRange(minValue, maxValue);
        this.setFloatValue(originalValue);
    }

    @Override
    protected HorizontalSliderWidget createSliderWidget()
    {
        return new HorizontalSliderWidget(-1, this.getHeight(), new FloatSliderCallback(this, this::updateTextField));
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        float amount = mouseButton == 1 ? -0.25F : 0.25F;
        if (BaseScreen.isShiftDown()) { amount *= this.shiftModifier; }
        if (BaseScreen.isAltDown()) { amount *= this.altModifier; }

        this.setFloatValue(this.value + amount);

        return true;
    }

    public void setConsumer(FloatConsumer consumer)
    {
        this.consumer = consumer;
    }

    public void setSupplier(@Nullable FloatSupplier supplier)
    {
        this.supplier = supplier;
    }

    public void setValidRange(float minValue, float maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.textFieldWidget.setTextValidator(new DoubleTextFieldWidget.DoubleValidator(minValue, maxValue));
    }

    @Override
    protected String getValueStringForTextfield()
    {
        return String.valueOf(this.value);
    }

    @Override
    protected void parseClampAndSetValue(String newValueStr)
    {
        try
        {
            this.clampAndSetValue(Float.parseFloat(newValueStr));
        }
        catch (NumberFormatException ignore) {}
    }

    protected void clampAndSetValue(float newValue)
    {
        this.value = MathUtils.clamp(newValue, this.minValue, this.maxValue);
        this.sliderWidget.updateWidgetState();
        this.textFieldWidget.getHoverInfoFactory().updateList();
    }

    public void setValueFromSupplier()
    {
        if (this.supplier != null)
        {
            this.clampAndSetValue(this.supplier.getAsFloat());
            this.updateTextField();
        }
    }

    @Override
    protected void updateConsumer()
    {
        this.consumer.accept(this.value);
    }

    @Override
    public boolean setFloatValue(float newValue)
    {
        this.clampAndSetValue(newValue);
        this.updateTextField();
        this.updateConsumer();
        return true;
    }

    @Override
    public float getFloatValue()
    {
        return this.value;
    }

    @Override
    public float getMinFloatValue()
    {
        return this.minValue;
    }

    @Override
    public float getMaxFloatValue()
    {
        return this.maxValue;
    }

    @Override
    protected List<String> getRangeHoverTooltip()
    {
        return Collections.singletonList(StringUtils.translate("malilib.hover.config.numeric.range",
                                                               this.minValue, this.maxValue));
    }
}

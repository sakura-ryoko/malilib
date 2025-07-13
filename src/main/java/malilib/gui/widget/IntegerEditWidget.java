package malilib.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;

import malilib.gui.BaseScreen;
import malilib.gui.callback.IntegerSliderCallback;
import malilib.util.MathUtils;
import malilib.util.StringUtils;
import malilib.util.data.RangedIntegerStorage;

public class IntegerEditWidget extends BaseNumberEditWidget implements RangedIntegerStorage
{
    protected IntConsumer consumer;
    protected IntFunction<String> toStringFunction = String::valueOf;
    @Nullable protected IntSupplier supplier;
    protected int minValue;
    protected int maxValue;
    protected int value;

    public IntegerEditWidget(int width, int height, RangedIntegerStorage storage)
    {
        this(width, height, storage.getIntegerValue(), storage.getMinIntegerValue(),
             storage.getMaxIntegerValue(), storage::setIntegerValue);
    }

    public IntegerEditWidget(int width, int height, IntConsumer consumer)
    {
        this(width, height, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, consumer);
    }

    public IntegerEditWidget(int width, int height, IntConsumer consumer, IntSupplier supplier)
    {
        this(width, height, supplier.getAsInt(), Integer.MIN_VALUE, Integer.MAX_VALUE, consumer);

        this.supplier = supplier;
    }

    public IntegerEditWidget(int width, int height, int originalValue, IntConsumer consumer)
    {
        this(width, height, originalValue, Integer.MIN_VALUE, Integer.MAX_VALUE, consumer);
    }

    public IntegerEditWidget(int width, int height, int originalValue,
                             int minValue, int maxValue, IntConsumer consumer)
    {
        super(width, height);

        this.consumer = consumer;

        this.setValidRange(minValue, maxValue);
        this.clampAndSetValue(originalValue);
        this.updateTextField();
    }

    @Override
    protected HorizontalSliderWidget createSliderWidget()
    {
        return new HorizontalSliderWidget(-1, this.getHeight(), new IntegerSliderCallback(this, this::updateTextField));
    }

    @Override
    protected boolean onValueAdjustButtonClick(int mouseButton)
    {
        int amount = mouseButton == 1 ? -1 : 1;
        if (BaseScreen.isShiftDown()) { amount *= this.shiftModifier; }
        if (BaseScreen.isAltDown()) { amount *= this.altModifier; }

        this.setIntegerValue(this.value + amount);

        return true;
    }

    public void setConsumer(IntConsumer consumer)
    {
        this.consumer = consumer;
    }

    public void setSupplier(@Nullable IntSupplier supplier)
    {
        this.supplier = supplier;
    }

    public void setToStringFunction(IntFunction<String> toStringFunction)
    {
        this.toStringFunction = toStringFunction;
    }

    public void setValidRange(int minValue, int maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.textFieldWidget.setTextValidator(new IntegerTextFieldWidget.IntRangeValidator(minValue, maxValue));
    }

    @Override
    protected String getValueStringForTextfield()
    {
        return this.toStringFunction.apply(this.value);
    }

    @Override
    protected void parseClampAndSetValue(String newValueStr)
    {
        try
        {
            this.clampAndSetValue(Integer.parseInt(newValueStr));
        }
        catch (NumberFormatException ignore) {}
    }

    protected void clampAndSetValue(int newValue)
    {
        this.value = MathUtils.clamp(newValue, this.minValue, this.maxValue);
        this.sliderWidget.updateWidgetState();
        this.textFieldWidget.getHoverInfoFactory().updateList();
    }

    public void setValueFromSupplier()
    {
        if (this.supplier != null)
        {
            this.clampAndSetValue(this.supplier.getAsInt());
            this.updateTextField();
        }
    }

    @Override
    protected void updateConsumer()
    {
        this.consumer.accept(this.value);
    }

    @Override
    public boolean setIntegerValue(int newValue)
    {
        this.clampAndSetValue(newValue);
        this.updateTextField();
        this.updateConsumer();
        return true;
    }

    @Override
    public int getIntegerValue()
    {
        return this.value;
    }

    @Override
    public int getMinIntegerValue()
    {
        return this.minValue;
    }

    @Override
    public int getMaxIntegerValue()
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

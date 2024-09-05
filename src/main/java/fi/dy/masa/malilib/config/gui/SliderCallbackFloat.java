package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.config.IConfigFloat;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;

public class SliderCallbackFloat implements ISliderCallback
{
    protected final IConfigFloat config;
    protected final ButtonBase resetButton;

    public SliderCallbackFloat(IConfigFloat config, @Nullable ButtonBase resetButton)
    {
        this.config = config;
        this.resetButton = resetButton;
    }

    @Override
    public int getMaxSteps()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public double getValueRelative()
    {
        return (double) (this.config.getFloatValue() - this.config.getMinFloatValue()) / (this.config.getMaxFloatValue() - this.config.getMinFloatValue());
    }

    @Override
    public void setValueRelative(double relativeValue)
    {
        float relValue = (float) (relativeValue * (this.config.getMaxFloatValue() - this.config.getMinFloatValue()));
        this.config.setFloatValue(relValue + this.config.getMinFloatValue());

        if (this.resetButton != null)
        {
            this.resetButton.setEnabled(this.config.isModified());
        }
    }

    @Override
    public String getFormattedDisplayValue()
    {
        return String.format("%.4f", this.config.getFloatValue());
    }
}

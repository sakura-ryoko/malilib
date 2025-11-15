package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;

public record SliderCallbackInteger(IConfigInteger config, ButtonBase buttonReset) implements ISliderCallback
{
	public SliderCallbackInteger(IConfigInteger config, @Nullable ButtonBase buttonReset)
	{
		this.config = config;
		this.buttonReset = buttonReset;
	}

	@Override
	public int getMaxSteps()
	{
		int steps = this.config.getMaxIntegerValue() - this.config.getMinIntegerValue() + 1;
		return steps > 0 ? steps : Integer.MAX_VALUE;
	}

	@Override
	public double getValueRelative()
	{
		return (double) (this.config.getIntegerValue() - this.config.getMinIntegerValue()) / (double) (this.config.getMaxIntegerValue() - this.config.getMinIntegerValue());
	}

	@Override
	public void setValueRelative(double relativeValue)
	{
		int relValue = (int) (relativeValue * (this.config.getMaxIntegerValue() - this.config.getMinIntegerValue()));
		this.config.setIntegerValue(relValue + this.config.getMinIntegerValue());

		if (this.buttonReset != null)
		{
			this.buttonReset.setEnabled(this.config.isModified());
		}
	}

	@Override
	public String getFormattedDisplayValue()
	{
		return String.valueOf(this.config.getIntegerValue());
	}
}

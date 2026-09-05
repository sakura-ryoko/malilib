package fi.dy.masa.malilib.gui;

import java.math.BigDecimal;
import net.minecraft.client.gui.Font;

public class GuiTextFieldDouble extends GuiTextFieldGeneric
{
    public GuiTextFieldDouble(int x, int y, int width, int height, Font fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

	    this.setResponder(this::onTextChanged);
    }

	protected boolean testDouble(String input)
	{
		try
		{
			Double.parseDouble(input);
			return true;
		}
		catch (NumberFormatException ignored) { }

		return false;
	}

	protected int getDoubleDecimalCount(String input)
	{
		try
		{
			int scale = BigDecimal.valueOf(Double.parseDouble(input)).scale();

			if (scale >= 0)
			{
				return scale;
			}
		}
		catch (NumberFormatException ignored) { }

		return -1;
	}

	protected void onTextChanged(String newText)
	{
		if (!this.testDouble(newText))
		{
			this.setHoverTooltip("malilib.gui.text_field.invalid_double");
		}
		else if (newText.contains("e") || newText.contains("E") ||
				 newText.contains("e+") || newText.contains("E+") ||
				 newText.contains("e-") || newText.contains("E-"))
		{
			this.setHoverTooltip("malilib.gui.text_field.double_has_scientific_notation");
		}
		else
		{
			int decimals = this.getDoubleDecimalCount(newText);

			if (decimals > 2)
			{
				this.setHoverTooltip("malilib.gui.text_field.double_has_additional_decimals", String.format("%d", decimals));
			}
			else
			{
				this.clearHoverTooltip();
			}
		}
	}
}

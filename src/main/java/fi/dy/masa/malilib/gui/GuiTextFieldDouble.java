package fi.dy.masa.malilib.gui;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import net.minecraft.client.font.TextRenderer;

public class GuiTextFieldDouble extends GuiTextFieldGeneric
{
    // Regex doesn't allow commas or other locale-specific notations
    private static final Pattern PATTERN_NUMBER = Pattern.compile("[-+]?([0-9]*[.])?[0-9]+([eE][-+]?\\d+)?");
//    private static final Pattern PATTERN_NUMBER = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?");
//    private static final Pattern PATTERN_NUMBER = Pattern.compile("^\\b\\d[\\d,.' ]*\\b");

    public GuiTextFieldDouble(int x, int y, int width, int height, TextRenderer fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

//        this.setTextPredicate(input -> input.isEmpty() || PATTERN_NUMBER.matcher(input).matches());
	    this.setChangedListener(this::onChanged);
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

	protected void onChanged(String newText)
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

//    @Override
//    protected String getValueStringForTextfield()
//    {
//        String val = super.getValueStringForTextfield();
//
//        if (MaLiLibConfigs.Generic.COORDINATE_DECIMAL_CLAMPING.getBooleanValue())
//        {
//            int decimals = MaLiLibConfigs.Generic.COORDINATE_DECIMAL_CLAMPING.getIntegerValue();
//            int expIndex = val.indexOf('E');
//            int dotIndex = val.indexOf('.');
//
//            if (dotIndex > 0)
//            {
//                // Scientific notation, yeet the decimals from the middle
//                if (expIndex > dotIndex && val.length() > expIndex + 1)
//                {
//                    // 123.456789E12 => 123.45E12
//
//                    try
//                    {
//                        int expValue = Integer.parseInt(val.substring(expIndex + 1));
//                        decimals += expValue;
//                    }
//                    catch (Exception ignore)
//                    {
//                        return val;
//                    }
//
//                    int last = Math.min(val.length(), dotIndex + decimals + 1);
//                    val = val.substring(0, last) + val.substring(expIndex);
//                }
//                // Normal decimal format
//                else
//                {
//                    int last = Math.min(val.length(), dotIndex + decimals + 1);
//                    val = val.substring(0, last);
//                }
//            }
//        }
//
//        return val;
//    }
}

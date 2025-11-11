package fi.dy.masa.malilib.gui;

import java.util.regex.Pattern;

import net.minecraft.client.font.TextRenderer;

public class GuiTextFieldInteger extends GuiTextFieldGeneric
{
    // Regex doesn't allow commas or other locale-specific notations
    private static final Pattern PATTERN_NUMBER = Pattern.compile("-?[0-9]*");
//    private static final Pattern PATTERN_NUMBER = Pattern.compile("^\\b\\d[\\d,.' ]*\\b");

    public GuiTextFieldInteger(int x, int y, int width, int height, TextRenderer fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

        this.setTextPredicate(input -> input.isEmpty() || PATTERN_NUMBER.matcher(input).matches());
		this.setChangedListener(this::onChanged);
    }

	protected boolean testInteger(String input)
	{
		try
		{
			Integer.parseInt(input);
			return true;
		}
		catch (NumberFormatException ignored) { }

		return false;
	}

	protected void onChanged(String newText)
	{
		if (!this.testInteger(newText))
		{
			this.setHoverTooltip("malilib.gui.text_field.invalid_integer");
		}
		else
		{
			this.clearHoverTooltip();
		}
	}
}

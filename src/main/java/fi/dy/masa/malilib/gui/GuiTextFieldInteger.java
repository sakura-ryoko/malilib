package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.Font;

public class GuiTextFieldInteger extends GuiTextFieldGeneric
{
    public GuiTextFieldInteger(int x, int y, int width, int height, Font fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

		this.setResponder(this::onTextChanged);
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

	protected void onTextChanged(String newText)
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

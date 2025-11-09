package fi.dy.masa.malilib.gui;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.client.gui.Font;

public class GuiTextFieldDouble extends GuiTextFieldGeneric
{
    // Regex doesn't allow commas or other locale-specific notations
    private static final Pattern PATTER_NUMBER = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?");
//    private static final Pattern PATTER_NUMBER = Pattern.compile("^\\b\\d[\\d,.' ]*\\b");

    public GuiTextFieldDouble(int x, int y, int width, int height, Font fontRenderer)
    {
        super(x, y, width, height, fontRenderer);

        this.setFilter(new Predicate<String>()
        {
            @Override
            public boolean test(String input)
            {
                if (input.length() > 0 && PATTER_NUMBER.matcher(input).matches() == false)
                {
                    try
                    {
                        Double.parseDouble(input);
                        return true;
                    }
                    catch (NumberFormatException ignored) { }
                }

                try
                {
                    Double.parseDouble(input);
                    return !input.isEmpty();
                }
                catch (NumberFormatException ignored) { }
                return false;
            }
        });
    }
}

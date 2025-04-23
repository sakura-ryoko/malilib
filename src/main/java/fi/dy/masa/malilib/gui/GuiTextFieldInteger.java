package fi.dy.masa.malilib.gui;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.client.font.TextRenderer;

import fi.dy.masa.malilib.render.GuiLayer;

public class GuiTextFieldInteger extends GuiTextFieldGeneric
{
    private static final Pattern PATTER_NUMBER = Pattern.compile("-?[0-9]*");

    public GuiTextFieldInteger(int x, int y, int width, int height, TextRenderer fontRenderer, GuiLayer type)
    {
        super(x, y, width, height, fontRenderer, type);

        this.setTextPredicate(new Predicate<String>()
        {
            @Override
            public boolean test(String input)
            {
                if (input.length() > 0 && PATTER_NUMBER.matcher(input).matches() == false)
                {
                    return false;
                }

                return true;
            }
        });
    }
}

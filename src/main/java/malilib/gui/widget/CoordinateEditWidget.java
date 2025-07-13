package malilib.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;
import malilib.MaLiLibConfigs;
import malilib.util.StringUtils;

public class CoordinateEditWidget extends DoubleEditWidget
{
    public CoordinateEditWidget(int width, int height, double originalValue,
                                double minValue, double maxValue, DoubleConsumer consumer)
    {
        super(width, height, originalValue, minValue, maxValue, consumer);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.showRangeTooltip)
        {
            this.textFieldWidget.getHoverInfoFactory().setStringListProvider("value", this::getFullValueHoverTooltip);
            this.sliderWidget.getHoverInfoFactory().setStringListProvider("value", this::getFullValueHoverTooltip);
        }
        else
        {
            this.textFieldWidget.getHoverInfoFactory().removeTextLineProvider("value");
            this.sliderWidget.getHoverInfoFactory().removeTextLineProvider("value");
        }
    }

    @Override
    protected String getValueStringForTextfield()
    {
        String val = super.getValueStringForTextfield();

        if (MaLiLibConfigs.Generic.COORDINATE_DECIMAL_CLAMPING.getBooleanValue())
        {
            int decimals = MaLiLibConfigs.Generic.COORDINATE_DECIMAL_CLAMPING.getIntegerValue();
            int expIndex = val.indexOf('E');
            int dotIndex = val.indexOf('.');

            if (dotIndex > 0)
            {
                // Scientific notation, yeet the decimals from the middle
                if (expIndex > dotIndex && val.length() > expIndex + 1)
                {
                    // 123.456789E12 => 123.45E12

                    try
                    {
                        int expValue = Integer.parseInt(val.substring(expIndex + 1));
                        decimals += expValue;
                    }
                    catch (Exception ignore)
                    {
                        return val;
                    }

                    int last = Math.min(val.length(), dotIndex + decimals + 1);
                    val = val.substring(0, last) + val.substring(expIndex);
                }
                // Normal decimal format
                else
                {
                    int last = Math.min(val.length(), dotIndex + decimals + 1);
                    val = val.substring(0, last);
                }
            }
        }

        return val;
    }

    protected List<String> getFullValueHoverTooltip()
    {
        return Collections.singletonList(StringUtils.translate("malilib.hover.config.numeric.full_value",
                                                               String.valueOf(this.value)));
    }
}

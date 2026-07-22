package malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import malilib.config.option.OptionConfig;
import malilib.config.value.OptionConfigValue;
import malilib.listener.EventListener;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;

public class OptionListConfigButton extends GenericButton
{
    protected final OptionConfig<?> config;
    @Nullable protected final String prefixTranslationKey;
    @Nullable protected EventListener changeListener;

    public OptionListConfigButton(int width, int height,
                                  OptionConfig<?> config)
    {
        this(width, height, config, null);
    }

    public OptionListConfigButton(int width, int height,
                                  OptionConfig<?> config,
                                  @Nullable String prefixTranslationKey)
    {
        super(width, height);

        this.config = config;
        this.prefixTranslationKey = prefixTranslationKey;

        this.getHoverInfoFactory().setTextLineProvider("list_preview", this::getOptionListPreviewHoverString, 100);
        this.setActionListener(this::cycleValue);
        this.setDisplayStringSupplier(this::getCurrentDisplayString);
    }

    public void setChangeListener(@Nullable EventListener changeListener)
    {
        this.changeListener = changeListener;
    }

    protected boolean cycleValue(int mouseButton)
    {
        if (mouseButton == 2)
        {
            this.config.resetToDefault();
        }
        else
        {
            this.config.cycleValue(mouseButton != 0);
        }

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }

        return true;
    }

    protected String getCurrentDisplayString()
    {
        String configDisplayText = this.config.getValue().getDisplayName();

        if (this.prefixTranslationKey != null)
        {
            return StringUtils.translate(this.prefixTranslationKey, configDisplayText);
        }
        else
        {
            return configDisplayText;
        }
    }

    protected List<StyledTextLine> getOptionListPreviewHoverString(List<StyledTextLine> previousLines)
    {
        return getOptionListPreviewHoverString(this.config, previousLines);
    }

    public static List<StyledTextLine> getOptionListPreviewHoverString(OptionConfig<?> config,
                                                                       List<StyledTextLine> previousLines)
    {
        List<StyledTextLine> lines = new ArrayList<>();
        List<OptionConfigValue> allValues = new ArrayList<>(config.getAllValues());
        Set<OptionConfigValue> allowedValues = new HashSet<>(config.getAllowedValues());
        OptionConfigValue currentValue = config.getValue();
        int totalValues = allValues.size();
        int allowedValuesCount = config.getAllowedValues().size();

        if (previousLines.isEmpty() == false)
        {
            lines.add(StyledTextLine.EMPTY);
        }

        if (totalValues == allowedValuesCount)
        {
            StyledTextLine.translate(lines, "malilib.hover.config.option_list.total_values.all", totalValues);
        }
        else
        {
            StyledTextLine.translate(lines, "malilib.hover.config.option_list.total_values.allowed",
                                     allowedValuesCount, totalValues);
        }

        for (OptionConfigValue value : allValues)
        {
            if (allowedValues.contains(value))
            {
                if (currentValue.equals(value))
                {
                    StyledTextLine.translate(lines, "malilib.hover.config.option_list.selected_value",
                                             value.getDisplayName());
                }
                else
                {
                    StyledTextLine.translate(lines, "malilib.hover.config.option_list.non_selected_value",
                                             value.getDisplayName());
                }
            }
        }

        if (totalValues != allowedValuesCount)
        {
            lines.add(StyledTextLine.EMPTY);
            StyledTextLine.translate(lines, "malilib.hover.config.option_list.total_values.disallowed",
                                     totalValues - allowedValuesCount, totalValues);

            for (OptionConfigValue value : allValues)
            {
                if (allowedValues.contains(value) == false)
                {
                    StyledTextLine.translate(lines, "malilib.hover.config.option_list.non_selected_value",
                                             value.getDisplayName());
                }
            }
        }

        lines.add(StyledTextLine.EMPTY);
        StyledTextLine.translate(lines, "malilib.hover.config.option_list.default_value",
                                 config.getDefaultValue().getDisplayName());

        return lines;
    }
}

package malilib.overlay.widget.sub;

import malilib.MaLiLibReference;
import malilib.config.option.OptionConfig;
import malilib.config.value.OptionConfigValue;
import malilib.render.text.StyledTextLine;
import malilib.util.data.ConfigOnTab;

public class OptionListConfigStatusWidget extends BaseConfigStatusIndicatorWidget<OptionConfig<OptionConfigValue>>
{
    protected OptionConfigValue lastValue;

    public OptionListConfigStatusWidget(OptionConfig<OptionConfigValue> config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_option_list");
    }

    public OptionListConfigStatusWidget(OptionConfig<OptionConfigValue> config,
                                        ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);

        this.valueColor = 0xFFFFA000;
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.lastValue != this.config.getValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getValue();
        this.valueDisplayText = StyledTextLine.parseFirstLine(this.lastValue.getDisplayName());
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}

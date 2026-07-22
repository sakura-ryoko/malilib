package malilib.gui.config;

import javax.annotation.Nullable;

import malilib.config.option.OptionConfig;
import malilib.config.option.list.BlackWhiteListConfig;
import malilib.config.option.list.ValueListConfig;
import malilib.config.value.BlackWhiteList;
import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteListEditScreen<TYPE> extends BaseConfigGroupEditScreen
{
    protected final BlackWhiteListConfig<TYPE> config;
    protected final OptionConfig<ListType> typeConfig;
    protected final ValueListConfig<TYPE> blackListConfig;
    protected final ValueListConfig<TYPE> whiteListConfig;
    @Nullable protected final Runnable saveListener;

    public BlackWhiteListEditScreen(BlackWhiteListConfig<TYPE> config, @Nullable Runnable saveListener)
    {
        super(config.getModInfo(), null);

        this.config = config;
        this.saveListener = saveListener;
        this.setTitle("malilib.title.screen.black_white_list_edit", config.getDisplayName());

        // Initialize them to the default value so that the reset button is active when they differ from the default value,
        // and also so that the reset restores them to the default value, not the value they were at when the screen was opened
        BlackWhiteList<TYPE> bwList = config.getValue();
        this.typeConfig = new OptionConfig<>("malilib.label.config.black_white_list_edit.type",
                                             config.getDefaultValue().getListType(), ListType.VALUES);
        this.typeConfig.setValue(bwList.getListType());
        this.typeConfig.setCommentTranslationKey(null);

        this.blackListConfig = bwList.getBlackList().copy();
        this.blackListConfig.setCommentTranslationKey(null);
        this.whiteListConfig = bwList.getWhiteList().copy();
        this.whiteListConfig.setCommentTranslationKey(null);

        this.configs.add(this.typeConfig);
        this.configs.add(this.blackListConfig);
        this.configs.add(this.whiteListConfig);

        this.addPreScreenCloseListener(this::saveConfigChanges);
    }

    protected void saveConfigChanges()
    {
        if (this.config.isLocked() == false)
        {
            BlackWhiteList<TYPE> old = this.config.getDefaultValue();
            BlackWhiteList<TYPE> list = new BlackWhiteList<>(this.typeConfig.getValue(),
                                                             this.blackListConfig,
                                                             this.whiteListConfig,
                                                             old.getToStringConverter(),
                                                             old.getFromStringConverter());
            this.config.setValue(list);

            if (this.saveListener != null)
            {
                this.saveListener.run();
            }
        }
    }

    @Override
    protected ConfigOptionListWidget createListWidget()
    {
        ConfigOptionListWidget listWidget = super.createListWidget();
        listWidget.setShowInternalConfigName(false);
        return listWidget;
    }
}

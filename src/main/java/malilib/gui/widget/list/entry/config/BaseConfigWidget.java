package malilib.gui.widget.list.entry.config;

import java.util.ArrayList;
import java.util.List;

import malilib.MaLiLibConfigs;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigTab;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.listener.EventListener;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.data.ConfigOnTab;

public abstract class BaseConfigWidget<CFG extends ConfigInfo> extends BaseDataListEntryWidget<CFG>
{
    protected final CFG config;
    protected final ConfigWidgetContext ctx;
    protected final GenericButton resetButton;
    protected final LabelWidget configOwnerAndNameLabelWidget;
    protected final StyledTextLine nameText;
    protected final StyledTextLine internalNameText;
    protected final StyledTextLine categoryText;

    public BaseConfigWidget(CFG config,
                            DataListEntryWidgetData constructData,
                            ConfigWidgetContext ctx)
    {
        super(config, constructData);

        this.config = config;
        this.ctx = ctx;

        String ownerLabel = this.getOwnerText();
        this.categoryText = StyledTextLine.parseFirstLine(ownerLabel);
        this.nameText = StyledTextLine.translateFirstLine("malilib.label.config.config_display_name", config.getDisplayName());
        this.internalNameText = StyledTextLine.translateFirstLine("malilib.label.config.config_internal_name", config.getName());
        this.configOwnerAndNameLabelWidget = new LabelWidget(this.getMaxLabelWidth(), this.getHeight());
        this.resetButton = GenericButton.create("malilib.button.misc.reset.caps", this::onResetButtonClicked);
        this.resetButton.setEnabledStatusSupplier(this::isResetEnabled);

        EventListener clickHandler = config.getLabelClickHandler();
        List<String> comments = new ArrayList<>();

        if (clickHandler != null)
        {
            comments.add(StringUtils.translate("malilib.hover.config.click_for_more_information"));
            this.configOwnerAndNameLabelWidget.setClickListener(clickHandler);
            this.configOwnerAndNameLabelWidget.getBorderRenderer()
                    .getHoverSettings().setBorderWidthAndColor(1, 0xFF15D6F0);
        }

        config.getComment().ifPresent(comments::add);

        this.configOwnerAndNameLabelWidget.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0x30FFFFFF);
        this.configOwnerAndNameLabelWidget.getHoverInfoFactory().addStrings(comments);

        int size = this.configOwnerAndNameLabelWidget.getHoverInfoFactory().getStyledLines().size();
        int maxLines = MaLiLibConfigs.Generic.CONFIG_TOOLTIP_MAX_LINES_DIRECT.getIntegerValue();
        this.configOwnerAndNameLabelWidget.setHoverInfoRequiresShift(size > maxLines);

        boolean bgEnabled = MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND.getBooleanValue();
        this.getBackgroundRenderer().getNormalSettings()
                .setEnabledAndColor(bgEnabled, this.isOdd ? 0x70606060 : 0x70909090);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.configOwnerAndNameLabelWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int nesting = this.getNestingOffset(this.ctx.getConfigOnTab().getNestingLevel());
        boolean showCategory = this.ctx.getListWidget().isShowingOptionsFromOtherCategories();

        this.configOwnerAndNameLabelWidget.setPosition(this.getX(), this.getY());
        this.configOwnerAndNameLabelWidget.getPadding().setLeft(nesting + 4);

        if (showCategory)
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setLines(this.nameText, this.categoryText);
        }
        else if (this.shouldShowInternalName())
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setLines(this.nameText, this.internalNameText);
        }
        else
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(7);
            this.configOwnerAndNameLabelWidget.setLines(this.nameText);
        }
    }

    protected void onResetButtonClicked()
    {
        this.config.resetToDefault();
        this.updateWidgetState();
    }

    @Override
    public void updateWidgetState()
    {
        this.resetButton.updateWidgetState();
    }

    protected boolean isResetEnabled()
    {
        return this.config.isModified();
    }

    protected boolean shouldShowInternalName()
    {
        return this.ctx.getListWidget().getShowInternalConfigName();
    }

    protected String getOwnerText()
    {
        return getOwnerText(this.ctx.getConfigOnTab(), this.shouldShowInternalName());
    }

    protected int getElementWidth()
    {
        return this.ctx.getListWidget().getElementWidth();
    }

    public boolean wasModified()
    {
        return false;
    }

    public int getMaxLabelWidth()
    {
        return this.ctx.getListWidget().getMaxLabelWidth();
    }

    public int getNestingOffset(int nestingLevel)
    {
        return nestingLevel * 13;
    }

    protected int getElementsStartPosition()
    {
        int nestingLevel = this.ctx.getConfigOnTab().getNestingLevel();
        int offset = this.getNestingOffset(nestingLevel);
        return this.getX() + this.getMaxLabelWidth() + offset + 10;
    }

    public static String getOwnerText(ConfigOnTab configOnTab, boolean shouldShowInternalName)
    {
        ConfigTab tab = configOnTab.getTab();
        String modName = configOnTab.getConfig().getModInfo().getModName();
        String tabName = tab.getDisplayName();

        if (shouldShowInternalName)
        {
            String configName = configOnTab.getConfig().getName();
            return StringUtils.translate("malilib.label.config.mod_category_internal_name",
                                         modName, tabName, configName);
        }
        else
        {
            return StringUtils.translate("malilib.label.config.mod_category", modName, tabName);
        }
    }
}

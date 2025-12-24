package malilib.gui.widget;

import malilib.config.value.LayerMode;
import malilib.gui.widget.IntegerTextFieldWidget.IntValueValidator;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.OnOffButton;
import malilib.util.StringUtils;
import malilib.util.game.wrap.GameWrap;
import malilib.util.position.LayerRange;

public class BaseLayerRangeEditWidget extends ContainerWidget
{
    protected final GenericButton modeButton;
    protected final GenericButton axisButton;
    protected final GenericButton setToPlayerButton;
    protected final GenericButton followPlayerButton;
    protected final IntegerEditWidget minLayerEditWidget;
    protected final IntegerEditWidget maxLayerEditWidget;
    protected final IntegerEditWidget singleLayerEditWidget;
    protected final IntegerEditWidget followOffsetEditWidget;
    protected final CheckBoxWidget moveMinLayerCheckbox;
    protected final CheckBoxWidget moveMaxLayerCheckbox;
    protected final LayerRange range;
    protected boolean addPlayerFollowingOptions;
    protected boolean addLayerRangeHotkeyCheckboxes;

    public BaseLayerRangeEditWidget(int width, int height, LayerRange range)
    {
        super(width, height);

        this.range = range;

        this.modeButton = GenericButton.create(20, this::getModeButtonLabel, this::cycleMode);
        this.axisButton = GenericButton.create(20, this::getAxisButtonLabel, this::cycleAxis);
        this.setToPlayerButton = GenericButton.create(20, "malilib.button.layer_range.set_to_player", this::setToPlayer);
        this.followPlayerButton = OnOffButton.onOff(20, "malilib.button.layer_range.follow_player",
                                                    this.range::shouldFollowPlayer, this::toggleShouldFollowPlayer);

        this.singleLayerEditWidget = new IntegerEditWidget(100, 18, this.range.getCurrentLayerValue(false), this.range::setSingleBoundaryToPosition);
        this.minLayerEditWidget = new IntegerEditWidget(100, 18, this.range.getLayerRangeMin(), this.range::setLayerRangeMin);
        this.maxLayerEditWidget = new IntegerEditWidget(100, 18, this.range.getLayerRangeMax(), this.range::setLayerRangeMax);
        this.followOffsetEditWidget = new IntegerEditWidget(100, 18, this.range.getPlayerFollowOffset(),
                                                            -1024, 1024, this::setFollowOffset);

        String label = "malilib.checkbox.layer_range.hotkey";
        String hover = "malilib.hover.checkbox.layer_range.hotkey";
        this.moveMinLayerCheckbox = new CheckBoxWidget(label, hover, this.range::getMoveLayerRangeMin, this.range::setMoveLayerRangeMin);
        this.moveMaxLayerCheckbox = new CheckBoxWidget(label, hover, this.range::getMoveLayerRangeMax, this.range::setMoveLayerRangeMax);

        this.followPlayerButton.translateAndAddHoverString("malilib.hover.button.layer_range.follow_player");
        this.followOffsetEditWidget.setLabelText("malilib.label.layer_range.player_follow_offset");
        this.singleLayerEditWidget.setLabelText("malilib.label.layer_range.layer");
        this.minLayerEditWidget.setLabelText("malilib.label.layer_range.layer_min");
        this.maxLayerEditWidget.setLabelText("malilib.label.layer_range.layer_max");

        int w = Math.max(this.minLayerEditWidget.getLabelWidth(), this.maxLayerEditWidget.getLabelWidth());
        this.minLayerEditWidget.setLabelFixedWidth(w);
        this.maxLayerEditWidget.setLabelFixedWidth(w);

        this.singleLayerEditWidget.setAutomaticWidth(true);
        this.maxLayerEditWidget.setAutomaticWidth(true);
        this.minLayerEditWidget.setAutomaticWidth(true);

        this.singleLayerEditWidget.setTextFieldFixedWidth(66);
        this.minLayerEditWidget.setTextFieldFixedWidth(66);
        this.maxLayerEditWidget.setTextFieldFixedWidth(66);

        this.singleLayerEditWidget.setShowRangeTooltip(false);
        this.minLayerEditWidget.setShowRangeTooltip(false);
        this.maxLayerEditWidget.setShowRangeTooltip(false);

        this.singleLayerEditWidget.getTextField().setUpdateListenerAlways(true);
        this.minLayerEditWidget.getTextField().setUpdateListenerAlways(true);
        this.maxLayerEditWidget.getTextField().setUpdateListenerAlways(true);

        this.minLayerEditWidget.getTextField().addChainedTextValidator(new IntValueValidator(v -> v <= this.range.getLayerRangeMax(), "malilib.message.error.layer_range.min_larger_than_max"));
        this.maxLayerEditWidget.getTextField().addChainedTextValidator(new IntValueValidator(v -> v >= this.range.getLayerRangeMin(), "malilib.message.error.layer_range.max_smaller_than_min"));

        this.setLayerValues();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.modeButton);

        if (this.range.getLayerMode() != LayerMode.ALL)
        {
            this.addWidget(this.axisButton);

            if (this.range.getLayerMode() == LayerMode.LAYER_RANGE)
            {
                this.addWidget(this.minLayerEditWidget);
                this.addWidget(this.maxLayerEditWidget);

                if (this.addLayerRangeHotkeyCheckboxes)
                {
                    this.addWidgetIf(this.moveMinLayerCheckbox, this.addLayerRangeHotkeyCheckboxes);
                    this.addWidgetIf(this.moveMaxLayerCheckbox, this.addLayerRangeHotkeyCheckboxes);
                }
            }
            else
            {
                this.addWidget(this.singleLayerEditWidget);
            }

            this.addWidget(this.setToPlayerButton);

            if (this.addPlayerFollowingOptions)
            {
                this.addWidget(this.followPlayerButton);
                this.addWidgetIf(this.followOffsetEditWidget, this.range.shouldFollowPlayer());
            }
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX() + 4;
        int y = this.getY() + 4;

        this.modeButton.setPosition(x, y);
        this.axisButton.setPosition(this.modeButton.getRight() + 4, y);

        this.singleLayerEditWidget.setPosition(x, this.modeButton.getBottom() + 4);
        this.maxLayerEditWidget.setPosition(x, this.modeButton.getBottom() + 4);
        this.minLayerEditWidget.setPosition(x, this.maxLayerEditWidget.getBottom() + 2);

        this.moveMaxLayerCheckbox.setX(this.maxLayerEditWidget.getRight() + 4);
        this.moveMinLayerCheckbox.setX(this.minLayerEditWidget.getRight() + 4);
        this.moveMaxLayerCheckbox.centerVerticallyInside(this.maxLayerEditWidget);
        this.moveMinLayerCheckbox.centerVerticallyInside(this.minLayerEditWidget);

        if (this.range.getLayerMode() == LayerMode.LAYER_RANGE)
        {
            this.setToPlayerButton.setPosition(this.minLayerEditWidget.getTextFieldWidgetX(),
                                               this.minLayerEditWidget.getBottom() + 2);
        }
        else
        {
            this.setToPlayerButton.setPosition(this.singleLayerEditWidget.getTextFieldWidgetX(),
                                               this.singleLayerEditWidget.getBottom() + 2);
        }

        x = Math.max(this.axisButton.getRight(), this.moveMinLayerCheckbox.getRight());
        this.followPlayerButton.setPosition(x + 10, y);
        this.followOffsetEditWidget.setPosition(this.followPlayerButton.getX() + 2, this.followPlayerButton.getBottom() + 4);
    }

    @Override
    public void updateWidgetState()
    {
        this.setLayerValues();
        this.updateSize();
    }

    @Override
    protected int getRequestedContentWidth()
    {
        return this.followOffsetEditWidget.getRight() - this.getX();
    }

    @Override
    protected int getRequestedContentHeight()
    {
        return this.setToPlayerButton.getBottom() - this.getY();
    }

    public void setAddPlayerFollowingOptions(boolean addPlayerFollowingOptions)
    {
        this.addPlayerFollowingOptions = addPlayerFollowingOptions;
    }

    public void setAddLayerRangeHotkeyCheckboxes(boolean addLayerRangeHotkeyCheckboxes)
    {
        this.addLayerRangeHotkeyCheckboxes = addLayerRangeHotkeyCheckboxes;
    }

    protected void setFollowOffset(int offset)
    {
        this.range.setPlayerFollowOffset(offset);
    }

    protected void setToPlayer()
    {
        this.range.setToPosition(GameWrap.getClientPlayer());
        this.setLayerValues();
    }

    protected void toggleShouldFollowPlayer()
    {
        this.range.toggleShouldFollowPlayer();
        this.reAddSubWidgets();
        this.updateSubWidgetPositions();
    }

    protected boolean cycleMode(int mouseButton, GenericButton button)
    {
        this.range.cycleLayerMode(mouseButton == 1);
        this.updateWidgetState();
        this.reAddSubWidgets();
        this.updateSubWidgetPositions();
        return true;
    }

    protected boolean cycleAxis(int mouseButton, GenericButton button)
    {
        this.range.cycleAxis(mouseButton == 1);
        return true;
    }

    protected void setLayerValues()
    {
        this.singleLayerEditWidget.setIntegerValue(this.range.getCurrentLayerValue(false));
        this.minLayerEditWidget.setIntegerValue(this.range.getLayerRangeMin());
        this.maxLayerEditWidget.setIntegerValue(this.range.getLayerRangeMax());
    }

    protected String getModeButtonLabel()
    {
        return StringUtils.translate("malilib.button.layer_range.layers", this.range.getLayerMode().getDisplayName());
    }

    protected String getAxisButtonLabel()
    {
        return StringUtils.translate("malilib.button.layer_range.axis", this.range.getAxis().name());
    }
}

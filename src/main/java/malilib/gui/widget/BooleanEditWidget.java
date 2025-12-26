package malilib.gui.widget;

import malilib.gui.widget.button.BooleanConfigButton;
import malilib.util.data.BooleanStorageWithDefault;
import malilib.util.data.LeftRight;

public class BooleanEditWidget extends ContainerWidget
{
    protected final BooleanStorageWithDefault storage;
    protected final BooleanConfigButton button;
    protected final LabelWidget label;
    protected LeftRight buttonPosition;
    protected boolean useSeparateColorForModifiedLabel = true;
    protected int buttonXOffset;
    protected int disabledWidgetLabelColor = 0xFF505050;
    protected int normalOnLabelColor = 0xFFF0F0F0;
    protected int normalOffLabelColor = 0xFFD0D0D0;
    protected int modifiedLabelColor = 0xFFD050D0;

    public BooleanEditWidget(int height, BooleanStorageWithDefault storage, String labelKey)
    {
        super(-1, height);

        this.storage = storage;
        this.label = new LabelWidget(labelKey);

        this.button = new BooleanConfigButton(-1, height, storage);
        this.button.blockHoverContentFromBelow = false;

        this.button.setClickListener(this::onButtonClicked);
        this.label.setClickListener(this::onClick);
        this.setClickListener(this::onClick);

        this.setButtonPosition(LeftRight.LEFT);
        this.updateLabelColor();
    }

    public BooleanEditWidget(int height, BooleanStorageWithDefault storage, String labelKey, String commentKey)
    {
        this(height, storage, labelKey);

        this.label.translateAndAddHoverString(commentKey);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.button);
        this.addWidget(this.label);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        if (this.buttonPosition == LeftRight.LEFT)
        {
            this.button.setPosition(x, y);
            this.label.setX(this.button.getRight() + 4);
        }
        else
        {
            this.button.setPosition(x + this.buttonXOffset, y);
            this.label.setX(x);
        }

        this.label.centerVerticallyInside(this, 1);
        this.updateLabelColor();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width;

            if (this.buttonPosition == LeftRight.LEFT)
            {
                width = this.label.getWidth() + this.button.getWidth() + 4;
            }
            else
            {
                width = this.buttonXOffset + this.button.getWidth();
            }

            this.setWidthNoUpdate(width);
        }
    }

    public void updateLabelColor()
    {
        if (this.enabled == false)
        {
            this.label.setNormalTextColor(this.disabledWidgetLabelColor);
            this.label.setHoverTextColor(this.disabledWidgetLabelColor);
        }
        else
        {
            int color;

            if (this.useSeparateColorForModifiedLabel && this.storage.isModified())
            {
                color = this.modifiedLabelColor;
            }
            else
            {
                color = this.storage.getBooleanValue() ? this.normalOnLabelColor : this.normalOffLabelColor;
            }

            this.label.setNormalTextColor(color);
            this.label.setHoverTextColor(color);
        }
    }

    public LabelWidget getLabelWidget()
    {
        return this.label;
    }

    public BooleanConfigButton getButton()
    {
        return this.button;
    }

    public void setShowAsOffIfDisabled(boolean showAsOffIfDisabled)
    {
        this.button.setShowAsOffIfDisabled(showAsOffIfDisabled);
    }

    public void setUseSeparateColorForModifiedLabel(boolean useSeparateColorForModifiedLabel)
    {
        this.useSeparateColorForModifiedLabel = useSeparateColorForModifiedLabel;
    }

    public void setDisabledWidgetLabelColor(int color)
    {
        this.disabledWidgetLabelColor = color;
        this.updateLabelColor();
    }

    public void setNormalStateOnAndOffLabelColor(int color)
    {
        this.normalOnLabelColor = color;
        this.normalOffLabelColor = color;
        this.updateLabelColor();
    }

    public void setNormalStateOnLabelColor(int color)
    {
        this.normalOnLabelColor = color;
        this.updateLabelColor();
    }

    public void setNormalStateOffLabelColor(int color)
    {
        this.normalOffLabelColor = color;
        this.updateLabelColor();
    }

    public void setModifiedStateLabelColor(int color)
    {
        this.modifiedLabelColor = color;
        this.updateLabelColor();
    }

    public void setButtonPosition(LeftRight buttonPosition)
    {
        this.buttonPosition = buttonPosition;

        if (buttonPosition == LeftRight.LEFT)
        {
            this.textOffset.setXOffset(this.button.getWidth() + 4);
        }
        else
        {
            this.textOffset.setXOffset(0);
        }

        this.updateWidth();
    }

    public void setButtonXOffset(int buttonOffsetX)
    {
        this.buttonXOffset = buttonOffsetX;
        this.updateWidth();
    }

    protected void onClick()
    {
        this.storage.toggleBooleanValue();
        this.updateLabelColor();
    }

    protected void onButtonClicked()
    {
        this.updateLabelColor();
    }
}

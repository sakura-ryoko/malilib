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
    protected int disabledLabelColor = 0xFF505050;
    protected int normalLabelColor = 0xFFFFFFFF;
    protected int modifiedLabelColor = 0xFFFF00FF;

    public BooleanEditWidget(int height, BooleanStorageWithDefault storage, String labelKey)
    {
        super(-1, height);

        this.storage = storage;
        this.label = new LabelWidget(labelKey);
        this.button = new BooleanConfigButton(-1, height, storage);
        this.button.setClickListener(this::onButtonClicked);
        this.button.blockHoverContentFromBelow = false;

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
            this.label.setNormalTextColor(this.disabledLabelColor);
            this.label.setHoverTextColor(this.disabledLabelColor);
        }
        else
        {
            boolean useModifiedColor = this.useSeparateColorForModifiedLabel && this.storage.isModified();
            int color = useModifiedColor ? this.modifiedLabelColor : this.normalLabelColor;
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

    public void setUseSeparateColorForModifiedLabel(boolean useSeparateColorForModifiedLabel)
    {
        this.useSeparateColorForModifiedLabel = useSeparateColorForModifiedLabel;
    }

    public void setDisabledLabelColor(int color)
    {
        this.disabledLabelColor = color;
        this.updateLabelColor();
    }

    public void setNormalStateLabelColor(int color)
    {
        this.normalLabelColor = color;
        this.updateLabelColor();
    }

    public void setLabelColors(int normalColor, int modifiedColor)
    {
        this.normalLabelColor = normalColor;
        this.modifiedLabelColor = modifiedColor;
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

    protected void onButtonClicked()
    {
        this.updateLabelColor();

        if (this.clickListener != null)
        {
            this.clickListener.onEvent();
        }
    }
}

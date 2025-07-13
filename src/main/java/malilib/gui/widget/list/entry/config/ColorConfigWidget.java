package malilib.gui.widget.list.entry.config;

import malilib.config.option.ColorConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ColorIndicatorWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.data.Color4f;

public class ColorConfigWidget extends BaseGenericConfigWidget<Color4f, ColorConfig>
{
    protected final InteractableWidget colorIndicatorWidget;
    protected final BaseTextFieldWidget textField;
    protected final String initialStringValue;

    public ColorConfigWidget(ColorConfig config,
                             DataListEntryWidgetData constructData,
                             ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialStringValue = this.initialValue.toString();
        this.colorIndicatorWidget = this.createColorIndicatorWidget();

        this.textField = new BaseTextFieldWidget(70, 16, this.config.getStringValue());
        this.textField.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.colorIndicatorWidget);
        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY();

        this.colorIndicatorWidget.setPosition(x, y + 2);

        this.textField.setWidth(this.getElementWidth() - 22);
        this.textField.setPosition(this.colorIndicatorWidget.getRight() + 4, y + 3);
        this.textField.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.textField.getRight() + 4, y + 1);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField.setTextNoNotify(this.config.getStringValue());
        this.textField.updateHoverStrings();
        this.colorIndicatorWidget.updateHoverStrings();
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialStringValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }

    protected void onColorSet(int newColor)
    {
        this.config.setValueFromInt(newColor);
        this.updateWidgetState();
    }

    protected InteractableWidget createColorIndicatorWidget()
    {
        InteractableWidget widget = new ColorIndicatorWidget(18, 18, this.config, this::onColorSet);
        widget.getHoverInfoFactory().setStringListProvider("locked", this.config::getLockAndOverrideMessages, 110);
        return widget;
    }
}

package malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import malilib.util.position.Coordinate;

public abstract class BaseDualNumberEditWidget<T, W extends BaseNumberEditWidget> extends ContainerWidget
{
    protected final Consumer<T> posConsumer;
    protected final W xCoordinateWidget;
    protected final W yCoordinateWidget;
    protected final boolean vertical;
    protected final int gap;
    protected T pos;

    public BaseDualNumberEditWidget(int width, int height,
                                    int gap, boolean vertical,
                                    T initialValue,
                                    Consumer<T> valueConsumer)
    {
        super(width, height);

        this.posConsumer = valueConsumer;
        this.pos = initialValue;
        this.gap = gap;
        this.vertical = vertical;

        int w = vertical ? width : (width - gap) / 2;
        int h = vertical ? (height - gap) / 2 : height;
        this.xCoordinateWidget = this.createNumberEditWidget(w, h, initialValue, Coordinate.X);
        this.yCoordinateWidget = this.createNumberEditWidget(w, h, initialValue, Coordinate.Y);

        this.xCoordinateWidget.setLabelText("malilib.label.misc.coordinate.x_label");
        this.yCoordinateWidget.setLabelText("malilib.label.misc.coordinate.y_label");

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.xCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.yCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.xCoordinateWidget);
        this.addWidget(this.yCoordinateWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        int width = this.getWidth();
        int height = this.getHeight();
        int w = this.vertical ? width : (width - this.gap) / 2;
        int h = this.vertical ? (height - this.gap) / 2 : height;
        this.xCoordinateWidget.setSize(w, h);
        this.yCoordinateWidget.setSize(w, h);

        this.xCoordinateWidget.setPosition(x, y);

        if (this.vertical)
        {
            this.yCoordinateWidget.setPosition(x, this.xCoordinateWidget.getBottom() + this.gap);
        }
        else
        {
            this.yCoordinateWidget.setPosition(this.xCoordinateWidget.getRight() + this.gap, y);
        }
    }

    protected abstract W createNumberEditWidget(int width, int height, T initialPos, Coordinate coord);

    public void setLabels(String xTranslationKey, String yTranslationKey)
    {
        this.xCoordinateWidget.setLabelText(xTranslationKey);
        this.yCoordinateWidget.setLabelText(yTranslationKey);
    }

    public void setHoverTexts(String xTranslationKey, String yTranslationKey)
    {
        this.xCoordinateWidget.getTextField().translateAndAddHoverString(xTranslationKey);
        this.yCoordinateWidget.getTextField().translateAndAddHoverString(yTranslationKey);
    }

    protected void setValue(T pos)
    {
        this.pos = pos;
        this.posConsumer.accept(pos);
    }

    public void setValueAndUpdate(T pos)
    {
        this.setValue(pos);
        this.updateWidgetState();
    }
}

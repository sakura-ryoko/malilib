package malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import malilib.gui.widget.button.GenericButton;
import malilib.util.position.Coordinate;

public abstract class BaseTripleNumberEditWidget<T, W extends BaseNumberEditWidget> extends ContainerWidget
{
    protected final Consumer<T> posConsumer;
    protected final W xCoordinateWidget;
    protected final W yCoordinateWidget;
    protected final W zCoordinateWidget;
    protected final GenericButton moveToPlayerButton;
    protected T pos;
    protected int gap;
    protected boolean addMoveToPlayerButton;

    public BaseTripleNumberEditWidget(int width, int height, int gap,
                                      boolean addMoveToPlayerButton,
                                      T initialPos,
                                      Consumer<T> posConsumer)
    {
        super(width, height);

        this.posConsumer = posConsumer;
        this.pos = initialPos;
        this.gap = gap;
        this.addMoveToPlayerButton = addMoveToPlayerButton;

        int h = addMoveToPlayerButton ? (height - gap * 3 - 18) / 3 : (height - gap * 2) / 3;
        this.moveToPlayerButton = GenericButton.create(18, "malilib.button.layer_range.set_to_player");
        this.moveToPlayerButton.setActionListener(this::moveToPlayer);

        this.xCoordinateWidget = this.createNumberEditWidget(width, h, initialPos, Coordinate.X);
        this.yCoordinateWidget = this.createNumberEditWidget(width, h, initialPos, Coordinate.Y);
        this.zCoordinateWidget = this.createNumberEditWidget(width, h, initialPos, Coordinate.Z);

        this.xCoordinateWidget.setLabelText("malilib.label.misc.coordinate.x_label");
        this.yCoordinateWidget.setLabelText("malilib.label.misc.coordinate.y_label");
        this.zCoordinateWidget.setLabelText("malilib.label.misc.coordinate.z_label");

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.moveToPlayerButton.setEnabledStatusSupplier(enabledSupplier);
        this.xCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.yCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.zCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.xCoordinateWidget);
        this.addWidget(this.yCoordinateWidget);
        this.addWidget(this.zCoordinateWidget);
        this.addWidgetIf(this.moveToPlayerButton, this.addMoveToPlayerButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();

        this.xCoordinateWidget.setPosition(x, this.getY());
        this.yCoordinateWidget.setPosition(x, this.xCoordinateWidget.getBottom() + this.gap);
        this.zCoordinateWidget.setPosition(x, this.yCoordinateWidget.getBottom() + this.gap);
        this.moveToPlayerButton.setPosition(this.zCoordinateWidget.getTextFieldWidgetX(),
                                            this.zCoordinateWidget.getBottom() + this.gap);
    }

    protected abstract W createNumberEditWidget(int width, int height, T initialPos, Coordinate coord);

    public int getTextFieldStartX()
    {
        return this.xCoordinateWidget.getTextFieldWidgetX();
    }

    public void setAddMoveToPlayerButton(boolean addMoveToPlayerButton)
    {
        this.addMoveToPlayerButton = addMoveToPlayerButton;
    }

    public void setLabels(String xTranslationKey, String yTranslationKey, String zTranslationKey)
    {
        this.xCoordinateWidget.setLabelText(xTranslationKey);
        this.yCoordinateWidget.setLabelText(yTranslationKey);
        this.zCoordinateWidget.setLabelText(zTranslationKey);
    }

    public void setUpdateImmediateFromTextInput(boolean updateImmediately)
    {
        this.xCoordinateWidget.getTextField().setUpdateListenerAlways(updateImmediately);
        this.yCoordinateWidget.getTextField().setUpdateListenerAlways(updateImmediately);
        this.zCoordinateWidget.getTextField().setUpdateListenerAlways(updateImmediately);
    }

    protected void moveToPlayer()
    {
        this.setPos(this.getPositionFromPlayer());
        this.updateWidgetState();
    }

    protected abstract T getPositionFromPlayer();

    protected void setPos(T pos)
    {
        this.pos = pos;
        this.posConsumer.accept(pos);
    }

    public void setPosNoUpdate(T pos)
    {
        this.pos = pos;
        this.updateWidgetState();
    }

    public void setPosAndUpdate(T pos)
    {
        this.setPos(pos);
        this.updateWidgetState();
    }
}

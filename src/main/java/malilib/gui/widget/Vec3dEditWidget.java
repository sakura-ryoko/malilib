package malilib.gui.widget;

import java.util.function.Consumer;

import malilib.util.game.wrap.EntityWrap;
import malilib.util.position.Coordinate;
import malilib.util.position.Vec3d;

public class Vec3dEditWidget extends BaseTripleNumberEditWidget<Vec3d, DoubleEditWidget>
{
    public Vec3dEditWidget(int width, int height, int gap,
                           boolean addMoveToPlayerButton,
                           Vec3d initialPos,
                           Consumer<Vec3d> posConsumer)
    {
        super(width, height, gap, addMoveToPlayerButton, initialPos, posConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        Vec3d pos = this.pos;
        this.xCoordinateWidget.setDoubleValue(pos.x);
        this.yCoordinateWidget.setDoubleValue(pos.y);
        this.zCoordinateWidget.setDoubleValue(pos.z);
    }

    public void setValidRange(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
        this.zCoordinateWidget.setValidRange(minZ, maxZ);
    }

    @Override
    protected DoubleEditWidget createNumberEditWidget(int width, int height, Vec3d initialPos, Coordinate coord)
    {
        return new CoordinateEditWidget(width, height,
                                        coord.asDouble(initialPos),
                                        -31000000.0, 31000000.0,
                                        (val) -> this.setPos(val, coord));
    }

    @Override
    protected Vec3d getPositionFromPlayer()
    {
        return EntityWrap.getCameraEntityPosition();
    }

    protected void setPos(double newVal, Coordinate coordinate)
    {
        this.setPos(coordinate.modifyVec3d(newVal, this.pos));
    }
}

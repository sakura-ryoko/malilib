package malilib.gui.action;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;

import malilib.gui.BaseScreen;
import malilib.gui.util.BackgroundSettings;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.StyledText;
import malilib.util.MathUtils;
import malilib.util.data.EdgeInt;
import malilib.util.data.json.JsonUtils;
import malilib.util.position.SectorEdge;
import malilib.util.position.Vec2i;

public class RadialActionExecutionWidget extends BaseActionExecutionWidget
{
    protected Vec2i center = Vec2i.ZERO;
    @Nullable SectorEdge draggedEdge;
    protected double outerRadius;
    protected double innerRadius;
    protected double startAngle;
    protected double endAngle;

    public RadialActionExecutionWidget()
    {
        super();

        this.setInnerRadius(30.0);
        this.setOuterRadius(60.0);
        this.setStartAngle(0.5);
        this.setEndAngle(1.2);

        this.getBorderRenderer().getNormalSettings().setBorderWidth(2);
        this.getBorderRenderer().getHoverSettings().setBorderWidth(5);
        this.editedBorderSettings.setBorderWidth(5);
    }

    @Override
    protected Type getType()
    {
        return Type.RADIAL;
    }

    public void setCenter(Vec2i center)
    {
        this.center = center;
    }

    @Override
    public void onAdded(BaseScreen screen)
    {
        super.onAdded(screen);

        int x = screen.getX() + screen.getScreenWidth() / 2;
        int y = screen.getY() + screen.getScreenHeight() / 2;

        this.setCenter(new Vec2i(x, y));
        this.updatePosition();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        double distance = this.center.getDistanceTo(mouseX, mouseY);

        if (distance >= this.innerRadius && distance <= this.outerRadius)
        {
            double mouseAngle = this.getMouseAngle(mouseX, mouseY);
            double start = this.startAngle;
            double end = this.endAngle;

            if (end > start)
            {
                return mouseAngle >= start && mouseAngle <= end;
            }
            else
            {
                return mouseAngle >= start || mouseAngle <= end;
            }
        }

        return false;
    }

    @Override
    public boolean intersects(EdgeInt rectangle)
    {
        int x = this.getX();
        int y = this.getY();

        return x >= rectangle.getLeft() &&
               x <= rectangle.getRight() &&
               y >= rectangle.getTop() &&
               y <= rectangle.getBottom();
    }

    @Override
    protected void startResize(int mouseX, int mouseY)
    {
        this.draggedEdge = SectorEdge.getClosestEdge(mouseX, mouseY, this.center.x, this.center.y,
                                                     this.innerRadius, this.outerRadius,
                                                     this.startAngle, this.endAngle);
        this.resizing = true;
        this.notifyChange();
    }

    @Override
    public void moveWidget(int mouseX, int mouseY)
    {
        double radiusChange = this.getMouseRadius(mouseX, mouseY) - this.getMiddleRadius();

        this.setInnerRadius(this.innerRadius + radiusChange);
        this.setOuterRadius(this.outerRadius + radiusChange);

        double sectorWidth = this.getSectorWidth();
        double mouseAngle = this.gridSnapValue(this.getMouseAngle(mouseX, mouseY), 128.0);
        //System.out.printf("mouse: %.4f, middle: %.4f, change: %.4f\n", mouseAngle, middleAngle, angleChange);

        this.setStartAngle(mouseAngle - sectorWidth / 2.0);
        this.setEndAngle(mouseAngle + sectorWidth / 2.0);
    }

    @Override
    protected void resizeWidget(int mouseX, int mouseY)
    {
        if (this.draggedEdge == null)
        {
            return;
        }

        if (this.draggedEdge.isRadius())
        {
            double mouseRadius = this.getMouseRadius(mouseX, mouseY);

            if (this.draggedEdge == SectorEdge.INNER_RING)
            {
                this.setInnerRadius(mouseRadius);
            }
            else
            {
                this.setOuterRadius(mouseRadius);
            }
        }
        else
        {
            double mouseAngle = this.getMouseAngle(mouseX, mouseY);
            mouseAngle = this.gridSnapValue(mouseAngle, 128.0);

            if (this.draggedEdge == SectorEdge.START_ANGLE)
            {
                this.setStartAngle(mouseAngle);
            }
            else if (this.draggedEdge == SectorEdge.END_ANGLE)
            {
                this.setEndAngle(mouseAngle);
            }
        }
    }

    public void setInnerRadius(double innerRadius)
    {
        innerRadius = this.gridSnapValue(innerRadius, 1.0);
        this.innerRadius = Math.max(innerRadius, 1);
        this.outerRadius = Math.max(this.innerRadius + 4, this.outerRadius);
        this.updatePosition();
    }

    public void setOuterRadius(double outerRadius)
    {
        outerRadius = this.gridSnapValue(outerRadius, 1.0);
        this.outerRadius = Math.max(outerRadius, 2);
        this.innerRadius = Math.max(Math.min(this.outerRadius - 4, this.innerRadius), 1);
        this.updatePosition();
    }

    public void setStartAngle(double startAngle)
    {
        this.startAngle = MathUtils.wrapRadianAngle(startAngle);
        this.updatePosition();
    }

    public void setEndAngle(double endAngle)
    {
        this.endAngle = MathUtils.wrapRadianAngle(endAngle);
        this.updatePosition();
    }

    protected double getMouseAngle(int mouseX, int mouseY)
    {
        // The zero angle will be on the middle right, because the coordinates are flipped here
        double diffX = this.center.x - mouseX;
        double diffY = this.center.y - mouseY;
        // Change from -pi .. pi to 0 .. 2 * pi.
        double angle = Math.atan2(diffY, diffX) + Math.PI;

        return MathUtils.wrapRadianAngle(angle);
    }

    protected double getMouseRadius(int mouseX, int mouseY)
    {
        double centerX = this.center.x;
        double centerY = this.center.y;
        double distX = mouseX - centerX;
        double distY = mouseY - centerY;

        return Math.sqrt(distX * distX + distY * distY);
    }

    protected double getMiddleAngle()
    {
        double start = this.startAngle;
        double end = this.endAngle;
        double twoPi = 2 * Math.PI;
        double angle;

        if (end > start)
        {
            angle = (start + end) / 2.0;
        }
        else
        {
            angle = start + (twoPi - (start - end)) / 2.0;
        }

        return MathUtils.wrapRadianAngle(angle);
    }

    protected double getSectorWidth()
    {
        return MathUtils.wrapRadianAngle(this.endAngle - this.startAngle);
    }

    protected double getMiddleRadius()
    {
        return (this.innerRadius + this.outerRadius) / 2.0;
    }

    protected double gridSnapValue(double value, double divider)
    {
        int gridSize = this.getGridSize();

        if (gridSize >= 1 && divider > 0)
        {
            value = MathUtils.roundUp(value, gridSize / divider);
        }

        return value;
    }

    protected void updatePosition()
    {
        double radius = this.gridSnapValue(this.getMiddleRadius(), 128.0);
        double angle  = this.gridSnapValue(this.getMiddleAngle(), 128.0);

        this.setX(this.center.x + (int) (Math.cos(angle) * radius));
        this.setY(this.center.y + (int) (Math.sin(angle) * radius));
    }

    @Override
    protected void renderWidgetBackgroundAndBorder(int x, int y, float z, ScreenContext ctx)
    {
        double centerX = this.center.x;
        double centerY = this.center.y;
        boolean hovered = this.isHoveredForRender(ctx);
        BorderSettings borderSettings = this.getActiveBorderSettings(ctx);
        BackgroundSettings backgroundSettings = this.getBackgroundRenderer().getActiveSettings(hovered);
        int borderColor = borderSettings.getColor().getTop();
        int borderWidth = borderSettings.getActiveBorderWidth();
        int bgColor = backgroundSettings.getColor();

        if (hovered)
        {
            z += 0.0125f;
        }

        ShapeRenderUtils.renderSectorFill(centerX, centerY, z, this.innerRadius, this.outerRadius,
                                          this.startAngle, this.endAngle, bgColor, ctx);

        ShapeRenderUtils.renderSectorOutline(centerX, centerY, z, this.innerRadius, this.outerRadius,
                                             this.startAngle, this.endAngle, borderWidth, borderColor, ctx);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.resizing || this.dragging)
        {
            StyledText text = StyledText.parse(String.format("r: %.2f ... %.2f\\n%.4f rad\\n%.4f rad => %.4f rad",
                                                             this.innerRadius, this.outerRadius,
                                                             this.getSectorWidth(),
                                                             this.startAngle, this.endAngle));

            double middleAngle = this.getMiddleAngle();
            int tx = (int) (this.center.x + Math.cos(middleAngle) * (this.outerRadius + 10));
            int ty = (int) (this.center.y + Math.sin(middleAngle) * (this.outerRadius + 10));

            if (middleAngle >= Math.PI / 2 && middleAngle <= Math.PI * 3 / 2)
            {
                tx -= 120;
            }

            this.textRenderer.renderText(tx, ty, z + 0.25f, 0xFFFFFFFF, true, text, ctx);
        }

        super.renderAt(x, y, z, ctx);
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("min_r", this.innerRadius);
        obj.addProperty("max_r", this.outerRadius);
        obj.addProperty("start_angle", this.startAngle);
        obj.addProperty("end_angle", this.endAngle);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.setInnerRadius(JsonUtils.getDoubleOrDefault(obj, "min_r", this.innerRadius));
        this.setOuterRadius(JsonUtils.getDoubleOrDefault(obj, "max_r", this.outerRadius));
        this.setStartAngle(JsonUtils.getDoubleOrDefault(obj, "start_angle", this.startAngle));
        this.setEndAngle(JsonUtils.getDoubleOrDefault(obj, "end_angle", this.endAngle));
    }
}

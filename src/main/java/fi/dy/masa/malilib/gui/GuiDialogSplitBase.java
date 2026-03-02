package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.MathUtils;

public class GuiDialogSplitBase extends GuiBase
{
    protected int dialogTotalWidth;
    protected int dialogTotalHeight;
    protected int dialogLeft;
    protected int dialogRight;
    protected int dialogTop;
    protected int dialogBottom;
    protected int dialogCenter;
    protected int dialogLeftSideWidth;
    protected int dialogRightSideWidth;
    protected int dialogLeftSideCenter;
    protected int dialogRightSideCenter;

    public void setTotalWidthAndHeight(int width, int height)
    {
        this.dialogTotalWidth = width;
        this.dialogTotalHeight = height;
        this.dialogLeftSideWidth = width / 2;
        this.dialogRightSideWidth = width / 2;
        this.dialogRight = this.dialogLeft + this.dialogTotalWidth;
        this.dialogBottom = this.dialogTop + this.dialogTotalHeight;
        this.recalculateCenters();
    }

    public void setLeftSideWidth(int leftSideWidth)
    {
        leftSideWidth = MathUtils.clamp(leftSideWidth, 16, this.dialogTotalWidth);
        this.dialogLeftSideWidth = leftSideWidth;
        this.dialogRightSideWidth = this.dialogTotalWidth - leftSideWidth;
        this.recalculateCenters();
    }

    public void setRightSideWidth(int rightSideWidth)
    {
        rightSideWidth = MathUtils.clamp(rightSideWidth, 16, this.dialogTotalWidth);
        this.dialogRightSideWidth = rightSideWidth;
        this.dialogLeftSideWidth = this.dialogTotalWidth - rightSideWidth;
        this.recalculateCenters();
    }

    public void setPosition(int left, int top)
    {
        this.dialogLeft = left;
        this.dialogRight = left + this.dialogTotalWidth;
        this.dialogTop = top;
        this.dialogBottom = top + this.dialogTotalHeight;
        this.recalculateCenters();
    }

    public void centerOnScreen()
    {
        int left;
        int top;

        if (this.getParent() != null)
        {
            left = this.getParent().width / 2 - this.dialogTotalWidth / 2;
            top = this.getParent().height / 2 - this.dialogTotalHeight / 2;
        }
        else
        {
            left = GuiUtils.getScaledWindowWidth() / 2 - this.dialogTotalWidth / 2;
            top = GuiUtils.getScaledWindowHeight() / 2 - this.dialogTotalHeight / 2;
        }

        this.setPosition(left, top);
    }

    protected int getScaledCenterX()
    {
        if (this.getParent() != null)
        {
            return this.getParent().width / 2;
        }
        else
        {
            return GuiUtils.getScaledWindowWidth() / 2;
        }
    }

    protected int getScaledCenterY()
    {
        if (this.getParent() != null)
        {
            return this.getParent().height / 2;
        }
        else
        {
            return GuiUtils.getScaledWindowHeight() / 2;
        }
    }

    // Keeps these scaled to the correct dialogCenter
    protected void recalculateCenters()
    {
        this.dialogCenter = this.getScaledCenterX();

        if (this.dialogLeftSideWidth <= this.dialogRightSideWidth)
        {
            this.dialogCenter -= (this.dialogLeftSideWidth / 2);
        }
        else
        {
            this.dialogCenter += (this.dialogRightSideWidth / 2);
        }

        this.dialogLeftSideCenter = this.dialogLeft + (this.dialogLeftSideWidth / 2);
        this.dialogRightSideCenter = this.dialogRight - (this.dialogRightSideWidth / 2);
    }

    // Divider "T" Bars
    protected void drawDividerBars(GuiContext ctx, int mouseX, int mouseY)
    {
        RenderUtils.drawRect(ctx, this.dialogLeft, this.dialogTop + 16, this.dialogTotalWidth, 1, COLOR_HORIZONTAL_BAR);
        RenderUtils.drawRect(ctx, this.dialogCenter, this.dialogTop + 16, 1, this.dialogTotalHeight - 16, COLOR_HORIZONTAL_BAR);
    }
}

package fi.dy.masa.malilib.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;

public class GuiTextFieldGeneric extends TextFieldWidget {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zLevel;

    public GuiTextFieldGeneric(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(textRenderer, x, y, width, height, ScreenTexts.EMPTY);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setMaxLength(256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.isMouseOver((int) mouseX, (int) mouseY)) {
            if (mouseButton == 1) {
                this.setText("");
            }

            this.setFocused(true);

            return true;
        } else {
            this.setFocused(false);
        }

        return ret;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= this.y && mouseY < this.y + this.height;
    }

    // Make public
    @Override
    public void setFocused(boolean isFocusedIn) {
        super.setFocused(isFocusedIn);
    }

    public int getCursorPosition() {
        return this.getCursor();
    }

    public void setCursorPosition(int pos) {
        this.setCursor(pos, GuiBase.isShiftDown());
    }

    public void setCursorPositionZero() {
        this.setCursorToStart(GuiBase.isShiftDown());
    }

    public void setCursorPositionEnd() {
        this.setCursorToEnd(GuiBase.isShiftDown());
    }

    public GuiTextFieldGeneric setZLevel(int zLevel) {
        this.zLevel = zLevel;
        return this;
    }

    // @Override
    public void renderZ(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        if (this.zLevel != 0) {
            MatrixStack matrixStack = drawContext.getMatrices();
            matrixStack.push();
            matrixStack.translate(0, 0, this.zLevel);

            super.render(drawContext, mouseX, mouseY, partialTicks);

            matrixStack.pop();
        } else {
            super.render(drawContext, mouseX, mouseY, partialTicks);
        }
    }
}

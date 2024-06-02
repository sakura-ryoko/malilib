package fi.dy.masa.malilib.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import fi.dy.masa.malilib.util.InfoUtils;

public class GuiTextFieldGeneric extends TextFieldWidget
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GuiTextFieldGeneric(int x, int y, int width, int height, TextRenderer textRenderer)
    {
        super(textRenderer, x, y, width, height, new LiteralText(""));

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setMaxLength(256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1 && this.isMouseOver((int) mouseX, (int) mouseY))
        {
            this.setText("");
            this.setFocused(true);
            return true;
        }

        return ret;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    @Override
    public void setFocused(boolean isFocusedIn)
    {
        boolean wasFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (this.isFocused() != wasFocused)
        {
            MinecraftClient.getInstance().keyboard.setRepeatEvents(this.isFocused());
        }
    }

    // Don't call this method, call the vanilla method directly
    @Deprecated
    public int getCursorPosition()
    {
        InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "malilib 0.18.2 deprecated the getCursorPosition() method. Update the mod that owns the GUI where this warning happens to the latest version.");
        return 0;
    }

    // Don't call this method, call the vanilla method directly
    @Deprecated
    public void setCursorPosition(int pos)
    {
        InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "malilib 0.18.2 deprecated the setCursorPosition() method. Update the mod that owns the GUI where this warning happens to the latest version.");
    }

    // Don't call this method, call the vanilla method directly
    @Deprecated
    public void setCursorPositionZero()
    {
        InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "malilib 0.18.2 deprecated the setCursorPositionZero() method. Update the mod that owns the GUI where this warning happens to the latest version.");
    }

    // Don't call this method, call the vanilla method directly
    @Deprecated
    public void setCursorPositionEnd()
    {
        InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "malilib 0.18.2 deprecated the setCursorPositionEnd() method. Update the mod that owns the GUI where this warning happens to the latest version.");
    }

    public GuiTextFieldGeneric setZLevel(int zLevel)
    {
        this.setZOffset(zLevel);
        return this;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getZOffset() != 0)
        {
            matrixStack.push();
            matrixStack.translate(0, 0, this.getZOffset());

            super.render(matrixStack, mouseX, mouseY, partialTicks);

            matrixStack.pop();
        }
        else
        {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
}

package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.Message.MessageType;

public class MessageRenderer
{
    private final List<Message> messages = new ArrayList<>();
    private MessageType nextMessageType = MessageType.INFO;
    private boolean useBackground = true;
    private boolean useBorder = true;
    private int backgroundColor;
    private int borderColor;
    private int messageBoxWidth = 400;
    private boolean centeredH = true;
    private boolean centeredV = true;
    private boolean expandUp;
    private float zLevel;

    protected static final Identifier BG_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");

    public MessageRenderer(int bgColor, int borderColor)
    {
        this.setBackgroundColors(bgColor, borderColor);
    }

    public MessageRenderer setBackgroundStyle(boolean useBackground, boolean useBorder)
    {
        this.useBackground = useBackground;
        this.useBorder = useBorder;
        return this;
    }

    public MessageRenderer setBackgroundColors(int bgColor, int borderColor)
    {
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        return this;
    }

    public MessageRenderer setMessageBoxWidth(int width)
    {
        this.messageBoxWidth = width;
        return this;
    }

    /**
     * Sets whether the rendered box should get centered to the given x and y coordinates, or expand
     * to a given direction from that point.
     * If centeredV is false, then the value set in {@link #setExpandUp(boolean)} determines whether the box expands up or down.
     * @param centeredH
     * @param centeredV
     */
    public MessageRenderer setCentered(boolean centeredH, boolean centeredV)
    {
        this.centeredH = centeredH;
        this.centeredV = centeredV;
        return this;
    }

    public MessageRenderer setExpandUp(boolean expandUp)
    {
        this.expandUp = expandUp;
        return this;
    }

    public MessageRenderer setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    public int getMessageBoxWidth()
    {
        return this.messageBoxWidth;
    }

    public MessageRenderer setNextMessageType(MessageType type)
    {
        this.nextMessageType = type;
        return this;
    }

    public MessageType getNextMessageType()
    {
        return this.nextMessageType;
    }

    public void addMessage(int displayTimeMs, String messageKey, Object... args)
    {
        this.addMessage(this.nextMessageType, displayTimeMs, messageKey, args);
    }

    public void addMessage(MessageType type, int displayTimeMs, String messageKey, Object... args)
    {
        this.messages.add(new Message(type, displayTimeMs, this.messageBoxWidth - 20, messageKey, args));
    }

    public int getMessagesHeight()
    {
        int height = 0;

        for (int i = 0; i < this.messages.size(); ++i)
        {
            height += this.messages.get(i).getMessageHeight();
        }

        return height;
    }

    public void drawMessages(int x, int y, DrawContext drawContext)
    {
        if (this.messages.isEmpty() == false)
        {
            int boxWidth = this.messageBoxWidth;
            int boxHeight = this.getMessagesHeight() + 20;

            if (this.centeredH)
            {
                x -= boxWidth / 2;
            }

            if (this.centeredV)
            {
                y -= boxHeight / 2;
            }
            else if (this.expandUp)
            {
                y -= boxHeight;
            }

            if (this.useBackground)
            {
                int bw = this.useBorder ? 1 : 0;
                RenderUtils.drawTexturedRect(BG_TEXTURE, x + bw, y + bw, 0, 0, boxWidth - 2 * bw, boxHeight - 2 * bw, drawContext);
                RenderUtils.drawRect(x + bw, y + bw, boxWidth - 2 * bw, boxHeight - 2 * bw, this.backgroundColor, this.zLevel);
            }

            if (this.useBorder)
            {
                RenderUtils.drawOutline(x, y, boxWidth, boxHeight, this.borderColor, this.zLevel);
            }

            x += 10;
            y += 10;
            long currentTime = System.currentTimeMillis();

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);
                y = message.renderAt(x, y, 0xFFFFFFFF, drawContext);

                if (message.hasExpired(currentTime))
                {
                    this.messages.remove(i);
                    --i;
                }
            }
        }
    }
}

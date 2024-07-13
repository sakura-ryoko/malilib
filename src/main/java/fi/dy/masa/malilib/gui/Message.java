package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.text.Text;

public class Message
{
    private final MessageType type;
    private final long created;
    private final int displayTime;
    private final int maxLineLength;
    private String message;

    public Message(MessageType type, int displayTimeMs, int maxLineLength, String message, Object... args)
    {
        this.type = type;
        this.created = System.currentTimeMillis();
        this.displayTime = displayTimeMs;
        this.maxLineLength = maxLineLength;

        this.message = StringUtils.translate(message, args);
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > (this.created + this.displayTime);
    }

    public int getMessageHeight()
    {
        return MinecraftClient.getInstance().textRenderer.wrapLines(Text.literal(message), maxLineLength).size() * (StringUtils.getFontHeight() + 1) - 1 + 5;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Renders the lines for this message
     * @return the y coordinate of the next message
     */
    public int renderAt(int x, int y, int textColor, DrawContext drawContext)
    {
        String format = this.getFormatCode();

        y = StringUtils.drawStringWrapped(x, y, maxLineLength, textColor, format + message + GuiBase.TXT_RST, drawContext);

        return y + 3;
    }

    public String getFormatCode()
    {
        return this.type.getFormatting();
    }

    public enum MessageType
    {
        INFO        ("malilib.message.formatting_code.info"),
        SUCCESS     ("malilib.message.formatting_code.success"),
        WARNING     ("malilib.message.formatting_code.warning"),
        ERROR       ("malilib.message.formatting_code.error");

        private final String translationKey;

        private MessageType(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getFormatting()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}

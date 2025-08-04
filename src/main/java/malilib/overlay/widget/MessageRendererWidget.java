package malilib.overlay.widget;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.gui.BaseScreen;
import malilib.gui.edit.overlay.MessageRendererWidgetEditScreen;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.overlay.message.Message;
import malilib.overlay.message.MessageDispatcher;
import malilib.render.text.StyledText;
import malilib.util.StringUtils;
import malilib.util.data.json.JsonUtils;

public class MessageRendererWidget extends InfoRendererWidget
{
    protected final List<Message> messages = new ArrayList<>();
    protected int messageGap = 3;
    protected int maxMessages = -1;

    public MessageRendererWidget()
    {
        super();

        this.shouldSerialize = true;
        this.getBackgroundSettings().setEnabled(true);
        this.getBackgroundSettings().setColor(0xF0000000);
        this.getBorderSettings().setEnabled(true);

        this.padding.setAll(6, 8, 6, 8);
        this.setName(StringUtils.translate("malilib.label.misc.default_message_renderer"));
        this.setLineHeight(10);
        this.setMaxWidth(320);
    }

    @Override
    public String getWidgetTypeId()
    {
        return MaLiLibReference.MOD_ID + ":message_renderer";
    }

    @Override
    public boolean isFixedPosition()
    {
        return true;
    }

    @Override
    public void initListEntryWidget(BaseInfoRendererWidgetEntryWidget widget)
    {
        widget.setCanConfigure(true);
        widget.setCanRemove(true);
    }

    @Override
    public void openEditScreen()
    {
        BaseScreen.openScreenWithParent(new MessageRendererWidgetEditScreen(this));
    }

    public void clearMessages()
    {
        this.messages.clear();
        this.updateSizeAndPosition();
    }

    public void addMessage(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        this.addMessage(StyledText.parse(translatedMessage), messageDispatcher);
    }

    public void addMessage(StyledText text, MessageDispatcher messageDispatcher)
    {
        int defaultTextColor = messageDispatcher.getDefaultTextColor();
        int displayTimeMs = messageDispatcher.getDisplayTimeMs();
        int fadeOutTimeMs = messageDispatcher.getFadeOutTimeMs();
        this.addMessage(text, defaultTextColor, displayTimeMs, fadeOutTimeMs);
    }

    public void addMessage(StyledText text, int defaultTextColor, int displayTimeMs, int fadeOutTimeMs)
    {
        if (this.maxMessages > 0 && this.messages.size() >= this.maxMessages)
        {
            this.messages.remove(0);
        }

        int width = this.getMaxMessageWidth();
        this.messages.add(new Message(text, defaultTextColor, displayTimeMs, fadeOutTimeMs, width));
        this.updateSizeAndPosition();
    }

    protected int getMaxMessageWidth()
    {
        int baseWidth = this.automaticWidth ? this.maxWidth : this.getWidth();
        return baseWidth - this.padding.getHorizontalTotal();
    }

    public int getMessageGap()
    {
        return this.messageGap;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    /**
     * Sets the maximum number of concurrent messages to display.
     * Use -1 for no limit.
     */
    public void setMaxMessages(int maxMessages)
    {
        this.maxMessages = maxMessages;
    }

    @Override
    public void onAdded()
    {
        this.updateSizeAndPosition();
    }

    protected void updateSizeAndPosition()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateWidgetPosition();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = 0;

            for (Message msg : this.messages)
            {
                width = Math.max(width, msg.getWidth());
            }

            width += this.padding.getHorizontalTotal();

            // Don't shrink while there are active messages,
            // to prevent an annoying horizontal move of the messages
            if (width > this.getWidth() || this.messages.isEmpty())
            {
                this.setWidth(width);
            }
        }
    }

    @Override
    public void updateHeight()
    {
        this.setHeight(this.getMessagesHeight() + this.padding.getVerticalTotal());
    }

    protected int getMessagesHeight()
    {
        final int messageCount = this.messages.size();

        if (messageCount > 0)
        {
            int height = (messageCount - 1) * this.messageGap;
            int lineHeight = this.getLineHeight();

            for (Message message : this.messages)
            {
                height += message.getLineCount() * lineHeight;
            }

            return height - (lineHeight - this.getFontHeight());
        }

        return 0;
    }

    @Override
    protected void renderTextBackground(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderTextBackground(x, y, z, ctx);
        }
    }

    @Override
    protected void renderWidgetBackground(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderWidgetBackground(x, y, z, ctx);
        }
    }

    @Override
    protected void renderWidgetBorder(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderWidgetBorder(x, y, z, ctx);
        }
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        this.drawMessages(x, y, z, ctx);
    }

    public void drawMessages(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            x += this.padding.getLeft();
            y += this.padding.getTop();

            long currentTime = System.nanoTime();
            int countBefore = this.messages.size();
            int lineHeight = this.getLineHeight();

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);

                if (message.hasExpired(currentTime))
                {
                    this.messages.remove(i);
                    --i;
                }
                else
                {
                    message.renderAt(x, y, z + 0.1f, lineHeight, currentTime, ctx);
                }

                // Always offset the position to prevent a flicker from the later
                // messages jumping over the fading message when it disappears,
                // before the entire widget gets resized and the messages possibly moving
                // (if the widget is bottom-aligned).
                y += message.getLineCount() * lineHeight + this.messageGap;
            }

            if (this.messages.size() != countBefore)
            {
                this.updateSizeAndPosition();
            }
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.addProperty("msg_gap", this.messageGap);
        obj.addProperty("max_messages", this.maxMessages);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.messageGap = JsonUtils.getIntegerOrDefault(obj, "msg_gap", this.messageGap);
        this.maxMessages = JsonUtils.getIntegerOrDefault(obj, "max_messages", this.maxMessages);
    }
}

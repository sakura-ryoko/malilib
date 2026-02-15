package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class GuiTextInputMultiLine extends GuiTextInputMultiLineBase implements ICompletionListener
{
    protected final IStringConsumer consumer;
    protected final IStringConsumerFeedback consumerFeedback;

    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumer consumer)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, Color4f.WHITE, Color4f.WHITE, true, true, true);
    }

    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumer consumer,
                                 Color4f textColor, boolean withShadow)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, textColor, Color4f.WHITE, withShadow, true, true);
    }

    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumer consumer,
                                 Color4f textColor, Color4f cursorColor,
                                 boolean withShadow, boolean withBackground, boolean withDecorations)
    {
        super(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, textColor, cursorColor, withShadow, withBackground, withDecorations);

        this.consumer = consumer;
        this.consumerFeedback = null;
    }

    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, Color4f.WHITE, Color4f.WHITE, true, true, true);
    }
    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer,
                                 Color4f textColor, boolean withShadow)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, textColor, Color4f.WHITE, withShadow, true, true);
    }

    public GuiTextInputMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer,
                                 Color4f textColor, Color4f cursorColor,
                                 boolean withShadow, boolean withBackground, boolean withDecorations)
    {
        super(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, textColor,  cursorColor, withShadow, withBackground, withDecorations);

        this.consumer = null;
        this.consumerFeedback = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        if (this.consumerFeedback != null)
        {
            return this.consumerFeedback.setString(string);
        }

        this.consumer.setString(string);
        return true;
    }

    @Override
    public void onTaskCompleted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskAborted();
        }
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        if (this.getParent() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) this.getParent()).addMessage(type, lifeTime, messageKey, args);
        }
        else
        {
            super.addMessage(type, lifeTime, messageKey, args);
        }
    }
}

package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import fi.dy.masa.malilib.interfaces.IStringDualConsumer;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class GuiTextInputStackedMultiLine extends GuiTextInputStackedMultiLineBase implements ICompletionListener
{
    protected final IStringDualConsumer consumer;
    protected final IStringDualConsumerFeedback consumerFeedback;

    public GuiTextInputStackedMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumer consumer)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, consumer, Color4f.WHITE, true);
    }

    public GuiTextInputStackedMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumer consumer,
                                        Color4f textColor, boolean withShadow)
    {
        super(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, textColor, withShadow);

        this.consumer = consumer;
        this.consumerFeedback = null;
    }

    public GuiTextInputStackedMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer)
    {
        this(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, consumer, Color4f.WHITE, true);
    }

    public GuiTextInputStackedMultiLine(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer,
                                        Color4f textColor, boolean withShadow)
    {
        super(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, textColor, withShadow);

        this.consumer = null;
        this.consumerFeedback = consumer;
    }

    @Override
    protected boolean applyValues(String string1, String string2)
    {
        if (this.consumerFeedback != null)
        {
            return this.consumerFeedback.setStrings(string1, string2);
        }

        this.consumer.setStrings(string1, string2);
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

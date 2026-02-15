package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.*;

@ApiStatus.Experimental
public class GuiTextDualInput extends GuiTextDualInputBase implements ICompletionListener
{
    protected final IStringDualConsumer consumer;
    protected final IStringDualConsumerFeedback consumerFeedback;

    public GuiTextDualInput(int maxTextLength, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumer consumer)
    {
        super(maxTextLength, titleKey, defaultText1, defaultText2, parent);

        this.consumer = consumer;
        this.consumerFeedback = null;
    }

    public GuiTextDualInput(int maxTextLength, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer)
    {
        super(maxTextLength, titleKey, defaultText1, defaultText2, parent);

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

package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class GuiTextInputMultiLineFeedback extends GuiTextInputMultiLineBase
{
    protected final IStringConsumerFeedback consumer;

	public GuiTextInputMultiLineFeedback(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer)
	{
		this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, Color4f.WHITE, Color4f.WHITE, true, true, true);
	}

	public GuiTextInputMultiLineFeedback(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer,
	                                     Color4f textColor, boolean withShadow)
	{
		this(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, consumer, textColor, Color4f.WHITE, withShadow, true, true);
	}

    public GuiTextInputMultiLineFeedback(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer,
                                         Color4f textColor, Color4f cursorColor,
                                         boolean withShadow, boolean withBackground, boolean withDecorations)
    {
	    super(maxTextLength, displayLines, maxLines, titleKey, defaultText, parent, textColor, cursorColor, withShadow, withBackground, withDecorations);
        this.consumer = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        return this.consumer.setString(this.textField.getValueWrapper());
    }

	@Override
	public void addMessage(Message.MessageType type, int lifeTime, String messageKey, Object... args)
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

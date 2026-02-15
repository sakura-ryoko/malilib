package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;
import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public class GuiTextInputStackedMultiLineFeedback extends GuiTextInputStackedMultiLineBase
{
    protected final IStringDualConsumerFeedback consumer;

	public GuiTextInputStackedMultiLineFeedback(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer)
	{
		this(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, consumer, Color4f.WHITE, true);
	}

    public GuiTextInputStackedMultiLineFeedback(int maxTextLength, int displayLines, int maxLines, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer,
                                                Color4f textColor, boolean withShadow)
    {
        super(maxTextLength, displayLines, maxLines, titleKey, defaultText1, defaultText2, parent, textColor, withShadow);

        this.consumer = consumer;
    }

    @Override
    protected boolean applyValues(String string1, String string2)
    {
        return this.consumer.setStrings(this.textField1.getValueWrapper(), this.textField2.getValueWrapper());
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

package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;

import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public class GuiTextInputFeedback extends GuiTextInputBase
{
    protected final IStringConsumerFeedback consumer;

    public GuiTextInputFeedback(int maxTextLength, String titleKey, String defaultText, @Nullable Screen parent, IStringConsumerFeedback consumer)
    {
        super(maxTextLength, titleKey, defaultText, parent);

        this.consumer = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        return this.consumer.setString(this.textField.getText());
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

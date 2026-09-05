package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;

public class GuiTextDualInputFeedback extends GuiTextDualInputBase
{
    protected final IStringDualConsumerFeedback consumer;

    public GuiTextDualInputFeedback(int maxTextLength, String titleKey, String defaultText1, String defaultText2, @Nullable Screen parent, IStringDualConsumerFeedback consumer)
    {
        super(maxTextLength, titleKey, defaultText1, defaultText2, parent);

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

package fi.dy.masa.malilib.gui;

import java.util.Collection;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IStringListConsumer;
import fi.dy.masa.malilib.gui.widgets.WidgetListStringSelection;
import fi.dy.masa.malilib.gui.widgets.WidgetStringListEntry;
import fi.dy.masa.malilib.interfaces.IStringListProvider;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiStringListSelection extends GuiListBase<String, WidgetStringListEntry, WidgetListStringSelection> implements IStringListProvider
{
    protected final ImmutableList<String> strings;
    protected final IStringListConsumer consumer;

    public GuiStringListSelection(Collection<String> strings, IStringListConsumer consumer)
    {
        super(10, 30);

        this.strings = ImmutableList.copyOf(strings);
        this.consumer = consumer;
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 60;
    }

    @Override
    public Collection<String> getStrings()
    {
        return this.strings;
    }

    @Override
    protected WidgetListStringSelection createListWidget(int listX, int listY)
    {
        return new WidgetListStringSelection(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 32;

        x += this.createButton(x, y, -1, ButtonListener.Type.OK) + 2;
        x += this.createButton(x, y, -1, ButtonListener.Type.CANCEL) + 2;
    }

    private int createButton(int x, int y, int width, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type, this);
        String label = type.getDisplayName();

        if (width == -1)
        {
            width = this.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);
        this.addButton(button, listener);

        return width;
    }

	private record ButtonListener(Type type, GuiStringListSelection parent) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			if (this.type == Type.OK && this.parent.getListWidget() != null)
			{
				this.parent.consumer.consume(this.parent.getListWidget().getSelectedEntries());
			}
			else
			{
				GuiBase.openGui(this.parent.getParent());
			}
		}

		public enum Type
		{
			OK("malilib.gui.button.ok"),
			CANCEL("malilib.gui.button.cancel");

			private final String translationKey;

			private Type(String translationKey)
			{
				this.translationKey = translationKey;
			}

			public String getDisplayName(Object... args)
			{
				return StringUtils.translate(this.translationKey, args);
			}
		}
	}
}

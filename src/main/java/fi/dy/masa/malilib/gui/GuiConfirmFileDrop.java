package fi.dy.masa.malilib.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.interfaces.IPathListConsumerFeedback;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiConfirmFileDrop<T extends WidgetFileBrowserBase.FileFilter> extends GuiDialogBase implements IPathListConsumerFeedback
{
    public static final WidgetFileBrowserBase.FileFilter FILE_FILTER_ANY = new WidgetFileBrowserBase.FileFilter();
    protected final List<String> messageLines = new ArrayList<>();
    protected final List<Path> files;
    protected final IPathListConsumerFeedback consumer;
    protected @Nullable T fileFilter;
    protected int textColor = 0xFFC0C0C0;

    @SuppressWarnings("unchecked")
    public GuiConfirmFileDrop(int width, List<Path> files, IPathListConsumerFeedback consumer, @Nullable Screen parent, String messageKey, Object... args)
    {
        this(width, "malilib.gui.title.file_drop_confirm", files, consumer, (T) FILE_FILTER_ANY, parent, messageKey, Arrays.asList(args));
    }

    public GuiConfirmFileDrop(int width, String title, List<Path> files, IPathListConsumerFeedback consumer, @Nullable T filter, @Nullable Screen parent, String messageKey, Object... args)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(title);
        this.fileFilter = filter;
        this.files = filter != null ? files.stream().filter(f ->
                                                            {
	                                                            try
	                                                            {
		                                                            return filter.accept(f);
	                                                            }
	                                                            catch (IOException _)
                                                                {
                                                                    return false;
                                                                }
                                                            }).toList() : files;
        this.consumer = consumer;
        this.useTitleHierarchy = false;

        StringUtils.splitTextToLines(this.messageLines, StringUtils.translate(messageKey, args), width - 30);

        this.setWidthAndHeight(width, this.getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        int x = this.dialogLeft + 10;
        int y = this.dialogTop + this.dialogHeight - 24;
        int buttonWidth = this.getButtonWidth();

        this.createButton(x, y, buttonWidth, ButtonType.OK);
        x += buttonWidth + 10;

        this.createButton(x, y, buttonWidth, ButtonType.CANCEL);
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
    }

    public int getMessageHeight()
    {
        return this.messageLines.size() * (this.fontHeight + 1) - 1 + 5;
    }

    protected int getButtonWidth()
    {
        int width = 0;

        for (ButtonType type : ButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getDisplayName()) + 10);
        }

        return width;
    }

    protected void createButton(int x, int y, int buttonWidth, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
        this.addButton(button, this.createActionListener(type));
    }

    @Override
    public boolean isPauseScreen()
    {
        return this.getParent() != null && this.getParent().isPauseScreen();
    }

    @Override
    public void drawContents(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().extractRenderState(ctx.getGuiGraphics(), mouseX, mouseY, partialTicks);
        }

	    ctx.pose().pushMatrix();
	    ctx.pose().translate(0, 0);

        RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xF0000000, COLOR_HORIZONTAL_BAR);

        // Draw the title
        this.drawStringWithShadow(ctx, this.getTitleString(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);
        int y = this.dialogTop + 20;

        for (String text : this.messageLines)
        {
            this.drawString(ctx, text, this.dialogLeft + 10, y, this.textColor);
            y += this.fontHeight + 1;
        }

        this.drawButtons(ctx, mouseX, mouseY, partialTicks);
	    ctx.pose().popMatrix();
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
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

    public @Nullable T getFileFilter()
    {
        return this.fileFilter;
    }

    @Override
    public boolean onSetPathsCompleted(List<Path> sources)
    {
        if (this.getParent() instanceof IPathListConsumerFeedback)
        {
            return ((IPathListConsumerFeedback) this.getParent()).onSetPathsCompleted(sources);
        }

        return false;
    }

    @Override
    public void onSetPathsAborted()
    {
        if (this.getParent() instanceof IPathListConsumerFeedback)
        {
            ((IPathListConsumerFeedback) this.getParent()).onSetPathsAborted();
        }
    }

    protected record ButtonListener(ButtonType type, GuiConfirmFileDrop<?> gui) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			if (this.type == ButtonType.OK)
			{
				this.gui().consumer.onSetPathsCompleted(this.gui().files);
			}
			else if (this.type == ButtonType.CANCEL)
			{
				this.gui().consumer.onSetPathsAborted();
			}

			GuiBase.openGui(this.gui().getParent());
		}
	}

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel");

        private final String labelKey;

        ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return (this == ButtonType.OK ? GuiBase.TXT_GREEN : GuiBase.TXT_RED) + StringUtils.translate(this.labelKey) + GuiBase.TXT_RST;
        }
    }
}

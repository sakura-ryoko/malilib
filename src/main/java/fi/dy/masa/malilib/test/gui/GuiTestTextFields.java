package fi.dy.masa.malilib.test.gui;

import java.util.function.Supplier;

import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiTestTextFields extends GuiBase
{
	private Supplier<String> string1;
	private Supplier<String> string2;

	public GuiTestTextFields()
	{
		this.title = StringUtils.translate("malilib.gui.title.test_text_fields");
		this.string1 = () -> "String 1";
		this.string2 = () -> "String 2";
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int x = 10;
		int y = 20;

		x += this.createButton(x, y, ButtonType.SINGLE_TEXT);
		x += this.createButton(x, y, ButtonType.DUAL_TEXT);
		x += this.createButton(x, y, ButtonType.MULTI_LINE);
		x += this.createButton(x, y, ButtonType.STACKED_MULTI_LINE);
	}

	private int createButton(int x, int y, ButtonType type)
	{
		ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, type.getDisplayName());
		this.addButton(button, this.createActionListener(type));
		return button.getWidth() + 2;
	}

	private void displayError()
	{
		InfoUtils.showGuiOrInGameMessage(Message.MessageType.ERROR, "malilib.message.error.invalid_strings_provided");
	}

	private void displayResults(boolean dual)
	{
		final String str1 = String.format("%s", this.string1.get());

		if (dual)
		{
			final String str2 = String.format("%s", this.string2.get());
			InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_strings_dual", str1, str2);
		}
		else
		{
			InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_strings_single", str1);
		}
	}

	private ButtonListener createActionListener(ButtonType type)
	{
		return new ButtonListener(type, this);
	}

	private record ButtonListener(ButtonType type, GuiTestTextFields gui)
			implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			if (this.type() == ButtonType.SINGLE_TEXT)
			{
				GuiBase.openGui(new GuiTextInputFeedback(256,
				                                         "malilib.gui.title.test_single_text_editor",
				                                         this.gui().string1.get(), this.gui,
				                                         new SingleFeedbackListener(this.gui)));
			}
			else if (this.type() == ButtonType.DUAL_TEXT)
			{
				GuiBase.openGui(new GuiTextDualInputFeedback(256,
				                                             "malilib.gui.title.test_dual_text_editor",
				                                             this.gui().string1.get(), this.gui().string2.get(),
				                                             this.gui,
				                                             new DualFeedbackListener(this.gui)));
			}
			else if (this.type() == ButtonType.MULTI_LINE)
			{
				GuiBase.openGui(new GuiTextInputMultiLineFeedback(256, 2, 8,
				                                                  "malilib.gui.title.test_multi_line_text_editor",
				                                                  this.gui().string1.get(), this.gui,
				                                                  new SingleFeedbackListener(this.gui)));
			}
			else if (this.type() == ButtonType.STACKED_MULTI_LINE)
			{
				GuiBase.openGui(new GuiTextInputStackedMultiLineFeedback(256, 2, 8,
				                                                         "malilib.gui.title.test_stacked_multi_line_text_editor",
				                                                         this.gui().string1.get(), this.gui().string2.get(),
				                                                         this.gui,
				                                                         new DualFeedbackListener(this.gui)));
			}
		}
	}

	private enum ButtonType
	{
		SINGLE_TEXT             ("malilib.gui.button.single_text"),
		DUAL_TEXT               ("malilib.gui.button.dual_text"),
		MULTI_LINE              ("malilib.gui.button.multi_line"),
		STACKED_MULTI_LINE      ("malilib.gui.button.stacked_multi_line"),
		;

		private final String labelKey;

		ButtonType(String labelKey)
		{
			this.labelKey = labelKey;
		}

		public String getDisplayName()
		{
			return StringUtils.translate(this.labelKey);
		}
	}

	private record SingleFeedbackListener(GuiTestTextFields gui) implements IStringConsumerFeedback
	{
		@Override
		public boolean setString(String string)
		{
			if (string.isEmpty())
			{
				this.gui().displayError();
				return false;
			}

			this.gui().string1 = () -> string;
			this.gui().string2 = () -> "";
			this.gui().displayResults(false);
			return true;
		}
	}

	private record DualFeedbackListener(GuiTestTextFields gui) implements IStringDualConsumerFeedback
	{
		@Override
		public boolean setStrings(String string1, String string2)
		{
			if (string1.isEmpty() || string2.isEmpty())
			{
				this.gui().displayError();
				return false;
			}

			this.gui().string1 = () -> string1;
			this.gui().string2 = () -> string2;
			this.gui().displayResults(true);
			return true;
		}
	}
}

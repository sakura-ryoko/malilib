package fi.dy.masa.malilib.test.gui;

import java.util.Objects;
import java.util.function.Supplier;

import fi.dy.masa.malilib.gui.*;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.interfaces.IStringDualConsumerFeedback;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiTestTextFields extends GuiBase
{
	private Supplier<String> string1;
	private Supplier<String> string2;
	private Supplier<String> string3;
	private Supplier<String> string4;
	private Supplier<String> string5;
	private GuiTextFieldGeneric shortField;
	private GuiTextFieldGeneric defaultField;
	private GuiTextFieldGeneric longField;

	private final String defaultString1 = "String 1";
	private final String defaultString2 = "String 2";
	private final String defaultString3 = "String 3";
	private final String defaultString4 = "String 4";
	private final String defaultString5 = "String 5";

	public GuiTestTextFields()
	{
		this.title = StringUtils.translate("malilib.gui.title.test_text_fields");
		this.string1 = () -> this.defaultString1;
		this.string2 = () -> this.defaultString2;
		this.string3 = () -> this.defaultString3;
		this.string4 = () -> this.defaultString4;
		this.string5 = () -> this.defaultString5;
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

		x = 10;
		y += 24;
		final int height = 20;
		final int shortLength = 12;
		this.shortField = new GuiTextFieldGeneric(x, y, this.calcMaxTextFieldWidth(shortLength), height, this.font);
		this.shortField.setValue(this.string3.get());
		this.shortField.setMaxLength(shortLength);
		this.addTextField(this.shortField, new ShortTextFieldListener(this), TextFieldType.STRING.setMaxLength(shortLength));
		y += 24;

		final int defaultLength = 32;
		this.defaultField = new GuiTextFieldGeneric(x, y, this.calcMaxTextFieldWidth(defaultLength), height, this.font);
		this.defaultField.setValue(this.string4.get());
		this.defaultField.setMaxLength(defaultLength);
		this.addTextField(this.defaultField, new DefaultTextFieldListener(this), TextFieldType.STRING.setMaxLength(defaultLength));
		y += 24;

		final int longLength = 64;
		this.longField = new GuiTextFieldGeneric(x, y, this.calcMaxTextFieldWidth(longLength), height, this.font);
		this.longField.setValue(this.string5.get());
		this.longField.setMaxLength(longLength);
		this.addTextField(this.longField, new LongTextFieldListener(this), TextFieldType.STRING.setMaxLength(longLength));
		y += 24;
		x += this.createButton(x, y, ButtonType.OK);
		x += this.createButton(x, y, ButtonType.RESET);
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

	private void displayFieldResults(final int i)
	{
		switch (i)
		{
			case 1 ->
			{
				final String result = String.format("%s", this.string1.get());
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_string_each", 1, result);
			}
			case 2 ->
			{
				final String result = String.format("%s", this.string2.get());
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_string_each", 2, result);
			}
			case 3 ->
			{
				final String result = String.format("%s", this.string3.get());
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_string_each", 3, result);
			}
			case 4 ->
			{
				final String result = String.format("%s", this.string4.get());
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_string_each", 4, result);
			}
			case 5 ->
			{
				final String result = String.format("%s", this.string5.get());
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "malilib.message.test_edit_string_each", 5, result);
			}
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
			else if (this.type() == ButtonType.OK)
			{
				if (!Objects.equals(this.gui().string1.get(), this.gui().defaultString1))
				{
					this.gui().displayFieldResults(1);
				}
				if (!Objects.equals(this.gui().string2.get(), this.gui().defaultString2))
				{
					this.gui().displayFieldResults(2);
				}
				if (!Objects.equals(this.gui().string3.get(), this.gui().defaultString3))
				{
					this.gui().displayFieldResults(3);
				}
				if (!Objects.equals(this.gui().string4.get(), this.gui().defaultString4))
				{
					this.gui().displayFieldResults(4);
				}
				if (!Objects.equals(this.gui().string5.get(), this.gui().defaultString5))
				{
					this.gui().displayFieldResults(5);
				}
			}
			else if (this.type() == ButtonType.RESET)
			{
				this.gui().string1 = () -> this.gui().defaultString1;
				this.gui().string2 = () -> this.gui().defaultString2;
				this.gui().string3 = () -> this.gui().defaultString3;
				this.gui().shortField.setValue(this.gui().defaultString3);
				this.gui().string4 = () -> this.gui().defaultString4;
				this.gui().defaultField.setValue(this.gui().defaultString4);
				this.gui().string5 = () -> this.gui().defaultString5;
				this.gui().longField.setValue(this.gui().defaultString5);
			}
		}
	}

	private enum ButtonType
	{
		SINGLE_TEXT             ("malilib.gui.button.single_text"),
		DUAL_TEXT               ("malilib.gui.button.dual_text"),
		MULTI_LINE              ("malilib.gui.button.multi_line"),
		STACKED_MULTI_LINE      ("malilib.gui.button.stacked_multi_line"),
		OK                      ("malilib.gui.button.ok"),
		RESET                   ("malilib.gui.button.reset"),
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

	private record ShortTextFieldListener(GuiTestTextFields gui) implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			this.gui().string3 = textField::getValue;
			return true;
		}
	}

	private record DefaultTextFieldListener(GuiTestTextFields gui) implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			this.gui().string4 = textField::getValue;
			return true;
		}
	}

	private record LongTextFieldListener(GuiTestTextFields gui) implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			this.gui().string5 = textField::getValue;
			return true;
		}
	}
}

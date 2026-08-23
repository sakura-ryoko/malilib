package fi.dy.masa.malilib.gui.wrappers;

import java.util.ArrayList;
import java.util.List;

public enum TextFieldType
{
	DOUBLE          (-1),
	FLOAT           (-1),
	INTEGER         (-1),
	STRING          (256),
	BLOCK_ID        (256),
	BLOCK_STATE     (256),
	VALID_STRING    (256),
	;

	private final List<String> validStrings = new ArrayList<>();
	private int maxLength;

	TextFieldType(int maxLength)
	{
		this.maxLength = maxLength;
	}

	public TextFieldType setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
		return this;
	}

	public TextFieldType setValidStrings(List<String> validStrings)
	{
		this.validStrings.addAll(validStrings);
		return this;
	}

	public int getMaxLength()
	{
		return this.maxLength;
	}

	public List<String> getValidStrings()
	{
		return this.validStrings;
	}

	public boolean matchValidString(String s)
	{
		return this.validStrings.contains(s);
	}
}

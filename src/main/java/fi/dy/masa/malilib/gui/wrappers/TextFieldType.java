package fi.dy.masa.malilib.gui.wrappers;

public enum TextFieldType
{
	DOUBLE      (-1),
	FLOAT       (-1),
	INTEGER     (-1),
	STRING      (256),
	;

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

	public int getMaxLength()
	{
		return this.maxLength;
	}
}

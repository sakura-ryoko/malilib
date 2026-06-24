package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class EmptyData extends BaseData
{
	public static final String TAG_NAME = "TAG_End";
	public static final EmptyData INSTANCE = new EmptyData();

	protected EmptyData()
	{
		super(Constants.NBT.TAG_END, TAG_NAME);
	}

	@Override
	public EmptyData copy()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return "";
	}

	@Override
	public void write(DataOutput output) throws IOException
	{
	}

	public static EmptyData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
	{
		return new EmptyData();
	}
}

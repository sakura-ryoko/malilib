package fi.dy.masa.malilib.util.data.tag.util;

import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class DataOutputSizeTracker implements DataOutput
{
	private final DataOutput os;
	private final SizeTracker tracker;

	public DataOutputSizeTracker(DataOutput os, SizeTracker tracker)
	{
		this.os = os;
		this.tracker = tracker;
	}

	private void increment(long numBytes) throws IOException
	{
		this.tracker.increment(numBytes);
	}

	@Override
	public void write(int b) throws IOException
	{
		this.increment(Byte.BYTES);
		this.os.write(b);
	}

	@Override
	public void write(byte @NotNull [] b) throws IOException
	{
		this.increment(b.length);
		this.os.write(b);
	}

	@Override
	public void write(byte @NotNull [] b, int off, int len) throws IOException
	{
		this.increment(len);
		this.os.write(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException
	{
		this.increment(Byte.BYTES);
		this.os.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException
	{
		this.increment(Byte.BYTES);
		this.os.writeByte(v);
	}

	@Override
	public void writeShort(int v) throws IOException
	{
		this.increment(Short.BYTES);
		this.os.writeShort(v);
	}

	@Override
	public void writeChar(int v) throws IOException
	{
		this.increment(Character.BYTES);
		this.os.writeChar(v);
	}

	@Override
	public void writeInt(int v) throws IOException
	{
		this.increment(Integer.BYTES);
		this.os.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException
	{
		this.increment(Long.BYTES);
		this.os.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException
	{
		this.increment(Float.BYTES);
		this.os.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException
	{
		this.increment(Double.BYTES);
		this.os.writeDouble(v);
	}

	@Override
	public void writeBytes(@NotNull String s) throws IOException
	{
		this.increment(s.length());
		this.os.writeBytes(s);
	}

	@Override
	public void writeChars(@NotNull String s) throws IOException
	{
		this.increment((long) s.length() * Character.BYTES);
		this.os.writeChars(s);
	}

	@Override
	public void writeUTF(@NotNull String s) throws IOException
	{
		this.increment(Short.BYTES + DataTypeUtils.getUTFLength(s));
		this.os.writeUTF(s);
	}
}

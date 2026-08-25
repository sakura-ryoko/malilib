package fi.dy.masa.malilib.util.data.tag.util;

import java.io.IOException;

public class SizeTrackerException extends IOException
{
	private final long maxSize;
	private final long currentSize;

	public SizeTrackerException(final long maxSize, final long currentSize)
	{
		super("SizeTracker limit exceeded!  Max Size: " + maxSize + ", Current Size: " + currentSize);
		this.maxSize = maxSize;
		this.currentSize = currentSize;
	}

	public long maxSize()
	{
		return this.maxSize;
	}

	public long currentSize()
	{
		return this.currentSize;
	}
}

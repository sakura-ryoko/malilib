package fi.dy.masa.malilib.util.data.tag.util;

import java.io.IOException;

public class SizeTracker
{
    private final long maxBytes;
    private long currentBytes;

    public SizeTracker(long maxBytes)
    {
        this.maxBytes = maxBytes;
    }

    public void increment(int numBytes) throws IOException
    {
        this.currentBytes += numBytes;

        if (this.maxBytes > 0 && this.currentBytes > this.maxBytes)
        {
            throw new IOException("SizeTracker limit exceeded, limit = " + this.maxBytes + ", current = " + this.currentBytes);
        }
    }
}

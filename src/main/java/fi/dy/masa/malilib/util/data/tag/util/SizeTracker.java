package fi.dy.masa.malilib.util.data.tag.util;

public class SizeTracker
{
    public static final long DEFAULT_MAX_BYTES  = 1024L * 1024L * 1024L; // 1 GB
    public static final long FILE_MAX_BYTES     =  512L * 1024L * 1024L; // 512 MB
    public static final long NETWORK_MAX_BYTES  =   64L * 1024L * 1024L; // 64 MB

    private final long maxBytes;
    private long currentBytes;

    public SizeTracker(long maxBytes)
    {
        long effectiveMax = (maxBytes <= 0) ? DEFAULT_MAX_BYTES : maxBytes;
        this.maxBytes = Math.min(effectiveMax, DEFAULT_MAX_BYTES);
    }

    public void increment(long numBytes) throws SizeTrackerException
    {
        this.currentBytes += numBytes;

        if (this.maxBytes > 0 && this.currentBytes > this.maxBytes)
        {
            throw new SizeTrackerException(this.maxBytes, this.currentBytes);
        }
    }
}

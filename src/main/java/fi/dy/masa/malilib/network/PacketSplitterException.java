package fi.dy.masa.malilib.network;

public class PacketSplitterException extends RuntimeException
{
	public PacketSplitterException(String message)
	{
		super(message);
	}

	public PacketSplitterException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

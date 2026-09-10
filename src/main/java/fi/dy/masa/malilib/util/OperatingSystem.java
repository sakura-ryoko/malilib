package fi.dy.masa.malilib.util;

public enum OperatingSystem
{
	WINDOWS,
	LINUX,
	MAC,
	UNIX,
	SOLARIS,
	UNKNOWN;

	public static OperatingSystem get()
	{
		final String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win"))
		{
			return OperatingSystem.WINDOWS;
		}
		else if (os.contains("mac"))
		{
			return OperatingSystem.MAC;
		}
		else if (os.contains("linux"))
		{
			return OperatingSystem.LINUX;
		}
		else if (os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0)
		{
			return OperatingSystem.UNIX;
		}
		else if (os.contains("sunos"))
		{
			return OperatingSystem.SOLARIS;
		}

		return OperatingSystem.UNKNOWN;
	}
}
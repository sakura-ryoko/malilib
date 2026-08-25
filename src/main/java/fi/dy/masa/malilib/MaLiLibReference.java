package fi.dy.masa.malilib;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.SharedConstants;

import fi.dy.masa.malilib.util.StringUtils;

public class MaLiLibReference
{
	public static final String MOD_ID = "malilib";
	public static final String MOD_NAME = "MaLiLib";
	public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
	public static final String MC_VERSION = getMcVersion();
	public static final String MOD_TYPE = "fabric";
	public static final String MOD_STRING = MOD_ID+"-"+MOD_TYPE+"-"+MC_VERSION+"-"+MOD_VERSION;

	public static final boolean LOCAL_DEBUG = false;                // Enforce DEBUG_MODE ON (Or use Property/Environment Variable)
	public static final boolean EXPERIMENTAL_MODE = false;          // Enforce EXPERIMENTAL_MODE when TRUE (Experimental code)

	public static final boolean RUNNING_IN_IDE = isRunningInIde();  // Enable IDE capabilities (Sets Vanilla in IDE mode)
	public static final boolean DEBUG_MODE = isDebug();             // Enable Debug Mode (Allows Test Library)
	public static final boolean ANSI_MODE = isAnsiColor();          // Enable the AnsiLogger's Console Colors

	/**
	 * Use jvmArg {@code -Dmalilib.debug.mode=true} or the
	 * Environment Variable: {@code MALILIB_DEBUG_MODE=true}
	 * to enable Debug Mode.
	 * @return -
	 */
	@ApiStatus.Internal
	private static boolean propertyCheck()
	{
		final String override = System.getProperty(MOD_ID+".debug.mode");
		final String envOverride = System.getenv(MOD_ID.toUpperCase()+"_DEBUG_MODE");

		if (override != null) { return Boolean.parseBoolean(override); }
		if (envOverride != null) { return Boolean.parseBoolean(envOverride); }

		return false;
	}

	@ApiStatus.Internal
	private static boolean isDebug()
	{
		return (propertyCheck() || LOCAL_DEBUG || EXPERIMENTAL_MODE);
	}

	@ApiStatus.Internal
	private static boolean isAnsiColor()
	{
		return (RUNNING_IN_IDE && DEBUG_MODE);
	}

	@ApiStatus.Internal
	private static String getMcVersion()
	{
		String result = StringUtils.getModVersionString("minecraft");

		if (result != null && !result.isEmpty() && !result.equalsIgnoreCase("?"))
		{
			return result;
		}

		return SharedConstants.getGameVersion().id();
	}

	@ApiStatus.Internal
	private static boolean isRunningInIde()
	{
		if (Boolean.getBoolean("fabric.development")) { return true; }

		// Try other ways also
		String sunCmd = System.getProperty("sun.java.command", "").toLowerCase();
		String classPath = System.getProperty("java.class.path", "").toLowerCase();

		if (System.getProperty("idea.active") != null || classPath.contains("idea_rt.jar") ||
			sunCmd.contains("net.fabricmc.devlaunchinjector") || sunCmd.contains("com.intellij"))
		{
			return true;
		}

		if (System.getProperty("eclipse.launcher") != null || classPath.contains("eclipse"))
		{
			return true;
		}

		for (StackTraceElement ele : Thread.currentThread().getStackTrace())
		{
			String className = ele.getClassName();

			if (className.startsWith("net.fabricmc.devlaunchinjector.") || className.startsWith("com.intellij.rt."))
			{
				return true;
			}
		}

		return false;
	}
}

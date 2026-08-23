package fi.dy.masa.malilib.compat.carpet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibFabricData;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.compat.ModIds;

public class CarpetCompat
{
	public static boolean isCarpetLoaded = false;
	public static boolean isCarpetTisLoaded = false;
	public static String carpetVersion = "";
	public static String carpetTisVersion = "";

	private static final List<Field> CARPET_SETTINGS;
	private static final List<Field> CARPET_TIS_SETTINGS;

	public static void load()
	{
		// NO-OP
	}

	static
	{
		if (MaLiLibFabricData.ALL_MOD_VERSIONS.containsKey(ModIds.carpet))
		{
			carpetVersion = MaLiLibFabricData.ALL_MOD_VERSIONS.get(ModIds.carpet);
			isCarpetLoaded = true;

			if (MaLiLibFabricData.ALL_MOD_VERSIONS.containsKey(ModIds.carpetTis))
			{
				carpetTisVersion = MaLiLibFabricData.ALL_MOD_VERSIONS.get(ModIds.carpetTis);
				isCarpetTisLoaded = true;
			}
		}

		MaLiLib.LOGGER.info("Carpet: [{}], CarpetTIS: [{}]", isCarpetLoaded ? carpetVersion : "N/F", isCarpetTisLoaded ? carpetTisVersion : "N/F");
		CARPET_SETTINGS = new ArrayList<>();
		CARPET_TIS_SETTINGS = new ArrayList<>();

		if (isCarpetLoaded)
		{
			try
			{
				Class<?> CARPET_SETTINGS_CLASS = Class.forName("carpet.CarpetSettings");
				Field[] fields = CARPET_SETTINGS_CLASS.getFields();

				// Only scan for public static fields
				for (Field field : fields)
				{
					if (Modifier.isStatic(field.getModifiers()))
					{
						CARPET_SETTINGS.add(field);
					}
				}
			}
			catch (Throwable e)
			{
				isCarpetLoaded = false;
			}
		}

		if (isCarpetTisLoaded)
		{
			try
			{
				Class<?> CARPET_TIS_SETTINGS_CLASS = Class.forName("carpettisaddition.CarpetTISAdditionSettings");
				Field[] fields = CARPET_TIS_SETTINGS_CLASS.getFields();

				// Only scan for public static fields
				for (Field field : fields)
				{
					if (Modifier.isStatic(field.getModifiers()))
					{
						CARPET_TIS_SETTINGS.add(field);
					}
				}
			}
			catch (Throwable e)
			{
				isCarpetTisLoaded = false;
			}
		}
	}

	/**
	 * Attempt to retrieve a Carpet Settings rule value
	 * @param name The name of the Carpet Rule.
	 * @return [Value|Null]
	 */
	public static @Nullable Object getCarpetRuleValue(String name)
	{
		if (isCarpetLoaded)
		{
			for (Field field : CARPET_SETTINGS)
			{
				if (field.getName().equals(name))
				{
					try
					{
						return field.get(null);
					}
					catch (Throwable e)
					{
						MaLiLib.LOGGER.error("getCarpetRuleValue: Exception retrieving value of rule named '{}'; {}", name, e.getLocalizedMessage());
						return null;
					}
				}
			}
		}

		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.warn("getCarpetRuleValue: Could not retrieve rule named '{}'", name);
		}

		return null;
	}

	/**
	 * Attempt to retrieve a CarpetTIS Settings rule value
	 * @param name The name of the CarpetTIS Rule.
	 * @return [Value|Null]
	 */
	public static @Nullable Object getCarpetTisRuleValue(String name)
	{
		if (isCarpetTisLoaded)
		{
			for (Field field : CARPET_TIS_SETTINGS)
			{
				if (field.getName().equals(name))
				{
					try
					{
						return field.get(null);
					}
					catch (Throwable e)
					{
						MaLiLib.LOGGER.error("getCarpetTisRuleValue: Exception retrieving value of rule named '{}'; {}", name, e.getLocalizedMessage());
						return null;
					}
				}
			}
		}

		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.warn("getCarpetTisRuleValue: Could not retrieve rule named '{}'", name);
		}

		return null;
	}

}

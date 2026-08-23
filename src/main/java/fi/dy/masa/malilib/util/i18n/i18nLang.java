package fi.dy.masa.malilib.util.i18n;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fi.dy.masa.malilib.MaLiLib;

public class i18nLang
{
	private static final Gson GSON = new Gson();
	private final String langCode;
	private ImmutableMap<String, String> map;

	private i18nLang(String langCode)
	{
		this.langCode = langCode;
		this.map = null;
	}

	@Nullable
	protected static i18nLang load(final String dir, final String langCode) throws IOException
	{
		final String filePath = "/"+dir+"/"+langCode+".json";
		InputStream is = i18nLang.class.getResourceAsStream(filePath);
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		i18nLang lang = new i18nLang(langCode);

		try
		{
			if (is != null)
			{
				JsonObject obj = GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);

				for (Map.Entry<String, JsonElement> entry : obj.entrySet())
				{
					builder.put(entry.getKey(), entry.getValue().getAsString());
				}

				lang.map = builder.build();
				is.close();

				MaLiLib.LOGGER.info("i18nLang#load: File: '{}' has been loaded successfully", filePath);

				return lang;
			}
			else
			{
				MaLiLib.LOGGER.error("i18nLang#load: Error; file not found: '{}'", filePath);
				return null;
			}
		}
		catch (Throwable t)
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (Throwable t1)
				{
					t.addSuppressed(t1);
				}
			}

			throw t;
		}
	}

	public String getLangCode()
	{
		return this.langCode;
	}

	public i18nOption toOption()
	{
		return i18nOption.fromString(this.langCode);
	}

	public boolean hasTranslation(final String key)
	{
		return this.map.containsKey(key);
	}

	public @Nullable String get(final String key)
	{
		return this.map.get(key);
	}

	public String getOrDefault(final String key, final String defaultString)
	{
		return this.map.getOrDefault(key, defaultString);
	}
}

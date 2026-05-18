package fi.dy.masa.malilib.util.i18n;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import net.minecraft.network.chat.HoverEvent;

public class i18nManager
{
	public static final i18nFileFilter FILE_FILTER = new i18nFileFilter();
	public static final String DEFAULT_LANG = "en_us";
	private final List<String> keys;
	private final List<i18nOption> options;
	private final i18nLang defaultLang;
	private final String modId;
	private final String baseString;
	private i18nLang lang;

	private i18nManager(String modId) throws IOException
	{
		this.modId = modId;
		this.keys = new ArrayList<>();
		this.options = new ArrayList<>();
		this.baseString = "assets/"+this.modId+"/lang";
		this.defaultLang = i18nLang.load(this.baseString, DEFAULT_LANG);
		this.lang = null;
		this.readKeys();
	}

	@Nullable
	public static i18nManager create(String modId)
	{
		try
		{
			i18nManager result = new i18nManager(modId);

			if (result.defaultLang == null)
			{
				MaLiLib.LOGGER.error("i18nOptionManager#create({}): Default language: '{}' not found!", modId, DEFAULT_LANG);
			}

			return result;
		}
		catch (IOException e)
		{
			MaLiLib.LOGGER.error("i18nOptionManager#create({}): Exception building i18nManager; {}", modId, e.getLocalizedMessage());
		}

		return null;
	}

	private void readKeys()
	{
		final String baseString = "/"+this.baseString;
		URL resource = i18nManager.class.getResource(baseString);

		if (resource != null)
		{
			try
			{
				URI uri = resource.toURI();
				Path root;
				FileSystem tempFs = null;
				boolean created = false;

				if (uri.getScheme().equals("jar"))
				{
					try
					{
						tempFs = FileSystems.getFileSystem(uri);
					}
					catch (FileSystemNotFoundException e)
					{
						tempFs = FileSystems.newFileSystem(uri, Collections.emptyMap());
						created = true;
					}

					root = tempFs.getPath(baseString);
				}
				else
				{
					root = Paths.get(uri);
				}

				this.keys.clear();

				try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, FILE_FILTER))
				{
					for (Path file : stream)
					{
						final String fileName = file.getFileName().toString();
						final String nameOnly = fileName.split("\\.")[0];
						this.keys.add(nameOnly);
					}
				}
				finally
				{
					if (tempFs != null && tempFs.isOpen()
						&& created)
					{
						tempFs.close();
					}
				}
			}
			catch (Exception e)
			{
				MaLiLib.LOGGER.error("i18nOptionManager#readKeys({}): Could not find resource '{}'; Exception: {}", this.getModId(), this.baseString, e.getLocalizedMessage());
			}
		}

		if (this.keys.isEmpty())
		{
			this.keys.add(DEFAULT_LANG);
		}

		this.buildLanguageOptions();

		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.info("i18nOptionManager#readKeys({}): keys read from assets folder: {}", this.getModId(), this.keys.toString());
		}
	}

	private void buildLanguageOptions()
	{
		this.options.clear();

		for (String key : this.keys)
		{
			i18nOption opt = i18nOption.fromString(key);

			if (opt != null)
			{
				this.options.add(opt);
			}
			else
			{
				MaLiLib.LOGGER.warn("i18nOptionManager#buildLanguageOptions({}): Language file: '{}' not matched", this.getModId(), key);
			}
		}

		MaLiLib.LOGGER.warn("i18nOptionManager#buildLanguageOptions({}): Detected [{}] available options.", this.getModId(), this.options.size());
	}

	public String getModId()
	{
		return this.modId;
	}

	public String getBaseString()
	{
		return this.baseString;
	}

	public String getMinecraftLanguage()
	{
		return Minecraft.getInstance().getLanguageManager().getSelected();
	}

	public String getLangCode()
	{
		this.ensureLang();
		return this.lang.getLangCode();
	}

	public boolean isVanillaLanguage()
	{
		this.ensureLang();
		return this.lang.getLangCode().equalsIgnoreCase(this.getMinecraftLanguage());
	}

	public i18nLang getDefaultLang()
	{
		return this.defaultLang;
	}

	public i18nLang getLang()
	{
		this.ensureLang();
		return this.lang;
	}

	public void resetLangToDefault()
	{
		this.lang = this.defaultLang;
	}

	public void setLangAsVanilla()
	{
		this.setLang(this.getMinecraftLanguage());
	}

	public void setLang(i18nConfig config)
	{
		this.setLang(config.getStringValue());
	}

	public void setLang(String langCode)
	{
		if (this.lang != null && Objects.equals(this.lang.getLangCode(), langCode))
		{
			// Already matches
			return;
		}

		// Change
		this.lang = null;

		try
		{
			this.lang = i18nLang.load(this.baseString,langCode);
			MaLiLib.LOGGER.info("i18nOptionManager#setLang({}): Language: '{}' - has been loaded successfully.", this.getModId(), langCode);
		}
		catch (IOException e)
		{
			this.ensureLang();
			MaLiLib.LOGGER.error("i18nOptionManager#setLang({}): Exception loading language: '{}'; {}", this.getModId(), langCode, e.getLocalizedMessage());
		}
	}

	public List<String> getLanguageKeys()
	{
		return this.keys;
	}

	public List<i18nOption> getLanguageOptions()
	{
		this.ensureLang();
		return this.options;
	}

	private void ensureLang()
	{
		if (this.lang == null)
		{
			this.lang = this.defaultLang;
		}
	}

	public boolean hasTranslation(String key)
	{
		this.ensureLang();
		return this.lang.hasTranslation(key);
	}

	public String translateOrFallback(String key, String fallback)
	{
		this.ensureLang();

		if (this.hasTranslation(key))
		{
			return this.translate(key);
		}

		return fallback;
	}

	public String translate(String key, Object... args)
	{
		this.ensureLang();
		final String result = this.lang.getOrDefault(key, key);

		try
		{
			return String.format(Locale.ROOT, result, args);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("i18nOptionManager#translate({}): Formatting exception for key: {}; {}", this.getModId(), key, e.getLocalizedMessage());
			return "Format Error: "+result;
		}
	}

	public Component translateAsText(String key, Object... args)
	{
		this.ensureLang();

		if (this.hasTranslation(key))
		{
			return Component.nullToEmpty(this.translate(key, args));
		}
		else
		{
			return Component.literal(key)
							.withStyle((style) ->
											   style.withColor(ChatFormatting.RED)
													.withHoverEvent(new HoverEvent.ShowText(Component.nullToEmpty("Missing translation: " + key)))
									  );
		}
	}

	// matches 'en_us.json'; for example.
	public static class i18nFileFilter implements DirectoryStream.Filter<Path>
	{
		private final Pattern pattern1 = Pattern.compile("^[a-z]{2,4}_[a-z]{2,4}$");
		private final Pattern pattern2 = Pattern.compile("^[a-z]{3,4}$");

		@Override
		public boolean accept(Path entry) throws IOException
		{
			try
			{
				final String fullName = entry.getFileName().toString();

				if (Files.isRegularFile(entry) && fullName.endsWith(".json"))
				{
					final String nameOnly = fullName.split("\\.")[0];

					return  this.pattern1.matcher(nameOnly).matches() ||
							this.pattern2.matcher(nameOnly).matches();
				}

				return false;
			}
			catch (Exception err)
			{
				throw new IOException(err.getLocalizedMessage());
			}
		}
	}
}

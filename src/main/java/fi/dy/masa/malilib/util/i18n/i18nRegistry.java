package fi.dy.masa.malilib.util.i18n;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLibReference;

public class i18nRegistry
{
	private final HashMap<String, i18nManager> translationManagerMap;
	private final HashMap<String, i18nMode> translationModeMap;
	private ImmutableList<i18nManager> translationManagers;

	public i18nRegistry()
	{
		this.translationManagerMap = new HashMap<>();
		this.translationModeMap = new HashMap<>();
		this.translationManagers = ImmutableList.of();
	}

	public void registerTranslationManager(String modId, i18nManager manager, i18nMode mode)
	{
		this.translationManagerMap.put(modId, manager);
		this.translationModeMap.put(modId, mode);
		ArrayList<i18nManager> list = new ArrayList<>(this.translationManagerMap.values());
		list.sort(Comparator.comparing(i18nManager::getModId));
		this.translationManagers = ImmutableList.copyOf(list);
	}

	public void registerLanguageMode(String modId, i18nMode mode)
	{
		this.translationModeMap.put(modId, mode);
	}

	// Return MaLiLib's configured Language Code.
	public String getBaseLanguageCode()
	{
		if (this.translationManagerMap.containsKey(MaLiLibReference.MOD_ID))
		{
			return this.translationManagerMap.get(MaLiLibReference.MOD_ID).getLang().getLangCode();
		}

		return i18nManager.DEFAULT_LANG;
	}

	public String getVanillaLanguageCode()
	{
		return Minecraft.getInstance().getLanguageManager().getSelected();
	}

	public int size()
	{
		return this.translationManagers.size();
	}

	public boolean isEmpty()
	{
		return this.translationManagers.isEmpty();
	}

	public ImmutableList<i18nManager> getTranslationManagers()
	{
		return this.translationManagers;
	}

	public Stream<i18nManager> stream()
	{
		return this.translationManagers.stream();
	}

	public Optional<i18nLang> getDefaultLanguage(String modId)
	{
		if (this.translationManagerMap.containsKey(modId))
		{
			return Optional.of(this.translationManagerMap.get(modId).getDefaultLang());
		}

		return Optional.empty();
	}

	public Optional<i18nMode> getLanguageMode(String modId)
	{
		if (this.translationModeMap.containsKey(modId))
		{
			return Optional.of(this.translationModeMap.get(modId));
		}

		return Optional.empty();
	}

	public Optional<i18nLang> getCurrentLanguage(String modId)
	{
		if (this.translationManagerMap.containsKey(modId))
		{
			return Optional.of(this.translationManagerMap.get(modId).getLang());
		}

		return Optional.empty();
	}

	public Optional<i18nManager> scanForTranslationKey(String key)
	{
		Set<String> keys = this.translationManagerMap.keySet();
		final String firstKey = key.split("\\.")[0];

		// Return i18nLang for matching ModId, if present first; such as "malilib.config.generic.somekey"
		for (String entry : keys)
		{
			if (entry.equalsIgnoreCase(firstKey))
			{
				Optional<i18nManager> opt = Optional.ofNullable(this.translationManagerMap.get(entry));

				// If key is not present, configure scanning
				if (opt.isPresent() && opt.get().hasTranslation(key))
				{
					return opt;
				}
			}
		}

		// Scan all managers for a positive key match
		AtomicReference<i18nManager> result = new AtomicReference<>(null);

		this.translationManagers.forEach(
				(m) ->
				{
					if (m.hasTranslation(key))
					{
						result.set(m);
					}
				}
		);

		return Optional.ofNullable(result.get());
	}
}

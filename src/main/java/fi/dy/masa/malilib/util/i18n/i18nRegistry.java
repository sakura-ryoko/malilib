package fi.dy.masa.malilib.util.i18n;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableList;

public class i18nRegistry
{
	private final HashMap<String, i18nManager> translationManagerMap;
	private ImmutableList<i18nManager> translationManagers;

	public i18nRegistry()
	{
		this.translationManagerMap = new HashMap<>();
		this.translationManagers = ImmutableList.of();
	}

	public void registerTranslationManager(String modId, i18nManager manager)
	{
		this.translationManagerMap.put(modId, manager);
		ArrayList<i18nManager> list = new ArrayList<>(this.translationManagerMap.values());
		list.sort(Comparator.comparing(i18nManager::getModId));
		this.translationManagers = ImmutableList.copyOf(list);
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
				return Optional.of(this.translationManagerMap.get(entry));
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

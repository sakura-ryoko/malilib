package fi.dy.masa.malilib.util.data;

import java.util.Map;
import java.util.Set;
import com.google.common.collect.ImmutableMap;

/**
 * A Simple interface to make quick and deep Immutable copies when the commonly used
 * shallow copy doesn't work; such as when you might use
 * `new {@link java.util.HashMap}<>(otherMap)` otherwise, but a deep copy is required.
 * <br>
 * @param <K>       The keySet value type
 * @param <V>       The element value type
 */
public class ImmutableMapCopy<K, V>
{
	private final Map<K, V> map;

	public ImmutableMapCopy(Map<K, V> map)
	{
		this.map = map;
	}

	public static <X, Y> ImmutableMapCopy<X, Y> of(final Map<X, Y> map)
	{
		return new ImmutableMapCopy<>(map);
	}

	public ImmutableMap<K, V> toMap()
	{
		ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
		final Set<K> keys = this.map.keySet();

		for (K key : keys)
		{
			builder.put(key, this.map.get(key));
		}

		this.clear();
		return builder.build();
	}

	private void clear()
	{
		this.map.clear();
	}
}

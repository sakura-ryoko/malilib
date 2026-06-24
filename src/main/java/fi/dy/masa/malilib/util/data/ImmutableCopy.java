package fi.dy.masa.malilib.util.data;

import java.util.*;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A Simple interface to make quick and deep Immutable copies when the commonly used
 * shallow copy doesn't work; such as when you might use
 * `new {@link ArrayList}<>(otherList)`, or `{@link ImmutableList}.copyOf(otherList)`,
 * but it requires a deep copy.
 * <br>
 * @param <V>       The element value type
 */
public class ImmutableCopy<V>
{
	private final List<V> list;

	public ImmutableCopy(List<V> values)
	{
		this.list = new ArrayList<>();
		this.list.addAll(values);
	}

	public static <T> ImmutableCopy<T> of(final List<T> list)
	{
		return new ImmutableCopy<>(list);
	}

	public static <T> ImmutableCopy<T> of(final Set<T> set)
	{
		return new ImmutableCopy<>(set.stream().toList());
	}

	public static <T> ImmutableCopy<T> of(final Collection<T> coll)
	{
		return new ImmutableCopy<>(coll.stream().toList());
	}

	public static <T> ImmutableCopy<T> of(final Iterable<T> iter)
	{
		final List<T> list =  new ArrayList<>();
		iter.forEach(list::add);
		return new ImmutableCopy<>(list);
	}

	public static <T> ImmutableCopy<T> of(final Stream<T> stream)
	{
		final List<T> list =  new ArrayList<>();
		stream.forEach(list::add);
		return new ImmutableCopy<>(list);
	}

	public static <T> ImmutableCopy<T> of(final T[] arr)
	{
		return new ImmutableCopy<>(Arrays.stream(arr).toList());
	}

	public ImmutableList<V> toList()
	{
		ImmutableList.Builder<V> builder = ImmutableList.builder();
		
		for (V v : this.list)
		{
			builder.add(v);
		}

		this.clear();
		return builder.build();
	}

	public ImmutableSet<V> toSet()
	{
		ImmutableSet.Builder<V> builder = ImmutableSet.builder();
		
		for (V v : this.list)
		{
			builder.add(v);
		}

		this.clear();
		return builder.build();
	}

	public ImmutableCollection<V> toCollection()
	{
		ImmutableList.Builder<V> builder = ImmutableList.builder();
		
		for (V v : this.list)
		{
			builder.add(v);
		}

		this.clear();
		return builder.build();
	}

	private void clear()
	{
		this.list.clear();
	}
}

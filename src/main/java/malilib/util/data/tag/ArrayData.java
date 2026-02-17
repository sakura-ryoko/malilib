package malilib.util.data.tag;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

/**
 * Informs {@link malilib.util.data.tag.util.DataOps} that the inherited class
 * is an Array Value, and can perform the basic {@link java.util.ArrayList} operations
 * @implNote They will also need to be Mutable objects.
 */
public interface ArrayData extends Iterable<BaseData>
{
	void clear();

	boolean set(int index, BaseData entry);

	boolean add(int index, BaseData entry);

	BaseData remove(int index);

	BaseData get(int index);

	int size();

	default boolean isEmpty()
	{
		return size() == 0;
	}

	default @Nonnull Iterator<BaseData> iterator()
	{
		return new Iterator<BaseData>()
		{
			private int index;

			@Override
			public boolean hasNext()
			{
				return this.index < ArrayData.this.size();
			}

			@Override
			public BaseData next()
			{
				if (this.hasNext())
				{
					return ArrayData.this.get(this.index++);
				}
				else
				{
					throw new NoSuchElementException();
				}
			}
		};
	}

	default Stream<BaseData> stream()
	{
		return StreamSupport.stream(this.spliterator(), false);
	}
}

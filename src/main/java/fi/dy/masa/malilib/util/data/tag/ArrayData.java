package fi.dy.masa.malilib.util.data.tag;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public interface ArrayData extends Iterable<BaseData>
{
	void clear();

	boolean set(int index, BaseData value);

	boolean add(int index, BaseData value);

	BaseData remove(int index);

	BaseData get(int index);

	int size();

	default boolean isEmpty()
	{
		return size() == 0;
	}

	default @Nonnull Iterator<BaseData> iterator()
	{
		return new Iterator<>()
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

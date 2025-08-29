package fi.dy.masa.malilib.data.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import fi.dy.masa.malilib.MaLiLib;

@ApiStatus.Experimental
@SuppressWarnings("unchecked")
public class ListDat<E> implements IDat<List<IDat<?>>>
{
	// FIXME
//    public static final Codec<ListDat<?>> CODEC = Codec.of(
//			new Encoder<>()
//			{
//				@Override
//				public <T> DataResult<T> encode(ListDat<?> input, DynamicOps<T> ops, T prefix)
//				{
//					List<?> list = input.toValueList();
//					Codec<?> codec = input.getValueCodec();
//					Serializer<?> serializer = new Serializer<>(codec);
//					return serializer.encode(list, ops, prefix);
//				}
//			},
//			new Decoder<>()
//			{
//				@Override
//				public <T> DataResult<Pair<ListDat<?>, T>> decode(DynamicOps<T> ops, T input)
//				{
//					return null;
//				}
//			}
//	);

    private final List<IDat<?>> valueList;
	private DatType valueType;
	private Serializer<E> serializer;

    public ListDat()
    {
        this.valueList = new ArrayList<>();
		this.valueType = DatType.EMPTY;
		this.serializer = null;
    }

	public ListDat(IDat<E> value)
	{
		this.valueList = new ArrayList<>();
		this.valueType = DatType.EMPTY;
		this.serializer = null;

		this.add(value);
	}

	private List<IDat<E>> cast()
	{
		return (List<IDat<E>>) (Object) this.valueList;
	}

    public @Nullable IDat<E> get(int index)
    {
        if (index >= this.valueList.size())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting list index [{}]; Out Of Bounds", index);
            return null;
        }

		try
        {
            return (IDat<E>) this.valueList.get(index).getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting list index [{}]; {}", index, err.getLocalizedMessage());
            return null;
        }
    }

    public @Nullable IDat<E> set(final int index, final IDat<E> newValue)
    {
        if (index >= this.valueList.size())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting setting list index [{}]; Out Of Bounds", index);
            return null;
        }

		if (newValue.getType() != this.valueType)
		{
			MaLiLib.LOGGER.error("ListDat: Exception setting element [{}]; Invalid type [{}] when expecting type [{}]", index, newValue.getType().asString(), this.valueType.asString());
			return null;
		}

        try
        {
            return (IDat<E>) this.valueList.set(index, newValue).getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting setting list index [{}]; {}", index, err.getLocalizedMessage());
            return null;
        }
    }

    public boolean add(final IDat<E> newValue)
    {
		if (!this.isEmpty() && newValue.getType() != this.valueType)
		{
			MaLiLib.LOGGER.error("ListDat: Exception adding new element; Invalid type [{}] when expecting type [{}]", newValue.getType().asString(), this.valueType.asString());
			return false;
		}
		else if (this.isEmpty())
		{
			this.valueType = newValue.getType();
		}

		try
        {
            this.valueList.add(newValue);
			this.refreshSerializer();
			return true;
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting adding to list; {}", err.getLocalizedMessage());
            return false;
        }
    }

    public @Nullable IDat<E> getFirst()
    {
        if (this.valueList.isEmpty())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting first index; Out Of Bounds (Empty)");
            return null;
        }

        try
        {
            return (IDat<E>) this.valueList.getFirst().getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting first index; {}", err.getLocalizedMessage());
            return null;
        }
    }

    public @Nullable IDat<E> getLast()
    {
        if (this.valueList.isEmpty())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting last index; Out Of Bounds (Empty)");
            return null;
        }

        try
        {
            return (IDat<E>) this.valueList.getLast().getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting last index; {}", err.getLocalizedMessage());
            return null;
        }
    }

    public boolean isEmpty() { return this.valueList.isEmpty(); }

    public int size() { return this.valueList != null ? this.valueList.size() : 0; }

    public void clear() { this.valueList.clear(); }

    public List<E> toValueList()
    {
        List<E> list = new ArrayList<>();

        this.valueList.forEach(
                entry ->
                        list.add((E) entry.getValue())
        );

        return list;
    }

    public @Nullable ListDat<E> copyFrom(List<IDat<?>> otherList)
    {
		this.valueList.clear();

		if (!otherList.isEmpty())
		{
			this.valueList.addAll(otherList);
			this.valueType = otherList.getFirst().getType();
			this.refreshSerializer();
		}
		else
		{
			this.valueType = DatType.EMPTY;
		}

        return this;
    }

    public Stream<IDat<E>> stream() { return this.cast().stream(); }

    public Stream<E> streamValues()
    {
        return this.toValueList().stream();
    }

    public Iterable<IDat<E>> iterator()
    {
        return Iterables.concat(this.cast());
    }

	@Override
	public List<IDat<?>> getValue()
	{
		return this.valueList;
	}

	@Override
	public void setValue(List<IDat<?>> newValue)
	{
		this.copyFrom(newValue);
	}

	@Override
	public DatType getType()
	{
		return DatType.LIST;
	}

	public DatType getValueType()
	{
		return this.valueType;
	}

	@Nullable
	public Codec<E> getValueCodec()
	{
		return (Codec<E>) this.valueType.codec();
	}

	@Override
	public NbtElement toVanilla()
	{
		NbtList list  = new NbtList();

		this.valueList.forEach(
				(entry) ->
						list.add(entry.toVanilla())
		);

		return list;
	}

	private void refreshSerializer()
	{
		if (this.valueList.isEmpty())
		{
			this.serializer = null;
			this.valueType = DatType.EMPTY;
			return;
		}

		IDat<E> entry = this.getFirst();

		if (entry == null)
		{
			MaLiLib.LOGGER.error("ListDat: Exception refreshing Serializer; first entry is empty!");
			this.valueList.clear();
			this.serializer = null;
			this.valueType = DatType.EMPTY;
			return;
		}

		this.valueType = entry.getType();
		Codec<E> valueCodec = this.getValueCodec();

		if (valueCodec == null)
		{
			MaLiLib.LOGGER.error("ListDat: Exception refreshing Serializer; first entry CODEC is empty!");
			this.valueList.clear();
			this.serializer = null;
			this.valueType = DatType.EMPTY;
			return;
		}

		this.serializer = new Serializer<>(valueCodec);
	}

	public record Serializer<E>(Codec<E> codec) implements Codec<List<E>>
	{
		@Override
		public <T> DataResult<T> encode(List<E> input, DynamicOps<T> ops, T prefix)
		{
			if (codec() == null || input.isEmpty())
			{
				return DataResult.error(() -> "Empty List / Codec!");
			}

			ListBuilder<T> builder = ops.listBuilder();

			for (E entry : input)
			{
				builder.add(codec().encodeStart(ops, entry));
			}

			return builder.build(prefix);
		}

		@Override
		public <T> DataResult<Pair<List<E>, T>> decode(DynamicOps<T> ops, T input)
		{
			return ops.getList(input).flatMap(
					inst -> {
						final DecoderContext<T> decoder = new DecoderContext<>(ops);

						inst.accept(decoder::accept);
						return decoder.build();
					}
			);
		}

		private class DecoderContext<V>
		{
			private final DynamicOps<V> ops;
			private final List<E> values = new ArrayList<>();
			private final Stream.Builder<V> error = Stream.builder();
			private DataResult<Unit> results = DataResult.success(Unit.INSTANCE, Lifecycle.stable());

			private DecoderContext(final DynamicOps<V> ops)
			{
				this.ops = ops;
			}

			public void accept(final V value)
			{
				final DataResult<Pair<E, V>> valueEach = codec().decode(this.ops, value);
				valueEach.error().ifPresent(err -> this.error.add(value));
				valueEach.resultOrPartial().ifPresent(pair -> this.values.add(pair.getFirst()));
				this.results = this.results.apply2stable((res, ele) -> res, valueEach);
			}

			public DataResult<Pair<List<E>, V>> build()
			{
				final V errors = this.ops.createList(this.error.build());
				final Pair<List<E>, V> pair = Pair.of(List.copyOf(this.values), errors);

				return this.results.map(unit -> pair).setPartial(pair);
			}
		}
	}
}

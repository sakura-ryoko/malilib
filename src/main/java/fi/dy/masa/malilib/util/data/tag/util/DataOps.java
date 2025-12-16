package fi.dy.masa.malilib.util.data.tag.util;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.util.Util;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.*;

public class DataOps implements DynamicOps<BaseData>
{
	public static final DataOps INSTANCE = new DataOps();

	private DataOps() {}

	@Override
	public BaseData empty()
	{
		return EmptyData.INSTANCE;
	}

	@Override
	public BaseData emptyList()
	{
		return new ListData();
	}

	@Override
	public BaseData emptyMap()
	{
		return new CompoundData();
	}

	@Override
	public String toString()
	{
		return "DATA";
	}

	@Override
	public <U> U convertTo(DynamicOps<U> ops, BaseData data)
	{
		return switch (data.getType())
		{
			case Constants.NBT.TAG_BYTE -> ops.createByte(((ByteData) data).value);
			case Constants.NBT.TAG_SHORT -> ops.createShort(((ShortData) data).value);
			case Constants.NBT.TAG_INT -> ops.createInt(((IntData) data).value);
			case Constants.NBT.TAG_LONG -> ops.createLong(((LongData) data).value);
			case Constants.NBT.TAG_FLOAT -> ops.createFloat(((FloatData) data).value);
			case Constants.NBT.TAG_DOUBLE -> ops.createDouble(((DoubleData) data).value);
			case Constants.NBT.TAG_STRING -> ops.createString(((StringData) data).value);
			case Constants.NBT.TAG_BYTE_ARRAY -> ops.createByteList(ByteBuffer.wrap(((ByteArrayData) data).value));
			case Constants.NBT.TAG_INT_ARRAY -> ops.createIntList(Arrays.stream(((IntArrayData) data).value));
			case Constants.NBT.TAG_LONG_ARRAY -> ops.createLongList(Arrays.stream(((LongArrayData) data).value));
			case Constants.NBT.TAG_COMPOUND -> this.convertMap(ops, data);
			case Constants.NBT.TAG_LIST -> this.convertList(ops, data);
			case Constants.NBT.TAG_END -> ops.empty();
			default -> throw new RuntimeException("(DataOps) Invalid data type: " + data.getType());
		};
	}

	@Override
	public BaseData createNumeric(Number number)
	{
		return new DoubleData(number.doubleValue());
	}

	@Override
	public DataResult<Number> getNumberValue(BaseData data)
	{
		return data.asNumber().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "(DataOps) Not a number: "+data.toString()));
	}

	@Override
	public Number getNumberValue(BaseData data, Number defaultValue)
	{
		DataResult<Number> result = this.getNumberValue(data);

		if (result.hasResultOrPartial())
		{
			return result.getPartialOrThrow();
		}

		return defaultValue;
	}

	@Override
	public BaseData createByte(byte value)
	{
		return new ByteData(value);
	}

	@Override
	public BaseData createShort(short value)
	{
		return new ShortData(value);
	}

	@Override
	public BaseData createInt(int value)
	{
		return new IntData(value);
	}

	@Override
	public BaseData createLong(long value)
	{
		return new LongData(value);
	}

	@Override
	public BaseData createFloat(float value)
	{
		return new FloatData(value);
	}

	@Override
	public BaseData createDouble(double value)
	{
		return new DoubleData(value);
	}

	@Override
	public BaseData createBoolean(boolean value)
	{
		return new ByteData((byte) (value ? 1 : 0));
	}

	@Override
	public DataResult<Boolean> getBooleanValue(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_BYTE)
		{
			byte value = ((ByteData) data).value;

			if (value == 0 || value == 1)
			{
				return DataResult.success(value == 1);
			}
		}

		return DataResult.error(() -> "(DataOps) Not a boolean: "+data.toString());
	}

	@Override
	public BaseData createString(String s)
	{
		return new StringData(s);
	}

	@Override
	public DataResult<String> getStringValue(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_STRING)
		{
			return DataResult.success(((StringData) data).value);
		}

		return DataResult.error(() -> "(DataOps) Not a string: "+data.toString());
	}

	@Override
	public DataResult<BaseData> mergeToList(BaseData data, BaseData entry)
	{
		return createArrayFactory(data)
				.map(factory -> DataResult.success(factory.accept(entry).result()))
				.orElseGet(() -> DataResult.error(() -> "(DataOps) Not a list: "+data.toString()));
	}

	@Override
	public DataResult<BaseData> mergeToList(BaseData data, List<BaseData> entry)
	{
		return createArrayFactory(data)
				.map(factory -> DataResult.success(factory.acceptAll(entry).result()))
				.orElseGet(() -> DataResult.error(() -> "(DataOps) Not a list: "+data.toString()));
	}

	@Override
	public DataResult<BaseData> mergeToMap(BaseData data, BaseData key, BaseData entry)
	{
		if (data.getType() != Constants.NBT.TAG_COMPOUND && data.getType() != Constants.NBT.TAG_END)
		{
			return DataResult.error(() -> "(DataOps) Not a Map: "+data.toString());
		}
		else if (key.getType() == Constants.NBT.TAG_STRING)
		{
			String keyValue = ((StringData) key).value;
			CompoundData compoundData = data instanceof CompoundData comp ? comp.copy() : new CompoundData();

			compoundData.put(keyValue, entry);
			return DataResult.success(compoundData);
		}
		else
		{
			return DataResult.error(() -> "(DataOps) Not a String key: "+key.toString());
		}
	}

	@Override
	public DataResult<BaseData> mergeToMap(BaseData data, MapLike<BaseData> mapLike)
	{
		if (data.getType() != Constants.NBT.TAG_COMPOUND && data.getType() != Constants.NBT.TAG_END)
		{
			return DataResult.error(() -> "(DataOps) Not a Map: "+data.toString());
		}
		else
		{
			Iterator<Pair<BaseData, BaseData>> iter = mapLike.entries().iterator();

			if (!iter.hasNext())
			{
				return data == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(data);
			}
			else
			{
				CompoundData compoundData = data instanceof CompoundData comp ? comp.copy() : new CompoundData();
				List<BaseData> list = new ArrayList<>();

				iter.forEachRemaining(pair ->
				                      {
										  BaseData entry = pair.getFirst();

										  if (entry.getType() == Constants.NBT.TAG_STRING)
										  {
											  compoundData.put(((StringData) pair.getFirst()).value, pair.getSecond());
										  }
										  else
										  {
											  list.add(entry);
										  }
				                      });

				return !list.isEmpty() ? DataResult.error(() -> "(DataOps) Some keys are not strings: "+list.toString(), compoundData) : DataResult.success(compoundData);
			}
		}
	}

	@Override
	public DataResult<BaseData> mergeToMap(BaseData data, Map<BaseData, BaseData> map)
	{
		if (data.getType() != Constants.NBT.TAG_COMPOUND && data.getType() != Constants.NBT.TAG_END)
		{
			return DataResult.error(() -> "(DataOps) Not a Map: "+data.toString());
		}
		else if (map.isEmpty())
		{
			return data == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(data);
		}
		else
		{
			CompoundData compoundData = data instanceof CompoundData comp ? comp.copy() : new CompoundData();
			List<BaseData> list = new ArrayList<>();

			for (Map.Entry<BaseData, BaseData> entry : map.entrySet())
			{
				BaseData key = entry.getKey();

				if (key.getType() == Constants.NBT.TAG_STRING)
				{
					compoundData.put(((StringData) key).value, entry.getValue());
				}
				else
				{
					list.add(key);
				}
			}

			return !list.isEmpty() ? DataResult.error(() -> "(DataOps) Some keys are not strings: "+list.toString(), compoundData) : DataResult.success(compoundData);
		}
	}

	@Override
	public DataResult<Stream<Pair<BaseData, BaseData>>> getMapValues(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_COMPOUND)
		{
			return DataResult.success(
					((CompoundData) data).entrySet().stream()
					    .map(ent ->
							         Pair.of(this.createString(ent.getKey()), ent.getValue()))
			);
		}

		return DataResult.error(() -> "(DataOps) Not a Map: "+ data.toString());
	}

	@Override
	public DataResult<Consumer<BiConsumer<BaseData, BaseData>>> getMapEntries(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_COMPOUND)
		{
			return DataResult.success(
					biCons ->
					{
						for (Map.Entry<String, BaseData> entry : ((CompoundData) data).entrySet())
						{
							biCons.accept(this.createString(entry.getKey()), entry.getValue());
						}
					}
			);
		}

		return DataResult.error(() -> "(DataOps) Not a Map: "+data.toString());
	}

	@Override
	public DataResult<MapLike<BaseData>> getMap(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_COMPOUND)
		{
			CompoundData comp = (CompoundData) data;

			return DataResult.success(new MapLike<>()
			{
				@Override
				public @Nullable BaseData get(BaseData key)
				{
					if (key.getType() == Constants.NBT.TAG_STRING)
					{
						return comp.getData(((StringData) key).value).orElse(null);
					}
					else
					{
						throw new RuntimeException("(DataOps) Key is not a string: " + key.toString());
					}
				}

				@Override
				public @Nullable BaseData get(String key)
				{
					return comp.getData(key).orElse(null);
				}

				@Override
				public Stream<Pair<BaseData, BaseData>> entries()
				{
					return comp.entrySet().stream().map(
							entry -> Pair.of(DataOps.this.createString(entry.getKey()), entry.getValue()));
				}
			});
		}
		else
		{
			return DataResult.error(() -> "(DataOps) Not a Map: "+data.toString());
		}
	}

	@Override
	public BaseData createMap(Stream<Pair<BaseData, BaseData>> stream)
	{
		CompoundData data = new CompoundData();

		stream.forEach(pair ->
		               {
						   BaseData key = pair.getFirst();
			               BaseData value = pair.getSecond();

						   if (key.getType() == Constants.NBT.TAG_STRING)
						   {
							   data.put(((StringData) key).value, value);
						   }
						   else
						   {
							   throw new RuntimeException("(DataOps) Key is not a string: "+key.toString());
						   }
		               });

		return data;
	}

	@Override
	public DataResult<Stream<BaseData>> getStream(BaseData data)
	{
		if (data instanceof ArrayData array)
		{
			return DataResult.success(array.stream());
		}

		return DataResult.error(() -> "(DataOps) Not an Array: "+data.toString());
	}

	@Override
	public DataResult<Consumer<Consumer<BaseData>>> getList(BaseData data)
	{
		if (data instanceof ArrayData array)
		{
			return DataResult.success(array::forEach);
		}

		return DataResult.error(() -> "(DataOps) Not a List: "+data.toString());
	}

	@Override
	public BaseData createList(Stream<BaseData> stream)
	{
		return new ListData((ArrayList<BaseData>) stream.collect(Util.toArrayList()));
	}

	@Override
	public DataResult<ByteBuffer> getByteBuffer(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_BYTE_ARRAY)
		{
			return DataResult.success(ByteBuffer.wrap(((ByteArrayData) data).value));
		}

		return DynamicOps.super.getByteBuffer(data);
	}

	@Override
	public BaseData createByteList(ByteBuffer buf)
	{
		ByteBuffer buffer = buf.duplicate().clear();
		byte[] bs = new byte[buf.capacity()];

		buffer.get(0, bs, 0, bs.length);

		return new ByteArrayData(bs);
	}

	@Override
	public DataResult<IntStream> getIntStream(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_INT_ARRAY)
		{
			return DataResult.success(Arrays.stream(((IntArrayData) data).value));
		}

		return DynamicOps.super.getIntStream(data);
	}

	@Override
	public BaseData createIntList(IntStream stream)
	{
		return new IntArrayData(stream.toArray());
	}

	@Override
	public DataResult<LongStream> getLongStream(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_LONG_ARRAY)
		{
			return DataResult.success(Arrays.stream(((LongArrayData) data).value));
		}

		return DynamicOps.super.getLongStream(data);
	}

	@Override
	public BaseData createLongList(LongStream stream)
	{
		return new LongArrayData(stream.toArray());
	}

	@Override
	public RecordBuilder<BaseData> mapBuilder()
	{
		return new Builder();
	}

	@Override
	@Nullable
	public BaseData remove(BaseData data, String key)
	{
		if (data.getType() == Constants.NBT.TAG_COMPOUND)
		{
			return ((CompoundData) data).getData(key).orElse(null);
		}

		return null;
	}

	private static Optional<ArrayFactory> createArrayFactory(BaseData data)
	{
		if (data.getType() == Constants.NBT.TAG_END)
		{
			return Optional.of(new GenericArrayFactory());
		}
		else if (data.getType() == Constants.NBT.TAG_LIST)
		{
			return Optional.of(new GenericArrayFactory(((ListData) data)));
		}
		else if (data.getType() == Constants.NBT.TAG_BYTE_ARRAY)
		{
			return Optional.of(new ByteArrayFactory(((ByteArrayData) data).value));
		}
		else if (data.getType() == Constants.NBT.TAG_INT_ARRAY)
		{
			return Optional.of(new IntArrayFactory(((IntArrayData) data).value));
		}
		else if (data.getType() == Constants.NBT.TAG_LONG_ARRAY)
		{
			return Optional.of(new LongArrayFactory(((LongArrayData) data).value));
		}
		else
		{
			return Optional.empty();
		}
	}

	private interface ArrayFactory
	{
		ArrayFactory accept(BaseData data);

		default ArrayFactory acceptAll(Iterable<BaseData> iterable)
		{
			ArrayFactory listFactory = this;

			for (BaseData data : iterable)
			{
				listFactory = listFactory.accept(data);
			}

			return listFactory;
		}

		default ArrayFactory acceptAll(Stream<BaseData> stream)
		{
			return this.acceptAll(stream::iterator);
		}

		BaseData result();
	}

	private static class GenericArrayFactory implements ArrayFactory
	{
		private final ListData listData = new ListData();

		public GenericArrayFactory() {}

		public GenericArrayFactory(ListData listData)
		{
			this.listData.add(listData);
		}

		public GenericArrayFactory(IntArrayList list)
		{
			list.forEach(i -> this.listData.add(new IntData(i)));
		}

		public GenericArrayFactory(ByteArrayList list)
		{
			list.forEach(b -> this.listData.add(new ByteData(b)));
		}

		public GenericArrayFactory(LongArrayList list)
		{
			list.forEach(l -> this.listData.add(new LongData(l)));
		}

		@Override
		public ArrayFactory accept(BaseData data)
		{
			this.listData.add(data);
			return this;
		}

		@Override
		public BaseData result()
		{
			return this.listData;
		}
	}

	private static class ByteArrayFactory implements ArrayFactory
	{
		private final ByteArrayList values = new ByteArrayList();

		public ByteArrayFactory(byte[] bs)
		{
			this.values.addElements(0, bs);
		}

		@Override
		public ArrayFactory accept(BaseData data)
		{
			if (data.getType() == Constants.NBT.TAG_BYTE)
			{
				this.values.add(((ByteData) data).value);
				return this;
			}
			else
			{
				return new GenericArrayFactory(this.values).accept(data);
			}
		}

		@Override
		public BaseData result()
		{
			return new ByteArrayData(this.values.toByteArray());
		}
	}

	private static class IntArrayFactory implements ArrayFactory
	{
		private final IntArrayList values = new IntArrayList();

		public IntArrayFactory(int[] is)
		{
			this.values.addElements(0, is);
		}

		@Override
		public ArrayFactory accept(BaseData data)
		{
			if (data.getType() == Constants.NBT.TAG_INT)
			{
				this.values.add(((IntData) data).value);
				return this;
			}
			else
			{
				return new GenericArrayFactory(this.values).accept(data);
			}
		}

		@Override
		public BaseData result()
		{
			return new IntArrayData(this.values.toIntArray());
		}
	}

	private static class LongArrayFactory implements ArrayFactory
	{
		private final LongArrayList values = new LongArrayList();

		public LongArrayFactory(long[] ls)
		{
			this.values.addElements(0, ls);
		}

		@Override
		public ArrayFactory accept(BaseData data)
		{
			if (data.getType() == Constants.NBT.TAG_LONG)
			{
				this.values.add(((LongData) data).value);
				return this;
			}
			else
			{
				return new GenericArrayFactory(this.values).accept(data);
			}
		}

		@Override
		public BaseData result()
		{
			return new LongArrayData(this.values.toLongArray());
		}
	}

	private class Builder extends RecordBuilder.AbstractStringBuilder<BaseData, CompoundData>
	{
		protected Builder()
		{
			super(DataOps.this);
		}

		@Override
		protected CompoundData initBuilder()
		{
			return new CompoundData();
		}

		@Override
		protected CompoundData append(String key, BaseData value, CompoundData data)
		{
			data.put(key, value);
			return data;
		}

		@Override
		protected DataResult<BaseData> build(CompoundData data, BaseData prefix)
		{
			if (prefix == null || prefix.getType() == Constants.NBT.TAG_END)
			{
				return DataResult.success(data);
			}
			else if (prefix.getType() != Constants.NBT.TAG_COMPOUND)
			{
				return DataResult.error(() -> "(DataOps) Not a map: "+prefix.toString());
			}
			else
			{
				CompoundData result = ((CompoundData) prefix).copy();

				for (Map.Entry<String, BaseData> entry : data.entrySet())
				{
					result.put(entry.getKey(), entry.getValue().copy());
				}

				return DataResult.success(result);
			}
		}
	}
}

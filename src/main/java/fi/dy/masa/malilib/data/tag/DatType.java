package fi.dy.masa.malilib.data.tag;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

@ApiStatus.Experimental
public enum DatType implements StringIdentifiable
{
	BOOL ("bool"),
	INT ("int"),
	SHORT ("short"),
	LONG ("long"),
	FLOAT ("float"),
	DOUBLE ("dbl"),
	FRACTION ("fraction"),
	LIST ("list"),
	EMPTY ("empty"),		// Returns an Empty Type
	;

	public static final StringIdentifiable.EnumCodec<DatType> CODEC = StringIdentifiable.createCodec(DatType::values);
	public static final PacketCodec<ByteBuf, DatType> PACKET_CODEC = PacketCodecs.STRING.xmap(DatType::fromString, DatType::asString);
	public static final ImmutableList<DatType> VALUES = ImmutableList.copyOf(values());

	private final String name;

	DatType(String name)
	{
		this.name = name;
	}

	@Override
	public String asString()
	{
		return this.name;
	}

	@Nullable
	public static DatType fromString(String str)
	{
		for (DatType entry : values())
		{
			if (entry.asString().equalsIgnoreCase(str))
			{
				return entry;
			}
		}

		return null;
	}

	public static DatType fromClass(Class<?> type)
	{
		final String name = type.getName();

		switch (name)
		{
			case "Boolean" -> { return BOOL; }
			case "Integer" -> { return INT; }
			case "Short" -> { return SHORT; }
			case "Long" -> { return LONG; }
			case "Float" -> { return FLOAT; }
			case "Double" -> { return DOUBLE; }
			case "Fraction" -> { return FRACTION; }
			case "List" -> { return LIST; }
			default -> { return EMPTY; }
		}
	}

	@Nullable
	public Codec<?> codec()
	{
		switch (this.name())
		{
			case "bool" -> { return BoolDat.CODEC; }
			case "int" -> { return IntDat.CODEC; }
			case "short" -> { return ShortDat.CODEC; }
			case "long" -> { return LongDat.CODEC; }
			case "float" -> { return FloatDat.CODEC; }
			case "dbl" -> { return DoubleDat.CODEC; }
			case "fraction" -> { return FractionDat.CODEC; }
//			case "list" -> { return ListDat.CODEC; }
			default -> { return null; }
		}
	}
}

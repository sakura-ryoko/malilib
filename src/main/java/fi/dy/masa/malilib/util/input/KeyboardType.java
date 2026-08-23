package fi.dy.masa.malilib.util.input;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeyboardType implements IConfigOptionListEntry, StringIdentifiable
{
	QWERTY("qwerty", "malilib.label.keyboard_type.qwerty"),
	AZERTY("azerty", "malilib.label.keyboard_type.azerty"),
	;

	public static final EnumCodec<@NotNull KeyboardType> CODEC = StringIdentifiable.createCodec(KeyboardType::values);
	public static final PacketCodec<@NotNull ByteBuf, @NotNull KeyboardType> PACKET_CODEC = PacketCodecs.STRING.xmap(KeyboardType::fromStringStatic, KeyboardType::asString);
	public static final ImmutableList<@NotNull KeyboardType> VALUES = ImmutableList.copyOf(values());

	private final String configString;
	private final String translationKey;

	KeyboardType(String configString, String translationKey)
	{
		this.configString = configString;
		this.translationKey = translationKey;
	}

	@Override
	public String getStringValue()
	{
		return this.configString;
	}

	@Override
	public String getDisplayName()
	{
		return StringUtils.translate(this.translationKey);
	}

	@Override
	public @Nonnull String asString()
	{
		return this.configString;
	}

	@Override
	public IConfigOptionListEntry cycle(boolean forward)
	{
		int id = this.ordinal();

		if (forward)
		{
			if (++id >= values().length)
			{
				id = 0;
			}
		}
		else
		{
			if (--id < 0)
			{
				id = values().length - 1;
			}
		}

		return values()[id % values().length];
	}

	@Override
	public KeyboardType fromString(String name)
	{
		return fromStringStatic(name);
	}

	public static KeyboardType fromStringStatic(String name)
	{
		for (KeyboardType mode : KeyboardType.VALUES)
		{
			if (mode.configString.equalsIgnoreCase(name))
			{
				return mode;
			}
		}

		return KeyboardType.QWERTY;
	}
}

package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum HandSlot implements IConfigOptionListEntry, StringIdentifiable
{
	ANY             ("any",         "malilib.label.hand_slot.any",       null),
	MAIN_HAND       ("main_hand",   "malilib.label.hand_slot.main_hand",      Hand.MAIN_HAND),
	OFF_HAND        ("off_hand",    "malilib.label.hand_slot.off_hand",     Hand.OFF_HAND),
	;

	public static final EnumCodec<@NotNull HandSlot> CODEC = StringIdentifiable.createCodec(HandSlot::values);
	public static final PacketCodec<@NotNull ByteBuf, @NotNull HandSlot> PACKET_CODEC = PacketCodecs.STRING.xmap(HandSlot::fromStringStatic, HandSlot::asString);
	public static final ImmutableList<@NotNull HandSlot> VALUES = ImmutableList.copyOf(values());

	private final String configString;
	private final String translationKey;
	private final Hand hand;

	HandSlot(String configString, String translationKey, Hand hand)
	{
		this.configString = configString;
		this.translationKey = translationKey;
		this.hand = hand;
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

	@Nullable
	public Hand getHand()
	{
		return this.hand;
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
	public HandSlot fromString(String name)
	{
		return fromStringStatic(name);
	}

	public static HandSlot fromStringStatic(String name)
	{
		for (HandSlot mode : HandSlot.VALUES)
		{
			if (mode.configString.equalsIgnoreCase(name))
			{
				return mode;
			}
		}

		return HandSlot.ANY;
	}
}

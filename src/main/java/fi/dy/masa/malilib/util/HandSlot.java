package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum HandSlot implements IConfigOptionListEntry, StringRepresentable
{
	ANY             ("any",         "malilib.label.hand_slot.any",       null),
	MAIN_HAND       ("main_hand",   "malilib.label.hand_slot.main_hand",  InteractionHand.MAIN_HAND),
	OFF_HAND        ("off_hand",    "malilib.label.hand_slot.off_hand",   InteractionHand.OFF_HAND),
	;

	public static final EnumCodec<@NotNull HandSlot> CODEC = StringRepresentable.fromEnum(HandSlot::values);
	public static final StreamCodec<@NotNull ByteBuf, @NotNull HandSlot> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(HandSlot::fromStringStatic, HandSlot::getSerializedName);
	public static final ImmutableList<@NotNull HandSlot> VALUES = ImmutableList.copyOf(values());

	private final String configString;
	private final String translationKey;
	private final InteractionHand hand;

	HandSlot(String configString, String translationKey, InteractionHand hand)
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
	public @Nonnull String getSerializedName()
	{
		return this.configString;
	}

	@Nullable
	public InteractionHand getHand()
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

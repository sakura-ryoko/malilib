package malilib.util.game;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import malilib.util.MathUtils;
import malilib.util.StringUtils;
import malilib.util.data.Constants;
import malilib.util.data.DyeColorCode;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.converter.DataConverterNbt;
import malilib.util.data.tag.util.DataTypeUtils;
import malilib.util.game.wrap.DefaultedList;
import malilib.util.nbt.NbtKeys;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * The purpose of this Library is to fully utilize {@link malilib.util.data.tag.DataView} Tags to parse NBT tags for use in downstream mods;
 * such as {@link Entity} Variants for MiniHUD's Info Lines; or other specific {@link malilib.util.data.tag.DataView} needs.
 * It should not fail to mimic the various 'readNbt()' / 'writeNbt()' type functions under Vanilla's {@link Entity} system,
 * without needing to instance a new {@link Entity} object; which can potentially break other people's mods if used too often.
 * These are more important in later versions of Minecraft; especially when it can be hard to remember every NBT tag name;
 * or CODEC method; to say; export a Mob's {@link AttributeMap} from Raw Tags, and then find a specific value; for example.
 * Utilizing {@link NbtKeys} is also very helpful to track changes across versions of Minecraft.
 */
public class DataEntityUtils
{
	/**
	 * Get the Entity Type from the Data Tag
	 * @param data -
	 * @return -
	 */
	public static Optional<Identifier> getEntityType(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
		{
			Identifier id = Identifier.of(data.getString(NbtKeys.ID));

			if (id != null && EntityList.REGISTRY.containsKey(id))
			{
				return Optional.of(id);
			}
		}

		return Optional.empty();
	}

	/**
	 * Get the Entity Name from the Data Tag, in it's translated form.
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getEntityName(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		Identifier id = getEntityType(data).orElse(null);

		if (id != null)
		{
			String translationKey = EntityList.getTranslationName(id);

			if (translationKey != null)
			{
				return Optional.ofNullable(StringUtils.translate(translationKey));
			}
			else
			{
				return Optional.ofNullable(StringUtils.translate("entity.generic.name"));
			}
		}

		return Optional.empty();
	}

	/**
	 * Write an Entity Type to Data Tag
	 *
	 * @param entity -
	 * @param dataIn -
	 * @return -
	 */
	public <T extends Entity> CompoundData setEntityType(Class<T> entity, @Nullable CompoundData dataIn) // @Nonnull RegistryAccess registry
	{
		CompoundData data = new CompoundData();
		ResourceLocation rl = EntityList.getKey(entity);

		if (rl != null)
		{
			if (dataIn != null)
			{
				dataIn.putString(NbtKeys.ID, rl.toString());
				return dataIn;
			}
			else
			{
				data.putString(NbtKeys.ID, rl.toString());
			}
		}

		return data;
	}

	/**
	 * Get an Entity UUID from Data Tags
	 * @param data -
	 * @return -
	 */
	public static Optional<UUID> getUUID(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.UUID_MOST, Constants.NBT.TAG_ANY_NUMERIC) &&
			data.contains(NbtKeys.UUID_LEAST, Constants.NBT.TAG_ANY_NUMERIC))
		{
			return Optional.ofNullable(DataTypeUtils.readUuidFromLongs(data, NbtKeys.UUID_MOST, NbtKeys.UUID_LEAST));
		}
		else if (data.contains(NbtKeys.UUID, Constants.NBT.TAG_STRING))
		{
			return Optional.of(UUID.fromString(data.getString(NbtKeys.UUID)));
		}
		else if (data.contains(NbtKeys.UUID, Constants.NBT.TAG_INT_ARRAY))
		{
			return Optional.ofNullable(DataTypeUtils.readUuidFromIntArray(data, NbtKeys.UUID));
		}

		return Optional.empty();
	}

	/**
	 * Read the CustomName from Data Tag, as a String
	 * @implNote The Text Serialization changes in future versions, such as 1.20.2 and ~1.21.5
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getCustomNameStr(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_COMPOUND))
		{
			return Optional.ofNullable(data.getString(NbtKeys.CUSTOM_NAME));
		}

		return Optional.empty();
	}

	/**
	 * Get either the Custom Name or the Entity Name from Data Tag
	 * @implNote The Text Serialization changes in future versions, such as 1.20.2 and ~1.21.5
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getCustomOrEntityName(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		final String customName = getCustomNameStr(data).orElse(null);
		final Optional<String> name = getEntityName(data);

		if (customName != null && !customName.isEmpty())
		{
			return Optional.of(customName);
		}

		return name;
	}

	/**
	 * Read the CustomName from Data Tag, as a Text ITextComponent.
	 * This function does not read the Team Color, if present.
	 * @implNote The Text Serialization changes in future versions, such as 1.20.2 and ~1.21.5
	 * @param data -
	 * @return -
	 */
	public static Optional<ITextComponent> getCustomNameText(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		String name = getCustomNameStr(data).orElse(null);
		if (name == null) { return Optional.empty(); }

		UUID uuid = getUUID(data).orElse(null);
		ITextComponent text = new TextComponentString(name);    // Omits the Scoreboard / Team Color

		getNameHoverEvent(data, uuid).ifPresent(
				hover -> text.getStyle().setHoverEvent(hover));

		if (uuid != null)
		{
			text.getStyle().setInsertion(uuid.toString());
		}

		return Optional.of(text);
	}

	/**
	 * Get an Entity Hover Event from Data Tag
	 * @param data -
	 * @return -
	 */
	public static Optional<HoverEvent> getNameHoverEvent(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		return getNameHoverEvent(data, null);
	}

	/**
	 * Get an Entity Hover Event from Data Tag
	 * @implNote The Text Serialization changes in future versions, such as 1.20.2 and ~1.21.5
	 * @param data -
	 * @param uuid -
	 * @return -
	 */
	public static Optional<HoverEvent> getNameHoverEvent(@Nonnull CompoundData data, @Nullable UUID uuid) // @Nonnull RegistryAccess registry
	{
		Identifier id = getEntityType(data).orElse(null);

		if (id != null)
		{
			if (uuid == null)
			{
				uuid = getUUID(data).orElse(null);
			}

			if (uuid != null)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				final String uuidStr = uuid.toString();
				final String name = getCustomOrEntityName(data).orElse("unknown");

				nbt.setString("id", uuidStr);
				nbt.setString("type", id.toString());
				nbt.setString("name", name);

				return Optional.of(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(nbt.toString())));
			}
		}

		return Optional.empty();
	}

	/**
	 * Get the AttributeMap with modifiers from Data Tag
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<AttributeMap> getAttributeMap(@Nonnull CompoundData data)
	{
		Identifier id = getEntityType(data).orElse(null);

		if (id != null && data.containsList(NbtKeys.ATTRIBUTES, Constants.NBT.TAG_COMPOUND))
		{
			AttributeMap container = new AttributeMap();
			ListData list = data.getList(NbtKeys.ATTRIBUTES, Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++)
			{
				CompoundData entry = list.getCompoundAt(i);

				if (entry != null && entry.contains(NbtKeys.ATTR_MOD_NAME, Constants.NBT.TAG_STRING))
				{
					final String name = entry.getStringOrDefault(NbtKeys.ATTR_MOD_NAME, "");
					IAttributeInstance inst = container.getAttributeInstanceByName(name);
					if (inst == null) { continue; }

					inst.setBaseValue(entry.getDoubleOrDefault(NbtKeys.ATTRIB_BASE, 0.0D));
					getAttributeModifiersForInstance(inst, entry);
				}
			}

			return Optional.of(container);
		}

		return Optional.empty();
	}

	/**
	 * Read the Attribute Modifiers into the Attribute Instance from Data Tag
	 * @param inst -
	 * @param data -
	 */
	public static void getAttributeModifiersForInstance(@Nonnull IAttributeInstance inst, @Nonnull CompoundData data)
	{
		if (data.containsList(NbtKeys.ATTRIB_MODIFIERS, Constants.NBT.TAG_COMPOUND))
		{
			ListData modifiers = data.getList(NbtKeys.ATTRIB_MODIFIERS, Constants.NBT.TAG_COMPOUND);

			for (int j = 0; j < modifiers.size(); j++)
			{
				CompoundData mod = modifiers.getCompoundAt(j);
				if (mod == null || mod.isEmpty()) { continue; }

				UUID uuid = DataTypeUtils.readUuidFromLongs(mod, NbtKeys.UUID_MOST, NbtKeys.UUID_LEAST);
				AttributeModifier modifier;

				try
				{
					modifier = new AttributeModifier(uuid,
					                                 mod.getStringOrDefault(NbtKeys.ATTR_MOD_NAME, ""),
					                                 mod.getDoubleOrDefault(NbtKeys.ATTR_MOD_AMOUNT, 0.0D),
					                                 mod.getIntOrDefault(NbtKeys.ATTR_MOD_OPERATION, 0));
				}
				catch (Exception e) { continue; }

				if (inst.getModifier(modifier.getID()) != null)
				{
					inst.removeModifier(modifier.getID());
				}

				inst.applyModifier(modifier);
			}
		}
	}

	/**
	 * Get a specific Attribute Instance from Data Tag
	 * @param data -
	 * @param attribute -
	 * @return -
	 */
	public static Optional<IAttributeInstance> getAttributeInstance(@Nonnull CompoundData data, IAttribute attribute)
	{
		AttributeMap map = getAttributeMap(data).orElse(null);
		if (map == null) { return Optional.empty(); }

		return Optional.ofNullable(map.getAttributeInstanceByName(attribute.getName()));
	}

	/** Get a specified Attribute Base Value from Data Tag
	 *
	 * @param data -
	 * @param attribute -
	 * @return -
	 */
	public static Optional<Double> getAttributeBaseValue(@Nonnull CompoundData data, IAttribute attribute)
	{
		IAttributeInstance inst = getAttributeInstance(data, attribute).orElse(null);

		if (inst != null)
		{
			return Optional.of(inst.getBaseValue());
		}

		return Optional.empty();
	}

	/** Get a specified Attribute Value from Data Tag
	 *
	 * @param data -
	 * @param attribute -
	 * @return -
	 */
	public static Optional<Double> getAttributeValue(@Nonnull CompoundData data, IAttribute attribute)
	{
		IAttributeInstance inst = getAttributeInstance(data, attribute).orElse(null);

		if (inst != null)
		{
			return Optional.of(inst.getAttributeValue());
		}

		return Optional.empty();
	}

	/**
	 * Get an entities' Health / Max Health from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Double, Double>> getHealth(@Nonnull CompoundData data)
	{
		double health = 0.0F;
		double maxHealth;

		if (data.contains(NbtKeys.HEALTH, Constants.NBT.TAG_FLOAT))
		{
			health = data.getFloatOrDefault(NbtKeys.HEALTH, 0.0F);
		}

		maxHealth = SharedMonsterAttributes.MAX_HEALTH.clampValue(getAttributeValue(data, SharedMonsterAttributes.MAX_HEALTH).orElse(20.0D));
		health = MathUtils.clamp(health, 0.0F, maxHealth);

		return Optional.of(Pair.of(health, maxHealth));
	}

	/**
	 * Get an entities Movement Speed, and Jump Strength attributes from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Double, Double>> getSpeedAndJumpStrength(@Nonnull CompoundData data)
	{
		AttributeMap map = getAttributeMap(data).orElse(null);
		double moveSpeed = 0.0D;
		double jumpStrength = 0.0D;

		if (map != null)
		{
			IAttributeInstance inst = map.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);

			if (inst != null)
			{
				moveSpeed = inst.getAttributeValue();
			}

			// There is probably a better way to get the attribute type, such as using an access widener
			inst = map.getAttributeInstanceByName("horse.jumpStrength");

			if (inst != null)
			{
				jumpStrength = inst.getAttributeValue();
			}
		}

		if (moveSpeed == 0.0D && jumpStrength == 0.0D) { return Optional.empty(); }

		return Optional.of(Pair.of(moveSpeed, jumpStrength));
	}

	/**
	 * Get Active Effects from Data Tag
	 * @param data -
	 * @return -
	 * @implNote `PotionEffect` get replaced/renamed to 'MobEffect'
	 */
	public static ImmutableMap<Potion, PotionEffect> getActiveEffects(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		ImmutableMap.Builder<Potion, PotionEffect> builder = ImmutableMap.builder();

		if (data.containsList(NbtKeys.ACTIVE_EFFECTS, Constants.NBT.TAG_COMPOUND))
		{
			ListData list = data.getList(NbtKeys.ACTIVE_EFFECTS, Constants.NBT.TAG_LIST);

			for (int i = 0; i < list.size(); i++)
			{
				CompoundData entry = list.getCompoundAt(i);
				if (entry == null) { continue; }

				getPotionEffect(entry).ifPresent(
						effect -> builder.put(effect.getPotion(), effect));
			}
		}

		return builder.build();
	}

	/**
	 * Get a Potion Effect from Data Tag
	 * @param data -
	 * @return -
	 * @implNote `PotionEffect` get replaced/renamed to 'MobEffect'
	 */
	public static Optional<PotionEffect> getPotionEffect(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.EFFECT_ID, Constants.NBT.TAG_BYTE))
		{
			try
			{
				final byte id = data.getByteOrDefault(NbtKeys.EFFECT_ID, (byte) -1);
				Potion pot = Potion.REGISTRY.getObjectById(id);

				if (pot != null)
				{
					final byte amp = data.getByteOrDefault(NbtKeys.EFFECT_AMPLIFIER, (byte) 0);
					final int dur = data.getIntOrDefault(NbtKeys.EFFECT_DURATION, 0);
					final boolean amb = data.getBooleanOrDefault(NbtKeys.EFFECT_AMBIENT, false);
					boolean particles = data.getBooleanOrDefault(NbtKeys.EFFECT_SHOW_PART, true);

					return Optional.of(new PotionEffect(pot, dur, (amp < 0 ? 0 : amp), amb, particles));
				}
			}
			catch (Exception ignored) {}
		}

		return Optional.empty();
	}

	/**
	 * Get a ItemStack List of all Equipment Slots
	 *   0/1   [{MainHand}, {OffHand}]
	 * 2/3/4/5 [{Head}, {Chest}, {Legs}, {Feet}]
	 *   6/7   [{BodyArmor}, {Saddle}]
	 * @implNote Note that slots 6/7 are added under 1.21.5+; so we are emulating the same behavior.
	 * @param data -
	 * @return -
	 */
	public static DefaultedList<ItemStack> getEquipmentSlots(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(8, ItemStack.EMPTY);

		// 1.21.5+
		if (data.contains(NbtKeys.NEW_EQUIPMENT, Constants.NBT.TAG_COMPOUND))
		{
			getNewEquipmentSlots(data.getCompound(NbtKeys.NEW_EQUIPMENT), stacks);
		}
		else
		{
			getLegacyEquipmentSlots(data, stacks);
		}

		return stacks;
	}

	/**
	 * Get Entity Equipment from 1.21.5+ type Equipment slots from New Equipment Data Tag
	 * @param data -
	 * @param stacks -
	 */
	public static void getNewEquipmentSlots(@Nonnull CompoundData data, @Nonnull DefaultedList<ItemStack> stacks)
	// @Nonnull RegistryAccess registry
	{
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			if (data.contains(slot.getName(), Constants.NBT.TAG_COMPOUND))
			{
				ItemStack stack = DataTypeUtils.toItemStack(data.getCompoundOrDefault(slot.getName(), new CompoundData())).orElse(ItemStack.EMPTY);

				if (!stack.isEmpty())
				{
					stacks.set(slot.getSlotIndex(), stack.copy());
				}
			}
		}

		if (data.contains(NbtKeys.NEW_ANIMAL_ARMOR, Constants.NBT.TAG_COMPOUND))
		{
			ItemStack bodyArmor = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.NEW_ANIMAL_ARMOR, new CompoundData())).orElse(ItemStack.EMPTY);

			if (!bodyArmor.isEmpty())
			{
				stacks.set(6, bodyArmor.copy());
			}
		}

		if (data.contains(NbtKeys.NEW_SADDLE, Constants.NBT.TAG_COMPOUND))
		{
			ItemStack saddle = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.NEW_SADDLE, new CompoundData())).orElse(ItemStack.EMPTY);

			if (!saddle.isEmpty())
			{
				stacks.set(7, saddle.copy());
			}
		}
	}

	/**
	 * Get Entity Equipment from pre-1.21.5 type Equipment slots.
	 * @param data -
	 * @param stacks -
	 */
	public static void getLegacyEquipmentSlots(@Nonnull CompoundData data, @Nonnull DefaultedList<ItemStack> stacks)
	// @Nonnull RegistryAccess registry
	{
		stacks.addAll(getLegacyHandSlots(data));
		stacks.addAll(getLegacyArmorSlots(data));

		ItemStack bodyArmor = getLegacyHorseArmor(data).orElse(ItemStack.EMPTY);
		ItemStack saddle = getLegacySaddle(data).orElse(ItemStack.EMPTY);

		if (!bodyArmor.isEmpty())
		{
			stacks.set(6, bodyArmor.copy());
		}
		if (!saddle.isEmpty())
		{
			stacks.set(7, saddle.copy());
		}
	}

	/**
	 * Get Hand Item Slots from Data Tag
	 * @implNote This is removed under 1.21.5
	 * @param data -
	 * @return -
	 */
	public static DefaultedList<ItemStack> getLegacyHandSlots(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(8, ItemStack.EMPTY);

		if (data.containsList(NbtKeys.HAND_ITEMS, Constants.NBT.TAG_COMPOUND))
		{
			ListData list = data.getList(NbtKeys.HAND_ITEMS, Constants.NBT.TAG_LIST);

			for (int i = 0; i < list.size(); i++)
			{
				CompoundData entry = list.getCompoundAt(i);
				if (entry == null) { continue; }
				if (i > 8) break;

				processEachSlot(stacks, EntityEquipmentSlot.Type.HAND, entry, i);
			}
		}

		return stacks;
	}

	/**
	 * Get Armor Item Slots from Data Tag
	 * @implNote This is removed under 1.21.5
	 * @param data -
	 * @return -
	 */
	public static DefaultedList<ItemStack> getLegacyArmorSlots(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(8, ItemStack.EMPTY);

		if (data.containsList(NbtKeys.ARMOR_ITEMS, Constants.NBT.TAG_COMPOUND))
		{
			ListData list = data.getList(NbtKeys.ARMOR_ITEMS, Constants.NBT.TAG_LIST);

			for (int i = 0; i < list.size(); i++)
			{
				CompoundData entry = list.getCompoundAt(i);
				if (entry == null) { continue; }
				if (i > 8) break;

				processEachSlot(stacks, EntityEquipmentSlot.Type.ARMOR, entry, i);
			}
		}

		return stacks;
	}

	/**
	 * Get Body Armor slot from Data Tag
	 * @implNote This is an actual Equipment Slot under 1.21.5+
	 * @param data -
	 * @return -
	 */
	public static Optional<ItemStack> getLegacyHorseArmor(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.HORSE_ARMOR, Constants.NBT.TAG_COMPOUND))
		{
			return DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.HORSE_ARMOR, new CompoundData()));
		}

		return Optional.empty();
	}

	/**
	 * Get A mount's Saddle from Data Tag
	 * @implNote The Saddle is an actual Equipment Slot under 1.21.5+
	 * @param data -
	 * @return -
	 */
	public static Optional<ItemStack> getLegacySaddle(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.SADDLE, Constants.NBT.TAG_COMPOUND))
		{
			return DataTypeUtils.toItemStack(data.getCompound(NbtKeys.HORSE_ARMOR));
		}

		return Optional.empty();
	}

	/**
	 * Get a ItemStack List of all Equipped Horse/Wolf/Llama/Camel/Etc Slots
	 * 0/1 [{BodyArmor}, {Saddle}]
	 *
	 * @param data ()
	 * @return ()
	 */
	public static DefaultedList<ItemStack> getHorseEquipment(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		DefaultedList<ItemStack> list = DefaultedList.ofSize(2, ItemStack.EMPTY);

		ItemStack bodyArmor = getLegacyHorseArmor(data).orElse(ItemStack.EMPTY);
		ItemStack saddle = getLegacySaddle(data).orElse(ItemStack.EMPTY);

		if (!bodyArmor.isEmpty())
		{
			list.set(0, bodyArmor.copy());
		}

		if (!saddle.isEmpty())
		{
			list.set(1, saddle.copy());
		}

		return list;
	}

	/**
	 * Internal Process for mapping all Stacks to the correct Equipment Slot.
	 * @param stacks -
	 * @param type -
	 * @param entry -
	 * @param index -
	 */
	private static void processEachSlot(@Nonnull DefaultedList<ItemStack> stacks,
										EntityEquipmentSlot.Type type,
	                                    @Nonnull CompoundData entry, int index)
	// @Nonnull RegistryAccess registry
	{
		ItemStack stack = DataTypeUtils.toItemStack(entry).orElse(ItemStack.EMPTY);;

		if (type == EntityEquipmentSlot.Type.HAND)
		{
			if (EntityEquipmentSlot.MAINHAND.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
			else if (EntityEquipmentSlot.OFFHAND.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
		}
		else if (type == EntityEquipmentSlot.Type.ARMOR)
		{
			if (EntityEquipmentSlot.FEET.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
			else if (EntityEquipmentSlot.LEGS.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
			else if (EntityEquipmentSlot.CHEST.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
			else if (EntityEquipmentSlot.HEAD.getSlotIndex() == index)
			{
				stacks.set(index, stack);
			}
		}
		else if (index == 6)
		{
			// Horse Armor
			stacks.set(index, stack);
		}
		else if (index == 7)
		{
			// Saddle
			stacks.set(index, stack);
		}
	}

	/**
	 * Get A Tamable owner UUID from Data Tag, and if the mob is sitting
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<UUID, Boolean>> getTamableOwner(@Nonnull CompoundData data)
	{
		UUID uuid = null; // new UUID(0L, 0L) ?
		boolean sitting = false;

		if (data.contains(NbtKeys.TAMABLE_OWNER, Constants.NBT.TAG_STRING))
		{
			uuid = UUID.fromString(data.getStringOrDefault(NbtKeys.TAMABLE_OWNER, ""));
		}
		else if (data.contains(NbtKeys.OWNER, Constants.NBT.TAG_INT_ARRAY))
		{
			uuid = DataTypeUtils.readUuidFromIntArray(data, NbtKeys.OWNER);
		}

		if (data.contains(NbtKeys.SITTING, Constants.NBT.TAG_BYTE))
		{
			sitting = data.getBoolean(NbtKeys.SITTING);
		}

		if (uuid == null) { return Optional.empty(); }

		return Optional.of(Pair.of(uuid, sitting));
	}

	/**
	 * Get the Common Age / ForcedAge data from Data Tag
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Integer, Integer>> getAge(@Nonnull CompoundData data)
	{
		int breedingAge = 0;
		int forcedAge = 0;

		if (data.contains(NbtKeys.GROWING_AGE, Constants.NBT.TAG_INT))
		{
			breedingAge = data.getInt(NbtKeys.GROWING_AGE);
		}

		if (data.contains(NbtKeys.FORCED_AGE, Constants.NBT.TAG_INT))
		{
			forcedAge = data.getInt(NbtKeys.FORCED_AGE);
		}

		if (breedingAge == 0 && forcedAge == 0) { return Optional.empty(); }

		return Optional.of(Pair.of(breedingAge, forcedAge));
	}

	/**
	 * Get the Merchant Trade Offer's Object from Data Tag
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<MerchantRecipeList> getTradeOffers(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.TRADE_OFFERS, Constants.NBT.TAG_COMPOUND))
		{
			return Optional.of(new MerchantRecipeList(DataConverterNbt.toVanillaCompound(data.getCompoundOrDefault(NbtKeys.TRADE_OFFERS, new CompoundData()))));
		}

		return Optional.empty();
	}

	/**
	 * Get the Merchant Trade Offer's as a List from Data Tag
	 *
	 * @param data -
	 * @return -
	 */
	public static ImmutableList<MerchantRecipe> getTradeOfferRecipesAsVanilla(@Nonnull CompoundData data)
	{
		ImmutableList.Builder<MerchantRecipe> builder = ImmutableList.builder();

		if (data.contains(NbtKeys.TRADE_OFFERS, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData tradeOffers = data.getCompoundOrDefault(NbtKeys.TRADE_OFFERS, new CompoundData());

			if (tradeOffers.containsList(NbtKeys.TRADE_RECIPES, Constants.NBT.TAG_COMPOUND))
			{
				ListData list = tradeOffers.getList(NbtKeys.TRADE_RECIPES, Constants.NBT.TAG_COMPOUND);

				for (int i = 0; i < list.size(); i++)
				{
					CompoundData entry = list.getCompoundAt(i);
					if (entry == null || entry.isEmpty()) { continue; }

					builder.add(new MerchantRecipe(DataConverterNbt.toVanillaCompound(entry)));
				}
			}
		}

		return builder.build();
	}

	/**
	 * Get the Merchant Trade Offer's as a List from Data Tag,
	 * in the form of a {@link TradeData} List in<br>
	 * <b><u>'buy, buyB, sell'</b></u>, etc format.
	 *
	 * @param data -
	 * @return -
	 */
	public static ImmutableList<TradeData> getTradeOfferRecipesAsData(@Nonnull CompoundData data)
	{
		ImmutableList.Builder<TradeData> builder = ImmutableList.builder();

		if (data.contains(NbtKeys.TRADE_OFFERS, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData tradeOffers = data.getCompoundOrDefault(NbtKeys.TRADE_OFFERS, new CompoundData());

			if (tradeOffers.containsList(NbtKeys.TRADE_RECIPES, Constants.NBT.TAG_COMPOUND))
			{
				ListData list = tradeOffers.getList(NbtKeys.TRADE_RECIPES, Constants.NBT.TAG_COMPOUND);

				for (int i = 0; i < list.size(); i++)
				{
					CompoundData entry = list.getCompoundAt(i);
					if (entry == null || entry.isEmpty()) { continue; }

					getTradeData(entry).ifPresent(builder::add);
				}
			}
		}

		return builder.build();
	}

	/**
	 * Return a {@link TradeData} of a single Merchant's <b><u>'buy, buyB, sell'</b></u>, etc trade.
	 * @param data -
	 * @return -
	 */
	public static Optional<TradeData> getTradeData(@Nonnull CompoundData data)
	{
		ItemStack buy = ItemStack.EMPTY;
		ItemStack buyB = null;      // Null if not found, using EMPTY here breaks future versions
		ItemStack sell = ItemStack.EMPTY;
		int uses = 0;
		int maxUses = 4;
		boolean rewardsExp = true;
		int specialPrice = 0;
		int demand = 0;
		float multiplier = 0.0F;
		int xp = 1;

		if (data.contains(NbtKeys.TRADE_BUY, Constants.NBT.TAG_COMPOUND))
		{
			buy = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.TRADE_BUY, new CompoundData())).orElse(ItemStack.EMPTY);;
		}
		if (data.contains(NbtKeys.TRADE_BUY_B, Constants.NBT.TAG_COMPOUND))
		{
			buyB = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.TRADE_BUY_B, new CompoundData())).orElse(ItemStack.EMPTY);;
		}
		if (data.contains(NbtKeys.TRADE_SELL, Constants.NBT.TAG_COMPOUND))
		{
			sell = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.TRADE_SELL, new CompoundData())).orElse(ItemStack.EMPTY);;
		}
		if (data.contains(NbtKeys.TRADE_USES, Constants.NBT.TAG_INT))
		{
			uses = data.getInt(NbtKeys.TRADE_USES);
		}
		if (data.contains(NbtKeys.TRADE_MAX_USES, Constants.NBT.TAG_INT))
		{
			maxUses = data.getInt(NbtKeys.TRADE_MAX_USES);
		}
		if (data.contains(NbtKeys.TRADE_REWARD_EXP, Constants.NBT.TAG_BYTE))
		{
			rewardsExp = data.getBoolean(NbtKeys.TRADE_REWARD_EXP);
		}
		if (data.contains(NbtKeys.ORG_TRADE_SP_PRICE, Constants.NBT.TAG_INT))
		{
			specialPrice = data.getInt(NbtKeys.ORG_TRADE_SP_PRICE);
		}
		if (data.contains(NbtKeys.ORG_TRADE_DEMAND, Constants.NBT.TAG_INT))
		{
			demand = data.getInt(NbtKeys.ORG_TRADE_DEMAND);
		}
		if (data.contains(NbtKeys.ORG_TRADE_PRICE_MUL, Constants.NBT.TAG_FLOAT))
		{
			multiplier = data.getFloat(NbtKeys.ORG_TRADE_PRICE_MUL);
		}
		if (data.contains(NbtKeys.ORG_TRADE_XP, Constants.NBT.TAG_INT))
		{
			xp = data.getInt(NbtKeys.ORG_TRADE_XP);
		}

		if (buy.isEmpty() && sell.isEmpty()) { return Optional.empty(); }

		return Optional.of(new TradeData(buy, buyB, sell, uses, maxUses, rewardsExp, specialPrice, demand, multiplier, xp));
	}

	public static class TradeData
	{
		private final ItemStack buyItem;
		private final @Nullable ItemStack buyBItem;
		private final ItemStack sellItem;
		private final int uses;
		private final int maxUses;
		private final boolean rewardXp;
		private final int specialPrice;
		private final int demand;
		private final float multiplier;
		private final int xp;

		TradeData(ItemStack buyItem, @Nullable ItemStack buyBItem, ItemStack sellItem, int uses, int maxUses, boolean rewardXp)
		{
			this(buyItem, buyBItem, sellItem, uses, maxUses, rewardXp, 0, 0, 1.0F, 0);
		}

		// 1.14.x+
		TradeData(ItemStack buyItem, @Nullable ItemStack buyBItem, ItemStack sellItem,
		          int uses, int maxUses, boolean rewardXp,
		          int specialPrice, int demand, float multiplier, int xp)
		{
			this.buyItem = buyItem;
			this.buyBItem = buyBItem;
			this.sellItem = sellItem;
			this.uses = uses;
			this.maxUses = maxUses;
			this.rewardXp = rewardXp;
			this.specialPrice = specialPrice;
			this.demand = demand;
			this.multiplier = multiplier;
			this.xp = xp;
		}

		public ItemStack buyItem() { return this.buyItem; }

		// Using 'Optional/ItemStack.EMPTY' here can break certain future 1.21.X builds, but it was eventually fixed
		public @Nullable ItemStack buyBItem() { return this.buyBItem; }

		public ItemStack sellItem() { return this.sellItem; }

		public int uses() { return this.uses; }

		public int maxUses() { return this.maxUses; }

		public boolean rewardXp() { return this.rewardXp; }

		public int specialPrice() { return this.specialPrice; }

		public int demand() { return this.demand; }

		public float multiplier() { return this.multiplier; }

		public int xp() { return this.xp; }
	}

	/**
	 * Get the VillagerData from Data Tag.
	 * @implNote This gets upgraded to 'VillagerData' under 1.14.x
	 * @param data -
	 * @return -
	 */
	public static Optional<VillagerJobData> getVillagerJobData(@Nonnull CompoundData data)
	{
		int profession = -1;
		int career = -1;
		int level = -1;
		String newType = "";
		String newProf = "";

		if (data.contains(NbtKeys.PROFESSION, Constants.NBT.TAG_INT))
		{
			profession = data.getIntOrDefault(NbtKeys.PROFESSION, 0);
		}
		if (data.contains(NbtKeys.CAREER, Constants.NBT.TAG_INT))
		{
			career = data.getIntOrDefault(NbtKeys.CAREER, 0);
		}
		if (data.contains(NbtKeys.CAREER_LEVEL, Constants.NBT.TAG_INT))
		{
			level = data.getIntOrDefault(NbtKeys.CAREER_LEVEL, 0);
		}

		if (data.contains(NbtKeys.ORG_TYPE, Constants.NBT.TAG_STRING))
		{
			newType = data.getString(NbtKeys.ORG_TYPE);
		}

		if (data.contains(NbtKeys.ORG_VILLAGER_PROFESSION, Constants.NBT.TAG_STRING))
		{
			newProf = data.getString(NbtKeys.ORG_VILLAGER_PROFESSION);
		}

		if (profession < 0 && career < 0 && level < 0 && newType.isEmpty() && newProf.isEmpty()) { return Optional.empty(); }

		return Optional.of(new VillagerJobData(profession, career, level, newType, newProf));
	}

	public static class VillagerJobData
	{
		private final int profession;
		private final int career;
		private final int level;
		private final String newType;
		private final String newProfession;

		VillagerJobData(int profession, int career, int level, String newType, String newProfession)
		{
			this.profession = profession;
			this.career = career;
			this.level = level;
			this.newType = newType;
			this.newProfession = newProfession;
		}

		public int legacyProfession() { return this.profession; }

		public int legacyCareer() { return this.career; }

		public int level() { return this.level; }

		public String newType() { return this.newType; }

		public String newProfession() { return this.newProfession; }
	}

	/**
	 * Get the Zombie Villager cure timer.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Integer, UUID>> getZombieConversionTimer(@Nonnull CompoundData data)
	{
		int timer = -1;
		UUID player = null;

		if (data.contains(NbtKeys.ZOMBIE_CONVERSION, Constants.NBT.TAG_INT))
		{
			timer = data.getInt(NbtKeys.ZOMBIE_CONVERSION);
		}
		if (data.contains(NbtKeys.CONVERSION_PLAYER+"Most", Constants.NBT.TAG_ANY_NUMERIC))
		{
			player = DataTypeUtils.readUuidFromLongs(data, NbtKeys.CONVERSION_PLAYER+"Most", NbtKeys.CONVERSION_PLAYER+"Least");
		}
		else if (data.contains(NbtKeys.CONVERSION_PLAYER, Constants.NBT.TAG_INT_ARRAY))
		{
			player = DataTypeUtils.readUuidFromIntArray(data, NbtKeys.CONVERSION_PLAYER);
		}

		if (player == null && timer < 0) { return Optional.empty(); }

		return Optional.of(Pair.of(timer, player));
	}

	/**
	 * Get Drowned conversion timer from a Zombie being in Water
	 * @implNote This gets added in 1.13.x
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Integer, Integer>> getDrownedConversionTimer(@Nonnull CompoundData data)
	{
		int drowning = -1;
		int inWater = -1;

		if (data.contains(NbtKeys.ORG_DROWNED_CONVERSION, Constants.NBT.TAG_INT))
		{
			drowning = data.getInt(NbtKeys.ORG_DROWNED_CONVERSION);
		}
		if (data.contains(NbtKeys.ORG_IN_WATER, Constants.NBT.TAG_INT))
		{
			inWater = data.getInt(NbtKeys.ORG_IN_WATER);
		}

		if (drowning < 0 || inWater < 0) { return Optional.empty(); }

		return Optional.of(Pair.of(drowning, inWater));
	}

	/**
	 * Get Stray Conversion Timer from being in Powered Snow
	 * @implNote This gets added in 1.17.x
	 * @param data -
	 * @return -
	 */
	public static Optional<Integer> getStrayConversionTime(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.ORG_STRAY_CONVERSION, Constants.NBT.TAG_INT))
		{
			return Optional.of(data.getInt(NbtKeys.ORG_STRAY_CONVERSION));
		}

		return Optional.empty();
	}

	/**
	 * Get the LeashData from Data Tag.
	 * @implNote This gets changed to using a 'LeashData' object in later versions,
	 * and after that it changes more around 1.21.8+
	 * @param data -
	 * @return -
	 */
	public static Optional<LeashableData> getLeashableData(@Nonnull CompoundData data)
	{
		UUID uuid = null;
		BlockPos pos = null;

		if (data.contains(NbtKeys.NEW_LEASH, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData leash = data.getCompound(NbtKeys.NEW_LEASH);

			if (leash.contains(NbtKeys.UUID, Constants.NBT.TAG_INT_ARRAY))
			{
				uuid = DataTypeUtils.readUuidFromIntArray(leash, NbtKeys.UUID);

				if (uuid != null)
				{
					return Optional.of(new LeashableData(null, uuid, null));
				}
			}

			return Optional.empty();
		}
		else if (data.contains(NbtKeys.NEW_LEASH, Constants.NBT.TAG_INT_ARRAY))
		{
			pos = DataTypeUtils.readBlockPosFromArrayTag(data, NbtKeys.NEW_LEASH);
			return Optional.of(new LeashableData(null, null, pos));
		}

		if (data.contains(NbtKeys.LEASH, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData leash = data.getCompoundOrDefault(NbtKeys.LEASH, new CompoundData());

			if (leash.contains(NbtKeys.UUID_MOST, Constants.NBT.TAG_LONG))
			{
				uuid = DataTypeUtils.readUuidFromLongs(leash, NbtKeys.UUID_MOST, NbtKeys.UUID_LEAST);
			}
			else if (leash.contains(NbtKeys.SH_BUL_OWNER_POS_X, Constants.NBT.TAG_INT) &&
					 leash.contains(NbtKeys.SH_BUL_OWNER_POS_Y, Constants.NBT.TAG_INT) &&
					 leash.contains(NbtKeys.SH_BUL_OWNER_POS_Z, Constants.NBT.TAG_INT))
			{
				pos = new BlockPos(leash.getInt(NbtKeys.SH_BUL_OWNER_POS_X), leash.getInt(NbtKeys.SH_BUL_OWNER_POS_Y), leash.getInt(NbtKeys.SH_BUL_OWNER_POS_Z));
			}

			if (uuid == null && pos == null) { return Optional.empty(); }

			return Optional.of(new LeashableData(null, uuid, pos));
		}

		return Optional.empty();
	}

	public static class LeashableData
	{
		private final @Nullable Entity holder;
		private @Nullable UUID holderUuid;
		private @Nullable BlockPos pos;

		LeashableData(@Nullable Entity holder, @Nullable UUID holderUuid, @Nullable BlockPos pos)
		{
			this.holder = holder;
			this.holderUuid = holderUuid;
			this.pos = pos;
			this.resolveKnotOrEntity(holder);
		}

		public boolean resolveKnotOrEntity(@Nullable Entity holder)
		{
			if (holder == null && this.holder != null)
			{
				holder = this.holder;
			}

			if (holder != null)
			{
				if (this.holderUuid == null)
				{
					this.holderUuid = holder.getUniqueID();
				}

				if (holder instanceof EntityLeashKnot)
				{
					EntityLeashKnot knot = (EntityLeashKnot) holder;
					this.pos = BlockPos.of(knot.getPosition());
				}

				return true;
			}

			return false;
		}

		public Optional<Entity> holder() { return Optional.ofNullable(this.holder); }

		public Optional<UUID> holderUuid() { return Optional.ofNullable(this.holderUuid); }

		public Optional<BlockPos> pos() { return Optional.ofNullable(this.pos); }
	}

	/**
	 * Get the Full Block Attachment data for an item frame, including its Item from a Data Tag
	 * @param data -
	 * @return -
	 */
	public static Optional<BlockAttachedData> getItemFrameData(@Nonnull CompoundData data)
	{
		BlockPos pos = null;
		Direction facing = null;
		Direction itemRot = null;
		ItemStack item = null;

		if (data.contains(NbtKeys.TILE_X, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TILE_Y, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TILE_Z, Constants.NBT.TAG_INT))
		{
			pos = DataTypeUtils.readAttachedPosFromTag(data);
		}
		else if (data.contains(NbtKeys.NEW_ATTACHED_BLOCK_POS, Constants.NBT.TAG_INT_ARRAY))
		{
			pos = DataTypeUtils.readBlockPosFromArrayTag(data, NbtKeys.NEW_ATTACHED_BLOCK_POS);
		}
		if (data.contains(NbtKeys.FACING_2, Constants.NBT.TAG_INT))
		{
			facing = Direction.byHorizontalIndex(data.getInt(NbtKeys.FACING_2));
		}
		if (data.contains(NbtKeys.ITEM_ROTATION, Constants.NBT.TAG_BYTE))
		{
			itemRot = Direction.byIndex(data.getInt(NbtKeys.ITEM_ROTATION));
		}
		if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			item = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.ITEM, new CompoundData())).orElse(ItemStack.EMPTY);;
		}

		if (pos == null && facing == null && itemRot == null && item == null) { return Optional.empty(); }

		return Optional.of(new BlockAttachedData(pos, facing, itemRot, item));
	}

	/**
	 * Get a Painting's Direction and Variant from Data Tag.
	 * @implNote Future versions use the 'PaintingVariant' object instead of a String
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<BlockAttachedData, String>> getPaintingData(@Nonnull CompoundData data)
	{
		BlockPos pos = null;
		Direction facing = null;
		String variant = "";

		if (data.contains(NbtKeys.TILE_X, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TILE_Y, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.TILE_Z, Constants.NBT.TAG_INT))
		{
			pos = DataTypeUtils.readAttachedPosFromTag(data);
		}
		else if (data.contains(NbtKeys.NEW_ATTACHED_BLOCK_POS, Constants.NBT.TAG_INT_ARRAY))
		{
			pos = DataTypeUtils.readBlockPosFromArrayTag(data, NbtKeys.NEW_ATTACHED_BLOCK_POS);
		}
		if (data.contains(NbtKeys.FACING_2, Constants.NBT.TAG_INT))
		{
			facing = Direction.byHorizontalIndex(data.getInt(NbtKeys.FACING_2));
		}
		else if (data.contains(NbtKeys.NEW_FACING, Constants.NBT.TAG_BYTE))
		{
			facing = Direction.byHorizontalIndex(data.getByte(NbtKeys.NEW_FACING));
		}
		if (data.contains(NbtKeys.PAINTING_TYPE, Constants.NBT.TAG_STRING))
		{
			variant = data.getString(NbtKeys.PAINTING_TYPE);
		}

		if (variant.isEmpty()) { return Optional.empty(); }

		return Optional.of(Pair.of(new BlockAttachedData(pos, facing), variant));
	}

	public static class BlockAttachedData
	{
		private final BlockPos pos;
		private final Direction facing;
		private final @Nullable Direction itemRot;
		private final @Nullable ItemStack item;

		// Basic / Painting
		BlockAttachedData(BlockPos pos, Direction facing)
		{
			this(pos, facing, null, null);
		}

		// Item Frame
		BlockAttachedData(BlockPos pos, Direction facing, @Nullable Direction itemRot, @Nullable ItemStack item)
		{
			this.pos = pos;
			this.facing = facing;
			this.itemRot = itemRot;
			this.item = item;
		}

		public BlockPos pos() { return this.pos; }

		public Direction facing() { return this.facing; }

		public Optional<Direction> itemRot() { return Optional.ofNullable(this.itemRot); }

		public Optional<ItemStack> item() { return Optional.ofNullable(this.item); }
	}

	/**
	 * Get the Horse Variant information via {@link HorseVariantData} with a hacky workaround.
	 * @implNote In future versions, the 'HorseColor' and 'Markings' objects are added
	 * @param data -
	 * @return -
	 */
	public static Optional<HorseVariantData> getHorseVariantData(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_INT))
		{
			return Optional.of(new HorseVariantData(data.getInt(NbtKeys.VARIANT)));
		}

		return Optional.empty();
	}

	/**
	 * @implNote The color (Variant) / Markings turns into Enums under 1.16.x+.
	 */
	public static class HorseVariantData
	{
		public static final String[] HORSE_COLORS = new String[]{"white", "creamy", "chestnut", "brown", "black", "gray", "darkbrown"};
		public static final String[] HORSE_MARKINGS = new String[]{"", "white", "whitefield", "whitedots", "blackdots"};
		private int variant;
		private int colorId = -1;
		private int markingId = -1;
		private String color = "";
		private String marking = "";

		public HorseVariantData(int variant)
		{
			this.variant = variant;
			this.calculateVariant(variant);
		}

		public void calculateVariant(int variant)
		{
			if (variant > 0 && this.variant != variant)
			{
				this.variant = variant;
			}

			this.colorId = (this.variant & 0xFF) % 7;
			this.markingId = ((this.variant & 0xFF00) >> 8) % 5;
			this.color = HORSE_COLORS[this.colorId];
			this.marking = HORSE_MARKINGS[this.markingId];
		}

		public int variant() { return this.variant; }

		public int colorId() { return this.colorId; }

		public int markingId() { return this.markingId; }

		public String color() { return this.color; }

		public String marking() { return this.marking; }
	}

	/**
	 * Get a Llama's Variant type from Data Tag.
	 * @implNote In future versions, the Llama has more Variants; and utilizes the 'Llama.Variant' Object
	 * @param data -
	 * @return -
	 */
	public static Optional<LlamaVariantData> getLlamaTypeData(@Nonnull CompoundData data)
	{
		int variant = -1;
		int strength = -1;
		ItemStack stack = ItemStack.EMPTY;

		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_INT))
		{
			variant = data.getInt(NbtKeys.VARIANT);
		}

		if (data.contains(NbtKeys.STRENGTH, Constants.NBT.TAG_INT))
		{
			strength = data.getInt(NbtKeys.STRENGTH);
		}

		if (data.contains(NbtKeys.LLAMA_DECOR, Constants.NBT.TAG_COMPOUND))
		{
			stack = DataTypeUtils.toItemStack(data.getCompoundOrDefault(NbtKeys.LLAMA_DECOR, new CompoundData())).orElse(ItemStack.EMPTY);;
		}

		if (stack.isEmpty() && variant == -1 && strength == -1) { return Optional.empty(); }

		return Optional.of(new LlamaVariantData(variant, strength, stack));
	}

	public static class LlamaVariantData
	{
		private final int variant;
		private final int strength;
		private final @Nullable ItemStack carpet;

		public LlamaVariantData(int variant, int strength, @Nullable ItemStack carpet)
		{
			this.variant = variant;
			this.strength = strength;
			this.carpet = carpet;
		}

		public int variant() { return this.variant; }

		public int strength() { return this.strength; }

		public Optional<ItemStack> carpet() { return Optional.ofNullable(this.carpet); }
	}

	/**
	 * Get a Sheep's Color from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<DyeColorCode> getSheepColor(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_BYTE))
		{
			return Optional.ofNullable(DyeColorCode.getByMeta(data.getByte(NbtKeys.COLOR)));
		}
		else if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_INT))
		{
			return Optional.ofNullable(DyeColorCode.getByMeta(data.getInt(NbtKeys.COLOR)));
		}

		return Optional.empty();
	}

	/**
	 * Get a Rabbit's Variant type from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<EntityRabbit.RabbitTypeData> getRabbitType(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.RABBIT_TYPE, Constants.NBT.TAG_INT))
		{
			return Optional.of(new EntityRabbit.RabbitTypeData(data.getInt(NbtKeys.RABBIT_TYPE)));
		}

		return Optional.empty();
	}

	/**
	 * Get a Parrot's Variant from Data Tag.
	 * @implNote In future versions, the Parrot uses the 'Parrot.Variant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<Integer> getParrotVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_INT))
		{
			return Optional.of(data.getInt(NbtKeys.VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Cat's Variant, and Collar Color from Data Tag.
	 * @implNote In later versions, Cats are added alone (Not Ocelots) under 1.14.x;
	 * and then use the 'CatVariant' Object is used under 1.21.4+
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<String, DyeColorCode>> getCatVariant(@Nonnull CompoundData data)
	{
		String variant = null;
		DyeColorCode collar = null;

		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			variant = data.getString(NbtKeys.NEW_VARIANT);
		}
		if (data.contains(NbtKeys.COLLAR_COLOR, Constants.NBT.TAG_ANY_NUMERIC))
		{
			collar = DyeColorCode.getByMeta(data.getInt(NbtKeys.COLLAR_COLOR));
		}

		if (variant == null && collar == null) { return Optional.empty(); }

		return Optional.of(Pair.of(variant, collar));
	}

	/**
	 * Get a Tropical Fish Variant from Data Tag.
	 * @implNote In future versions, the Tropical Fish are added to the game,
	 * and then uses the 'TropicalFish.Variant' and 'TropicalFish.Pattern' objects
	 * @param data -
	 * @return -
	 */
	public static Optional<Integer> getTropicalFishVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_INT))
		{
			return Optional.of(data.getInt(NbtKeys.VARIANT) & '\uffff');
		}
		else if (data.contains(NbtKeys.ORG_BUCKET_VARIANT, Constants.NBT.TAG_INT))
		{
			return Optional.of(data.getInt(NbtKeys.ORG_BUCKET_VARIANT) & '\uffff');
		}

		return Optional.empty();
	}

	/**
	 * Get a Dolphin's TreasurePos and other data from Data Tag.
	 * @implNote In the future for 1.21.8+; they removed the "Treasure" functionality.
	 * @param data -
	 * @return -
	 */
	public static Optional<DolphinData> getDolphinData(@Nonnull CompoundData data)
	{
		boolean hasFish = false;
		boolean canFind = false;
		int moist = -1;
		BlockPos treasure = null;

		if (data.contains(NbtKeys.ORG_MOISTNESS, Constants.NBT.TAG_INT))
		{
			moist = data.getInt(NbtKeys.ORG_MOISTNESS);
		}

		if (data.contains(NbtKeys.ORG_GOT_FISH, Constants.NBT.TAG_BYTE))
		{
			hasFish = data.getBoolean(NbtKeys.ORG_GOT_FISH);
		}

		if (data.contains(NbtKeys.ORG_CAN_FIND_TREASURE, Constants.NBT.TAG_BYTE))
		{
			canFind = data.getBoolean(NbtKeys.ORG_CAN_FIND_TREASURE);
		}

		if (data.contains(NbtKeys.ORG_TREASURE_X, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.ORG_TREASURE_Y, Constants.NBT.TAG_INT) &&
			data.contains(NbtKeys.ORG_TREASURE_Z, Constants.NBT.TAG_INT))
		{
			treasure = new BlockPos(data.getInt(NbtKeys.ORG_TREASURE_X), data.getInt(NbtKeys.ORG_TREASURE_Y), data.getInt(NbtKeys.ORG_TREASURE_Z));
		}

		if (!hasFish && !canFind && moist == -1 && treasure == null) { return Optional.empty(); }

		return Optional.of(new DolphinData(moist, hasFish, canFind, treasure));
	}

	/**
	 * @implNote In the future for 1.21.8+; they removed the "Treasure" functionality.
	 */
	public static class DolphinData
	{
		private final int moistness;
		private final boolean hasFish;
		private final boolean canFindTreasure;
		private final @Nullable BlockPos treasure;

		DolphinData(int moistness, boolean hasFish)
		{
			this(moistness, hasFish, false, null);
		}

		DolphinData(int moistness, boolean hasFish, boolean canFindTreasure, @Nullable BlockPos treasure)
		{
			this.moistness = moistness;
			this.hasFish = hasFish;
			this.canFindTreasure = canFindTreasure;
			this.treasure = treasure;
		}

		public int moistness() { return this.moistness; }

		public boolean hasFish() { return this.hasFish; }

		public boolean canFindTreasure() { return this.canFindTreasure; }

		public Optional<BlockPos> treasure() { return Optional.ofNullable(this.treasure); }
	}

	/**
	 * Get a Fox Variant from Data Tag.
	 * @implNote Future Versions after the Fox is added under 1.14.x; uses the 'Fox.Type' object, and then later on, using the 'Fox.Variant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getFoxVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.TYPE, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.TYPE));
		}

		return Optional.empty();
	}

	/**
	 * Get a Mooshroom's Variant from Data Tag.
	 * @implNote This was added around 1.14.x using the 'MushroomCow.MushroomType' object, and then later on, using the 'MushroomCow.Variant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getMooshroomVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.TYPE, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.TYPE));
		}

		return Optional.empty();
	}

	/**
	 * Get the Panda Gene's from Data Tag
	 * @implNote Future Versions after the Panda is added under 1.14.x; uses the 'Panda.Gene' object
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<String, String>> getPandaGenes(@Nonnull CompoundData data)
	{
		String mainGene = "";
		String hiddenGene = "";

		if (data.contains(NbtKeys.ORG_MAIN_GENE, Constants.NBT.TAG_STRING))
		{
			mainGene = data.getString(NbtKeys.ORG_MAIN_GENE);
		}
		if (data.contains(NbtKeys.ORG_HIDDEN_GENE, Constants.NBT.TAG_STRING))
		{
			hiddenGene = data.getString(NbtKeys.ORG_HIDDEN_GENE);
		}

		if (mainGene.isEmpty() && hiddenGene.isEmpty()) { return Optional.empty(); }

		return Optional.of(Pair.of(mainGene, hiddenGene));
	}

	/**
	 * Get an Axolotl's Variant from Data Tag.
	 * @implNote When the Axolotl gets added to the game under 1.17.x, it uses the 'Axolotl.Variant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<Integer> getAxolotlVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.VARIANT, Constants.NBT.TAG_INT))
		{
			return Optional.of(data.getIntOrDefault(NbtKeys.VARIANT, -1));
		}

		return Optional.empty();
	}

	/**
	 * Get a Frog's Variant from Data Tag.
	 * @implNote When Frogs are added under 1.19.x, they utilize the 'FrogVariant' object.
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getFrogVariant(@Nonnull CompoundData data)
	{
		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Wolves' Variant and Collar Color from Data Tag.
	 * @implNote This information isn't added until 1.20.6, which then uses the 'WolfVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<String, DyeColorCode>> getWolfVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		DyeColorCode collar = null;
		String variant = "";

		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			variant = data.getString(NbtKeys.NEW_VARIANT);
		}

		if (data.contains(NbtKeys.COLLAR_COLOR, Constants.NBT.TAG_STRING))
		{
			collar = DyeColorCode.valueOf(data.getString(NbtKeys.COLLAR_COLOR));
		}
		else if (data.contains(NbtKeys.COLLAR_COLOR, Constants.NBT.TAG_INT))
		{
			collar = DyeColorCode.getByMeta(data.getInt(NbtKeys.COLLAR_COLOR));
		}

		if (variant.isEmpty() && collar == null) { return Optional.empty(); }

		return Optional.of(Pair.of(variant, collar));
	}

	/**
	 * Get a Salmon Variant from Data Tag.
	 * @implNote This was added around 1.21.2+ using the 'Salmon.Variant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getSalmonVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.ORG_TYPE, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.ORG_TYPE));
		}

		return Optional.empty();
	}

	/**
	 * Get a Wolves' Sound Type Variant from Data Tag.
	 * @implNote This was added around 1.21.5+ using the 'WolfSoundVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getWolfSoundType(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_SOUND_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_SOUND_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Chicken's Variant from Data Tag.
	 * @implNote This was added around 1.21.5+ using the 'ChickenVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getChickenVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Pig's Variant from Data Tag.
	 * @implNote This was added around 1.21.5+ using the 'PigVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getPigVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Cow's Variant from Data Tag.
	 * @implNote This was added around 1.21.5+ using the 'CowVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getCowVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a Mob's Home Pos and Radius from Data Tag
	 * @implNote This first appears under 1.21.8+, and it is utilized by a number of various entities; including Leash Knots.
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<BlockPos, Integer>> getHomePos(@Nonnull CompoundData data)
	{
		BlockPos pos = null;
		int radius = -1;

		if (data.contains(NbtKeys.NEW_HOME_POS, Constants.NBT.TAG_INT_ARRAY))
		{
			pos = DataTypeUtils.readBlockPosFromArrayTag(data, NbtKeys.NEW_HOME_POS);
		}

		if (data.contains(NbtKeys.NEW_HOME_RADIUS, Constants.NBT.TAG_INT))
		{
			radius = data.getInt(NbtKeys.NEW_HOME_RADIUS);
		}

		if (pos == null && radius < 0) { return Optional.empty(); }

		return Optional.of(Pair.of(pos, radius));
	}

	/**
	 * Get a Copper Golem's Weathering Data from Data Tag
	 * @implNote The Copper Golem first appears under 1.21.10, and utilizes the 'WeatheringCopper.WeatherState' object
	 * @param data -
	 * @return -
	 */
	public static Optional<OxidizationState> getWeatheringState(@Nonnull CompoundData data)
	{
		String state = "";
		long age = -1L;

		if (data.contains(NbtKeys.NEW_WEATHER_STATE, Constants.NBT.TAG_STRING))
		{
			state = data.getString(NbtKeys.NEW_WEATHER_STATE);
		}

		if (data.contains(NbtKeys.NEW_NEXT_WEATHER_AGE, Constants.NBT.TAG_LONG))
		{
			age = data.getLong(NbtKeys.NEW_NEXT_WEATHER_AGE);
		}

		if (state.isEmpty() && age < 0) { return Optional.empty(); }

		return Optional.of(new OxidizationState(state, age));
	}

	/**
	 * This Object describes the "Weathering" status of Copper
	 */
	public static class OxidizationState
	{
		private final String state;
		private final long age;

		public OxidizationState(String state, long age)
		{
			this.state = state;
			this.age = age;
		}

		public String state() { return this.state; }

		public long age() { return this.age; }
	}

	/**
	 * Get a Zombie Nautilus's Variant from Data Tag.
	 * @implNote This was added under 1.21.11 using the 'ZombieNautilusVariant' object
	 * @param data -
	 * @return -
	 */
	public static Optional<String> getZombieNautilusVariant(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_VARIANT, Constants.NBT.TAG_STRING))
		{
			return Optional.ofNullable(data.getString(NbtKeys.NEW_VARIANT));
		}

		return Optional.empty();
	}

	/**
	 * Get a player's Experience values from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<PlayerExpData> getPlayerExp(@Nonnull CompoundData data)
	{
		int level = -1;
		int total = -1;
		float progress = 0.0F;

		if (data.contains(NbtKeys.EXP_LEVEL, Constants.NBT.TAG_INT))
		{
			level = data.getInt(NbtKeys.EXP_LEVEL);
		}
		if (data.contains(NbtKeys.EXP_TOTAL, Constants.NBT.TAG_INT))
		{
			total = data.getInt(NbtKeys.EXP_TOTAL);
		}
		if (data.contains(NbtKeys.EXP_PROGRESS, Constants.NBT.TAG_FLOAT))
		{
			progress = data.getFloat(NbtKeys.EXP_PROGRESS);
		}

		if (level < 0 && total < 0 && progress == 0.0F) { return Optional.empty(); }

		return Optional.of(new PlayerExpData(level, total, progress));
	}

	public static class PlayerExpData
	{
		private final int level;
		private final int total;
		private final float progress;

		PlayerExpData(int level, int total, float progress)
		{
			this.level = level;
			this.total = total;
			this.progress = progress;
		}

		public int level() { return this.level; }

		public int total() { return this.total; }

		public float progress() { return this.progress; }
	}

	/**
	 * Get a Player's Hunger Manager from Data Tag.
	 *
	 * @param data -
	 * @return -
	 */
	public static Optional<FoodStats> getPlayerHunger(@Nonnull CompoundData data)
	{
		FoodStats hunger = null;

		if (data.contains(NbtKeys.FOOD_LEVEL, Constants.NBT.TAG_ANY_NUMERIC))
		{
			hunger = new FoodStats();
			hunger.readNBT(DataConverterNbt.toVanillaCompound(data));
		}

		return Optional.ofNullable(hunger);
	}
}

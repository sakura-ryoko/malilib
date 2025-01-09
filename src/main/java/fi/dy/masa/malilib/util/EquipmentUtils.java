package fi.dy.masa.malilib.util;

import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import fi.dy.masa.malilib.mixin.IMixinAnimalArmorItem;

public class EquipmentUtils
{
	public static boolean isAnyWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		return isMeleeWeapon(stack) || isRangedWeapon(stack);
	}

	public static boolean isMeleeWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof MaceItem || item instanceof AxeItem)
		{
			return true;
		}
		else if (item instanceof RangedWeaponItem)
		{
			return false;
		}

		return stack.contains(DataComponentTypes.WEAPON);
	}

	public static boolean isRangedWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof MaceItem || item instanceof AxeItem)
		{
			return false;
		}
		else return item instanceof RangedWeaponItem;
	}

	public static boolean isAnyTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		return isRegularTool(stack) || isMiscTool(stack);
	}

	public static boolean isRegularTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		return stack.contains(DataComponentTypes.TOOL);
	}

	public static boolean isMiscTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		return  item instanceof ShearsItem ||
				item instanceof FlintAndSteelItem ||
				item instanceof BrushItem ||
				item instanceof FishingRodItem;
	}

	public static Pair<Integer, Boolean> getWeaponData(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return Pair.of(-1, false);
		}

		if (stack.contains(DataComponentTypes.WEAPON))
		{
			WeaponComponent weaponComponent = stack.get(DataComponentTypes.WEAPON);

			if (weaponComponent != null)
			{
				return Pair.of(weaponComponent.damagePerAttack(), weaponComponent.canDisableBlocking());
			}
		}

		return Pair.of(-1, false);
	}

	public Pair<Double, Double> getDamageAndSpeedAttributes(ItemStack stack)
	{
		double speed = -1;
		double damage = -1;

		if (stack == null || stack.isEmpty())
		{
			return Pair.of(damage, speed);
		}

		if (stack.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS))
		{
			AttributeModifiersComponent attrib = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (AttributeModifiersComponent.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(EntityAttributes.ATTACK_DAMAGE))
					{
						damage = entry.modifier().value();
					}
					else if (entry.attribute().equals(EntityAttributes.ATTACK_SPEED))
					{
						speed = entry.modifier().value();
					}
				}
			}
		}

		return Pair.of(damage, speed);
	}

	public static boolean isCorrectTool(ItemStack stack, BlockState state)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		if (stack.contains(DataComponentTypes.TOOL))
		{
			ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);

			return (toolComponent != null && toolComponent.isCorrectForDrops(state));
		}

		return false;
	}

	public static float getMiningSpeed(ItemStack stack, @Nullable BlockState state)
	{
		if (stack == null || stack.isEmpty())
		{
			return -1;
		}

		if (stack.contains(DataComponentTypes.TOOL))
		{
			ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);

			if (toolComponent != null)
			{
				if (state != null)
				{
					return toolComponent.getSpeed(state);
				}

				return toolComponent.defaultMiningSpeed();
			}
		}

		return -1;
	}

	public static boolean isAnyArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		return isHumanoidArmor(stack) || isAnyAnimalArmor(stack);
	}

	public static boolean isHumanoidArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		if (stack.contains(DataComponentTypes.EQUIPPABLE) &&
			stack.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS))
		{
			AttributeModifiersComponent attrib = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (AttributeModifiersComponent.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(EntityAttributes.ARMOR) &&
						(entry.slot() != AttributeModifierSlot.MAINHAND &&
						 entry.slot() != AttributeModifierSlot.OFFHAND))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isAnyAnimalArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		return stack.getItem() instanceof AnimalArmorItem;
	}

	public static boolean isHorseArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		if (stack.getItem() instanceof AnimalArmorItem armor)
		{
			return (((IMixinAnimalArmorItem) armor).malilib_getAnimalArmorType() == AnimalArmorItem.Type.EQUESTRIAN);
		}

		return false;
	}

	public static boolean isWolfArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return false;
		}

		if (stack.getItem() instanceof AnimalArmorItem armor)
		{
			return (((IMixinAnimalArmorItem) armor).malilib_getAnimalArmorType() == AnimalArmorItem.Type.CANINE);
		}

		return false;
	}

	public static @Nullable AttributeModifierSlot getEquipmentSlot(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return null;
		}

		if (stack.contains(DataComponentTypes.EQUIPPABLE) &&
			stack.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS))
		{
			AttributeModifiersComponent attrib = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (AttributeModifiersComponent.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(EntityAttributes.ARMOR))
					{
						return entry.slot();
					}
				}
			}
		}

		return null;
	}

	public static int getEnchantmentLevel(ItemStack stack, @Nonnull RegistryKey<Enchantment> enchantment)
	{
		ItemEnchantmentsComponent enchants = stack.getEnchantments();

		if (!enchants.equals(ItemEnchantmentsComponent.DEFAULT))
		{
			Set<RegistryEntry<Enchantment>> enchantList = enchants.getEnchantments();

			for (RegistryEntry<Enchantment> entry : enchantList)
			{
				if (entry.matchesKey(enchantment))
				{
					return enchants.getLevel(entry);
				}
			}
		}

		return -1;
	}
}

package fi.dy.masa.malilib.util;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;

public class EquipmentUtils
{
	public static boolean isAnyWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return  isMeleeWeapon(stack) ||
				isRangedWeapon(stack) ||
				isTrident(stack) ||
				isMace(stack);
	}

	public static boolean isMeleeWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		Item item = stack.getItem();

		if (item instanceof MaceItem || item instanceof AxeItem)
		{
			return true;
		}
		else if (item instanceof ProjectileWeaponItem || item instanceof TridentItem)
		{
			return false;
		}

		return (stack.is(ItemTags.WEAPON_ENCHANTABLE) ||
			    isSword(stack) ||
				isAxe(stack) ||
				isMace(stack) ||
				isSpear(stack)) &&
			    stack.has(DataComponents.WEAPON);
	}

	public static boolean isSword(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_sword");
    }

	public static boolean isAxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_axe");
	}

	public static boolean isSpear(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_spear");
	}

	public static boolean isMace(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof MaceItem;
	}

	public static boolean isRangedWeapon(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return  stack.getItem() instanceof ProjectileWeaponItem ||
				isTrident(stack);
	}

	public static boolean isTrident(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof TridentItem;
	}

	public static boolean isAnyTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return  isRegularTool(stack) ||
				isMiscTool(stack);
	}

	public static boolean isRegularTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return (stack.is(ItemTags.MINING_ENCHANTABLE) ||
			    isPickAxe(stack) ||
				isAxe(stack) ||
			    isHoe(stack) ||
				isShovel(stack)) &&
			    stack.has(DataComponents.TOOL);
	}

	public static boolean isPickAxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_pickaxe");
	}

	public static boolean isShovel(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_shovel");
	}

	public static boolean isHoe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("_hoe");
	}

	public static boolean isMiscTool(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return  isShears(stack) ||
				isFlintAndSteel(stack) ||
				isFishingRod(stack) ||
				isBrush(stack);
	}

	public static boolean isShears(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof ShearsItem;
	}

	public static boolean isFlintAndSteel(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof FlintAndSteelItem;
	}

	public static boolean isFishingRod(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof FishingRodItem;
	}

	public static boolean isBrush(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof BrushItem;
	}

	public static Pair<Integer, Float> getWeaponData(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
		{
			return Pair.of(-1, 0.0f);
		}

		if (stack.has(DataComponents.WEAPON))
		{
			Weapon weaponComponent = stack.get(DataComponents.WEAPON);

			if (weaponComponent != null)
			{
				return Pair.of(weaponComponent.itemDamagePerAttack(), weaponComponent.disableBlockingForSeconds());
			}
		}

		return Pair.of(-1, 0.0f);
	}

	public static Pair<Double, Double> getDamageAndSpeedAttributes(ItemStack stack)
	{
		double speed = -1;
		double damage = -1;

		if (stack == null || stack.isEmpty())
		{
			return Pair.of(damage, speed);
		}

		if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ATTACK_DAMAGE))
					{
						damage = entry.modifier().amount();
					}
					else if (entry.attribute().equals(Attributes.ATTACK_SPEED))
					{
						speed = entry.modifier().amount();
					}
				}
			}
		}

		return Pair.of(damage, speed);
	}

	public static boolean isCorrectTool(ItemStack stack, @Nonnull BlockState state)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.TOOL))
		{
			Tool toolComponent = stack.get(DataComponents.TOOL);

			return (toolComponent != null && toolComponent.isCorrectForDrops(state));
		}

		return false;
	}

	public static float getMiningSpeed(ItemStack stack, @Nullable BlockState state)
	{
		if (stack == null || stack.isEmpty()) return -1;

		if (stack.has(DataComponents.TOOL))
		{
			Tool toolComponent = stack.get(DataComponents.TOOL);

			if (toolComponent != null)
			{
				if (state != null)
				{
					return toolComponent.getMiningSpeed(state);
				}

				return toolComponent.defaultMiningSpeed();
			}
		}

		return -1;
	}

	public static boolean isAnyArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		return  isHumanoidArmor(stack) ||
				isShield(stack) ||
				isAnyAnimalArmor(stack);
	}

	public static boolean isShield(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return stack.getItem() instanceof ShieldItem;
	}

	public static boolean isHumanoidArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ARMOR) &&
						(entry.slot() != EquipmentSlotGroup.MAINHAND &&
						 entry.slot() != EquipmentSlotGroup.OFFHAND))
					{
						return true;
					}
				}
			}
		}

		return stack.is(ItemTags.EQUIPPABLE_ENCHANTABLE);
	}

	public static boolean matchArmorSlot(ItemStack stack, @Nonnull EquipmentSlot slot)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
			EquipmentSlotGroup attributeSlot = EquipmentSlotGroup.bySlot(slot);

			if (attrib != null)
			{
				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ARMOR) &&
						entry.slot() == attributeSlot)
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
		if (stack == null || stack.isEmpty()) return false;
		return Objects.equals(getEquipmentSlot(stack), EquipmentSlotGroup.BODY);
	}

	public static boolean isHorseArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
			Equippable equip = stack.get(DataComponents.EQUIPPABLE);

			if (attrib != null && equip != null)
			{
				boolean bodySlot = false;

				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ARMOR) && entry.slot().equals(EquipmentSlotGroup.BODY))
					{
						bodySlot = true;
						break;
					}
				}

				return bodySlot && (equip.canBeEquippedBy(EntityType.HORSE) || equip.canBeEquippedBy(EntityType.ZOMBIE_HORSE));
			}
		}

		return false;
	}

	public static boolean isNautilusArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
			Equippable equip = stack.get(DataComponents.EQUIPPABLE);

			if (attrib != null && equip != null)
			{
				boolean bodySlot = false;

				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ARMOR) && entry.slot().equals(EquipmentSlotGroup.BODY))
					{
						bodySlot = true;
						break;
					}
				}

				return bodySlot && (equip.canBeEquippedBy(EntityType.NAUTILUS) || equip.canBeEquippedBy(EntityType.ZOMBIE_NAUTILUS));
			}
		}

		return false;
	}

	public static boolean isWolfArmor(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
			Equippable equip = stack.get(DataComponents.EQUIPPABLE);

			if (attrib != null && equip != null)
			{
				boolean bodySlot = false;

				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
                    if (entry.attribute().equals(Attributes.ARMOR) && entry.slot().equals(EquipmentSlotGroup.BODY))
                    {
                        bodySlot = true;
                        break;
                    }
				}

				return bodySlot && equip.canBeEquippedBy(EntityType.WOLF);
			}
		}

		return false;
	}

	public static @Nullable EquipmentSlotGroup getEquipmentSlot(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return null;

		if (stack.has(DataComponents.EQUIPPABLE) &&
			stack.has(DataComponents.ATTRIBUTE_MODIFIERS))
		{
			ItemAttributeModifiers attrib = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

			if (attrib != null)
			{
				for (ItemAttributeModifiers.Entry entry : attrib.modifiers())
				{
					if (entry.attribute().equals(Attributes.ARMOR))
					{
						return entry.slot();
					}
				}
			}
		}

		return null;
	}

	public static int getEnchantmentLevel(ItemStack stack, @Nonnull ResourceKey<@NotNull Enchantment> enchantment)
	{
		if (stack == null || stack.isEmpty()) return -1;

		ItemEnchantments enchants = stack.getEnchantments();

		if (!enchants.equals(ItemEnchantments.EMPTY))
		{
			Set<Holder<@NotNull Enchantment>> enchantList = enchants.keySet();

			for (Holder<@NotNull Enchantment> entry : enchantList)
			{
				if (entry.is(enchantment))
				{
					return enchants.getLevel(entry);
				}
			}
		}

		return -1;
	}

	public static int hasSameOrBetterEnchantment(ItemStack testedStack, ItemStack previous, ResourceKey<@NotNull Enchantment> enchantment)
	{
		return getEnchantmentLevel(testedStack, enchantment) - getEnchantmentLevel(previous, enchantment);
	}

	public static boolean hasSilkTouch(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.SILK_TOUCH) > 0;
	}

	public static boolean hasFortune(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.FORTUNE) > 0;
	}

	public static boolean hasMending(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.MENDING) > 0;
	}

	public static boolean hasUnbreaking(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.UNBREAKING) > 0;
	}

	public static boolean hasLooting(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.LOOTING) > 0;
	}

	public static boolean hasSmite(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.SMITE) > 0;
	}

	public static boolean hasSharpness(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.SHARPNESS) > 0;
	}

	public static boolean hasBane(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.BANE_OF_ARTHROPODS) > 0;
	}

	public static boolean hasImpaling(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.IMPALING) > 0;
	}

	public static boolean hasDensity(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.DENSITY) > 0;
	}

	public static boolean hasLunge(ItemStack stack)
	{
		return getEnchantmentLevel(stack, Enchantments.LUNGE) > 0;
	}

	public static boolean isSilkAxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isAxe(stack) && hasSilkTouch(stack);
	}

	public static boolean isSilkHoe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isHoe(stack) && hasSilkTouch(stack);
	}

	public static boolean isSilkPickaxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isPickAxe(stack) && hasSilkTouch(stack);
	}

	public static boolean isFortuneAxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isAxe(stack) && hasFortune(stack);
	}

	public static boolean isFortuneHoe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isHoe(stack) && hasFortune(stack);
	}

	public static boolean isFortunePickaxe(ItemStack stack)
	{
		if (stack == null || stack.isEmpty()) return false;
		return isPickAxe(stack) && hasFortune(stack);
	}
}

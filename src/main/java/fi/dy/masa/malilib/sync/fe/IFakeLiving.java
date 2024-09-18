package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

public interface IFakeLiving
{
    Iterable<ItemStack> getArmorItems();

    ItemStack getEquippedStack(EquipmentSlot slot);

    void equipStack(EquipmentSlot slot, ItemStack stack);

    Arm getMainArm();
}

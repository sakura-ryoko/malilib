package fi.dy.masa.malilib.sync.fbe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface IFakeContainer
{
    DefaultedList<ItemStack> getHeldStacks();

    void setHeldStacks(DefaultedList<ItemStack> inventory);
}

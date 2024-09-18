package fi.dy.masa.malilib.sync.fe;

import net.minecraft.nbt.NbtCompound;

public interface IFakeEntityHolder
{
    void readCustomDataFromNbt(NbtCompound nbt);

    void writeCustomDataToNbt(NbtCompound nbt);
}

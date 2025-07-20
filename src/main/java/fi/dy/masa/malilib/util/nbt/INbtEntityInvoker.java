package fi.dy.masa.malilib.util.nbt;

import java.util.Optional;

import net.minecraft.nbt.NbtCompound;

public interface INbtEntityInvoker
{
    Optional<NbtCompound> malilib$getNbtDataWithId(final int expectedId);
}

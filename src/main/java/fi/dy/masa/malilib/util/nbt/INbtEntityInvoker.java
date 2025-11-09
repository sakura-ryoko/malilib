package fi.dy.masa.malilib.util.nbt;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import fi.dy.masa.malilib.util.data.tag.CompoundData;

public interface INbtEntityInvoker
{
    Optional<CompoundTag> malilib$getNbtDataWithId(final int expectedId);
	Optional<CompoundData> malilib$getDataTagWithId(final int expectedId);
}

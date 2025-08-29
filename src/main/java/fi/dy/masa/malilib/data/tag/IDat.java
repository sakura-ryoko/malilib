package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.nbt.NbtElement;

@ApiStatus.Experimental
public interface IDat<T>
{
    T getValue();
    void setValue(T newValue);
    DatType getType();
	NbtElement toVanilla();
}

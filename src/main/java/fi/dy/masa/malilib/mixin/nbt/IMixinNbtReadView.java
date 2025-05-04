package fi.dy.masa.malilib.mixin.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtReadView.class)
public interface IMixinNbtReadView
{
    @Accessor("context")
    ReadContext malilib_getContext();

    @Accessor("nbt")
    NbtCompound malilib_getNbt();
}

package fi.dy.masa.malilib.mixin.nbt;

import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.NbtWriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtWriteView.class)
public interface IMixinNbtWriteView
{
    @Accessor("ops")
    DynamicOps<?> malilib_getOps();

    @Accessor("nbt")
    NbtCompound malilib_getNbt();
}

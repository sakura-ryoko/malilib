package fi.dy.masa.malilib.mixin.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInputContextHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagValueInput.class)
public interface IMixinNbtReadView
{
    @Accessor("context")
    ValueInputContextHelper malilib_getContext();

    @Accessor("input")
    CompoundTag malilib_getNbt();
}

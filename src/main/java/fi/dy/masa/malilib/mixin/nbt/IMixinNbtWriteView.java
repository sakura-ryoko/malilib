package fi.dy.masa.malilib.mixin.nbt;

import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagValueOutput.class)
public interface IMixinNbtWriteView
{
    @Accessor("ops")
    DynamicOps<?> malilib_getOps();

    @Accessor("output")
    CompoundTag malilib_getNbt();
}

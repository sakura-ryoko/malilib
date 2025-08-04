package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.datafix.DataFixer;

@Mixin(DataFixer.class)
public interface DataFixerMixin
{
    @Accessor("version")
    int malilib$getVersion();
}

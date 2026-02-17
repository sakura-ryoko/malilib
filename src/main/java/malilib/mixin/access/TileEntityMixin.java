package malilib.mixin.access;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntity.class)
public interface TileEntityMixin
{
	@Accessor("REGISTRY")
	RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> malilib_getTileEntityRegistry();
}

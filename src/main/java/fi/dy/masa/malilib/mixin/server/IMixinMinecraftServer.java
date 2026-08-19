package fi.dy.masa.malilib.mixin.server;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface IMixinMinecraftServer
{
	@Accessor("registries")
	LayeredRegistryAccess<RegistryLayer> malilib_registries();
}

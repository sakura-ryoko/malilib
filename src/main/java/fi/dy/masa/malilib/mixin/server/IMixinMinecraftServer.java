package fi.dy.masa.malilib.mixin.server;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface IMixinMinecraftServer
{
	@Accessor("combinedDynamicRegistries")
	CombinedDynamicRegistries<ServerDynamicRegistryType> malilib_registries();
}

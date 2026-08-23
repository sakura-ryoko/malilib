package fi.dy.masa.malilib.util.game;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.hud.IMixinDebugHudProfile;

/**
 * You need to add the AW for the "ENTRIES" in the downstream mod.
 * There really is no other more "elegant" method for this to be able
 * to reliably register / unregister them.
 */
public class DebugHudUtils
{
	public static void register(Identifier id, @Nonnull DebugHudEntry entry)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		if (!DebugHudEntries.ENTRIES.containsKey(id))
		{
			MinecraftClient mc = MinecraftClient.getInstance();

			try
			{
				DebugHudEntries.ENTRIES.put(id, entry);
				MaLiLib.debugLog("DebugHudUtils#register(): Registered [{}]", id.toString());

				if (mc.debugHudEntryList == null) return;

				if (!mc.debugHudEntryList.visibilityMap.containsKey(id))
				{
					mc.debugHudEntryList.visibilityMap.put(id, DebugHudEntryVisibility.NEVER);
					mc.debugHudEntryList.saveProfileFile();
				}
			}
			catch (Throwable e)
			{
				MaLiLib.LOGGER.error("DebugHudUtils#register(): Exception registering Debug Hud Entry: '{}'; {}", id.toString(), e.getLocalizedMessage());
			}
		}
	}

	public static void unregister(Identifier id)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		MinecraftClient mc = MinecraftClient.getInstance();

		try
		{
			DebugHudEntries.ENTRIES.remove(id);

			if (mc.debugHudEntryList != null)
			{
				mc.debugHudEntryList.visibilityMap.remove(id);
				mc.debugHudEntryList.visibleEntries.remove(id);
				mc.debugHudEntryList.saveProfileFile();
			}
		}
		catch (Throwable e)
		{
			MaLiLib.LOGGER.error("DebugHudUtils#unregister(): Exception unregistering Debug Hud Entry: '{}'; {}", id.toString(), e.getLocalizedMessage());
		}
	}

	public static @Nullable DebugHudEntryVisibility getVisibility(Identifier id)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (DebugHudEntries.getEntries().containsKey(id) &&
			mc.debugHudEntryList != null &&
			mc.debugHudEntryList.visibilityMap.containsKey(id))
		{
			return mc.debugHudEntryList.visibilityMap.get(id);
		}

		return null;
	}

	public static void setVisibility(Identifier id, DebugHudEntryVisibility visibility)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (DebugHudEntries.ENTRIES.containsKey(id) && mc.debugHudEntryList != null)
		{
			mc.debugHudEntryList.visibilityMap.put(id, visibility);
		}
	}
}

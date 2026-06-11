package fi.dy.masa.malilib.util.game;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import fi.dy.masa.malilib.MaLiLib;

/**
 * You need to add the AW for the "ENTRIES" in the downstream mod.
 * There really is no other more "elegant" method for this to be able
 * to reliably register / unregister them.
 */
public class DebugHudUtils
{
	public static void register(Identifier id, @Nonnull DebugScreenEntry entry)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) { return; }
		if (!DebugScreenEntries.ENTRIES_BY_ID.containsKey(id))
		{
			Minecraft mc = Minecraft.getInstance();

			try
			{
				DebugScreenEntries.ENTRIES_BY_ID.put(id, entry);
				MaLiLib.debugLog("DebugHudUtils#register(): Registered [{}]", id.toString());

				if (mc.debugEntries == null) return;

				if (!mc.debugEntries.allStatuses.containsKey(id))
				{
					mc.debugEntries.allStatuses.put(id, DebugScreenEntryStatus.NEVER);
					mc.debugEntries.save();
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
		if (Objects.equals(id.getNamespace(), "minecraft")) { return; }
		Minecraft mc = Minecraft.getInstance();

		try
		{
			DebugScreenEntries.ENTRIES_BY_ID.remove(id);
			MaLiLib.debugLog("DebugHudUtils#register(): Unregistered [{}]", id.toString());

			if (mc.debugEntries != null)
			{
				mc.debugEntries.allStatuses.remove(id);
				mc.debugEntries.currentlyEnabled.remove(id);
				mc.debugEntries.save();
			}
		}
		catch (Throwable e)
		{
			MaLiLib.LOGGER.error("DebugHudUtils#unregister(): Exception unregistering Debug Hud Entry: '{}'; {}", id.toString(), e.getLocalizedMessage());
		}
	}

	public static @Nullable DebugScreenEntryStatus getVisibility(Identifier id)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.ENTRIES_BY_ID.containsKey(id) &&
			mc.debugEntries != null &&
			mc.debugEntries.allStatuses.containsKey(id))
		{
			return mc.debugEntries.allStatuses.get(id);
		}

		return null;
	}

	public static void setVisibility(Identifier id, DebugScreenEntryStatus visibility)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.ENTRIES_BY_ID.containsKey(id) && mc.debugEntries != null)
		{
			mc.debugEntries.allStatuses.put(id, visibility);
		}
	}
}

package fi.dy.masa.malilib.util.data;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.gui.GuiBase;
import org.jetbrains.annotations.NotNull;

/**
 * Post-ReWrite code
 */
public record ModInfo(String modId, String modName, @Nullable Supplier<GuiBase> configScreenSupplier)
{
	public static final ModInfo NO_MOD = new ModInfo("-", "-");

	public ModInfo(String modId, String modName)
	{
		this(modId, modName, null);
	}

	/**
	 * @return the mod ID of this mod
	 */
	@Override
	public String modId()
	{
		return this.modId;
	}

	/**
	 * @return the human-friendly mod name of this mod
	 */
	@Override
	public String modName()
	{
		return this.modName;
	}

	/**
	 * @return the supplier for the config screen for this mod, or null if there is none
	 */
	@Override
	@Nullable
	public Supplier<GuiBase> configScreenSupplier()
	{
		return configScreenSupplier;
	}

	@Override
	public @NotNull String toString()
	{
		return "ModInfo{modId='" + this.modId + "', modName='" + this.modName + "'}";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || this.getClass() != o.getClass())
		{
			return false;
		}

		ModInfo modInfo = (ModInfo) o;

		if (!this.modId.equals(modInfo.modId))
		{
			return false;
		}
		return this.modName.equals(modInfo.modName);
	}

	@Override
	public int hashCode()
	{
		int result = this.modId.hashCode();
		result = 31 * result + this.modName.hashCode();
		return result;
	}
}

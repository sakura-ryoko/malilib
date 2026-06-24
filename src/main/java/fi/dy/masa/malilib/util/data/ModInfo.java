package fi.dy.masa.malilib.util.data;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.gui.GuiBase;

/**
 * Post-ReWrite code
 */
public class ModInfo
{
    public static final ModInfo NO_MOD = new ModInfo("-", "-");

    protected final String modId;
    protected final String modName;
    protected final @Nullable Supplier<GuiBase> configScreenSupplier;

    public ModInfo(String modId, String modName)
    {
        this.modId = modId;
        this.modName = modName;
        this.configScreenSupplier = null;
    }

    public ModInfo(String modId, String modName, @Nullable Supplier<GuiBase> configScreenSupplier)
    {
        this.modId = modId;
        this.modName = modName;
        this.configScreenSupplier = configScreenSupplier;
    }

    /**
     * @return the mod ID of this mod
     */
    public String getModId()
    {
        return this.modId;
    }

    /**
     * @return the human-friendly mod name of this mod
     */
    public String getModName()
    {
        return this.modName;
    }

    /**
     * @return the supplier for the config screen for this mod, or null if there is none
     */
    @Nullable
    public Supplier<GuiBase> getConfigScreenSupplier() {
        return configScreenSupplier;
    }

    @Override
    public String toString()
    {
        return "ModInfo{modId='" + this.modId + "', modName='" + this.modName + "'}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ModInfo modInfo = (ModInfo) o;

        if (!this.modId.equals(modInfo.modId)) { return false; }
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

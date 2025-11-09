package fi.dy.masa.malilib.data;

import fi.dy.masa.malilib.MaLiLibReference;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CachedTagKey(String modId, String tag)
{
    public static CachedTagKey fromString(String str)
    {
        ResourceLocation id = ResourceLocation.tryParse(str);

        if (id != null)
        {
            return new CachedTagKey(id.getNamespace(), id.getPath());
        }

        return new CachedTagKey(MaLiLibReference.MOD_ID, str);
    }

    @Override
    public @NotNull String toString()
    {
        return this.modId() + ":" + this.tag();
    }
}

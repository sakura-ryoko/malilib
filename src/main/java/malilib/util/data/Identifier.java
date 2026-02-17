package malilib.util.data;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;

/**
 * This is a simple dummy "wrapper" class that just extends the vanilla ResourceLocation/Identifier class.
 * The sole purpose of this class is to hide that mapping difference from lots of the places in
 * the mods' code, where an identifier is needed.
 */
public class Identifier extends ResourceLocation
{
    public Identifier(ResourceLocation resourceLocation)
    {
        this(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    public Identifier(String resourceName)
    {
        super(resourceName);
    }

    public Identifier(String namespaceIn, String pathIn)
    {
        super(namespaceIn, pathIn);
    }

    public static Identifier of(String resourceName)
    {
        return new Identifier(resourceName);
    }

    public static Identifier of(String namespaceIn, String pathIn)
    {
        return new Identifier(namespaceIn, pathIn);
    }
}

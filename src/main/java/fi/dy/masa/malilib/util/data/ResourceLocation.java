package fi.dy.masa.malilib.util.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Wraps the Mojmap "ResourceLocation" with Identifier
 * -
 * Post-ReWrite code
 */
@Deprecated(forRemoval = true)
public class ResourceLocation
{
    public static final Codec<ResourceLocation> CODEC = RecordCodecBuilder.create(
            resourceLocationInstance -> resourceLocationInstance.group(
                    net.minecraft.resources.ResourceLocation.CODEC.fieldOf("id").forGetter(get -> get.id)
            ).apply(resourceLocationInstance, ResourceLocation::new)
    );
    public static final StreamCodec<ByteBuf, ResourceLocation> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(ResourceLocation::of, ResourceLocation::toString);
    private final net.minecraft.resources.ResourceLocation id;

    public ResourceLocation(String str)
    {
        this.id = net.minecraft.resources.ResourceLocation.parse(str);
    }

    public ResourceLocation(String name, String path)
    {
        this.id = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(name, path);
    }

    public ResourceLocation(net.minecraft.resources.ResourceLocation id)
    {
        this.id = id;
    }

    public static ResourceLocation of(String str)
    {
        return new ResourceLocation(str);
    }

    public static ResourceLocation of(String name, String path)
    {
        return new ResourceLocation(name, path);
    }

    public static ResourceLocation ofVanilla(String path)
    {
        return new ResourceLocation("minecraft", path);
    }

    public static ResourceLocation of(net.minecraft.resources.ResourceLocation id)
    {
        return new ResourceLocation(id);
    }

    public static List<ResourceLocation> of(List<net.minecraft.resources.ResourceLocation> list)
    {
        List<ResourceLocation> newList = new ArrayList<>();

        list.forEach((id) -> newList.add(ResourceLocation.of(id)));

        return newList;
    }

    public @Nullable net.minecraft.resources.ResourceLocation getId()
    {
        return this.id;
    }

    public String getNamespace()
    {
        return this.id.getNamespace();
    }

    public String getPath()
    {
        return this.id.getPath();
    }

    public String toTranslationKey()
    {
        return this.id.getNamespace()+"."+this.id.getPath();
    }

    @Override
    public String toString()
    {
        return this.id.toString();
    }
}

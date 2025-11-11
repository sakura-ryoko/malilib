package fi.dy.masa.malilib.util.data;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface IEnumCodecProvider extends StringIdentifiable
{
    int getIndex();

    String getStringValue();

//    @ApiStatus.Experimental
//    default <T extends IEnumCodecProvider> Codec<T> codec() { return null; }
//
//    @Nullable
//    @ApiStatus.Experimental
//    default IEnumCodecProvider fromJsonCodec(JsonElement json)
//    {
//        if (this.codec() == null) return null;
//
//        try
//        {
//            return this.codec().decode(JsonOps.INSTANCE, json).resultOrPartial().orElseThrow().getFirst();
//        }
//        catch (Exception err)
//        {
//            MaLiLib.LOGGER.warn("IEnumCodecProvider#fromJsonCodec(): Error: {}", err.getLocalizedMessage());
//            return null;
//        }
//    }
//
//    @ApiStatus.Experimental
//    default JsonElement toJsonCodec()
//    {
//        if (this.codec() == null) return new JsonObject();
//
//        try
//        {
//            return this.codec().encodeStart(JsonOps.INSTANCE, this).resultOrPartial().orElse(new JsonObject());
//        }
//        catch (Exception err)
//        {
//            MaLiLib.LOGGER.warn("IEnumCodecProvider#toJsonCodec(): Error: {}", err.getLocalizedMessage());
//            return new JsonObject();
//        }
//    }
}

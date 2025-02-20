package fi.dy.masa.malilib.util.data;

import net.minecraft.util.StringIdentifiable;

public interface IEnumCodecProvider extends StringIdentifiable
{
    int getIndex();
    String getName();
}

package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

public interface IConfigOptionList
{
    @ApiStatus.Experimental
    Codec<? extends IConfigOptionList> codec();

    IConfigOptionListEntry getOptionListValue();

    IConfigOptionListEntry getDefaultOptionListValue();

    void setOptionListValue(IConfigOptionListEntry value);
}

package fi.dy.masa.malilib.util.i18n;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum i18nMode implements IConfigOptionListEntry, StringRepresentable
{
    FOLLOW_VANILLA  ("vanilla",     "malilib.label.lang_mode.vanilla",      "malilib.gui.button.hovertext.lang_mode.vanilla"),
    FOLLOW_MALILIB  ("malilib",     "malilib.label.lang_mode.malilib",      "malilib.gui.button.hovertext.lang_mode.malilib"),
    INDEPENDENT     ("independent", "malilib.label.lang_mode.independent",  "malilib.gui.button.hovertext.lang_mode.independent"),
    OFF             ("off",         "malilib.label.lang_mode.off",          "malilib.gui.button.hovertext.lang_mode.off"),
    ;

    public static final EnumCodec<@NotNull i18nMode> CODEC = StringRepresentable.fromEnum(i18nMode::values);
    public static final StreamCodec<@NotNull ByteBuf, @NotNull i18nMode> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(i18nMode::fromStringStatic, i18nMode::getSerializedName);
    public static final ImmutableList<@NotNull i18nMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;
    private final String hoverText;

    i18nMode(String configString, String translationKey, String hoverText)
    {
        this.configString = configString;
        this.translationKey = translationKey;
        this.hoverText = hoverText;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public List<String> getHoverText()
    {
        List<String> result = new ArrayList<>();

        result.add(StringUtils.translate(this.hoverText));

        return result;
    }

    @Override
    public @Nonnull String getSerializedName()
    {
        return this.configString;
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public i18nMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static i18nMode fromStringStatic(String name)
    {
        for (i18nMode mode : i18nMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return i18nMode.FOLLOW_VANILLA;
    }
}

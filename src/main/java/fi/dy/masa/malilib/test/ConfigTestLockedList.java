package fi.dy.masa.malilib.test;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.config.IConfigLockedListType;
import fi.dy.masa.malilib.util.StringUtils;

@ApiStatus.Experimental
public class ConfigTestLockedList implements IConfigLockedListType
{
    public static final ConfigTestLockedList INSTANCE = new ConfigTestLockedList();
    public ImmutableList<Entry> VALUES = ImmutableList.copyOf(Entry.values());
    public static final Codec<ConfigTestLockedList> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Entry.CODEC.listOf().fieldOf("values").forGetter(get -> get.VALUES)
            ).apply(inst, ConfigTestLockedList::new)
    );

    private ConfigTestLockedList() { }

    private ConfigTestLockedList(List<Entry> list)
    {
        ImmutableList.Builder<IConfigLockedListEntry> builder = ImmutableList.builder();

        list.forEach(builder::add);

        VALUES = ImmutableList.<Entry>builder().build();
    }

    @Override
    public ImmutableList<IConfigLockedListEntry> getDefaultEntries()
    {
        ImmutableList.Builder<IConfigLockedListEntry> list = ImmutableList.builder();

        VALUES.forEach((list::add));

        return list.build();
    }

    @Override
    @Nullable
    public IConfigLockedListEntry fromString(String element)
    {
        return Entry.fromString(element);
    }

    public enum Entry implements IConfigLockedListEntry, StringIdentifiable
    {
        TEST1 ("test1", "test1"),
        TEST2 ("test2", "test2"),
        TEST3 ("test3", "test3"),
        TEST4 ("test4", "test4");

        public static final StringIdentifiable.EnumCodec<Entry> CODEC = StringIdentifiable.createCodec(Entry::values);
        public static final ImmutableList<Entry> VALUES = ImmutableList.copyOf(values());

        private final String configKey;
        private final String translationKey;

        Entry(String configKey, String translationKey)
        {
            this.configKey = configKey;
            this.translationKey = MaLiLibReference.MOD_ID+".gui.label.locked_test."+translationKey;
        }

//        @Override
//        public Codec<Entry> codec()
//        {
//            return CODEC;
//        }
//
        @Override
        public String getStringValue()
        {
            return this.configKey;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.getTranslatedOrFallback(this.translationKey, this.configKey);
        }

        @Nullable
        public static Entry fromString(String key)
        {
            for (Entry entry : values())
            {
                if (entry.configKey.equalsIgnoreCase(key))
                {
                    return entry;
                }
                else if (entry.translationKey.equalsIgnoreCase(key))
                {
                    return entry;
                }
                else if (StringUtils.hasTranslation(entry.translationKey) && StringUtils.translate(entry.translationKey).equalsIgnoreCase(key))
                {
                    return entry;
                }
            }

            return null;
        }

        @Override
        public @Nonnull String asString()
        {
            return this.configKey;
        }
    }
}

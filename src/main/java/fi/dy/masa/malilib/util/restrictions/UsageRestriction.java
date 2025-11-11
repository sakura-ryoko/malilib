package fi.dy.masa.malilib.util.restrictions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class UsageRestriction<TYPE>
{
    protected ListType type = ListType.NONE;
    protected final HashSet<TYPE> blackList = new HashSet<>();
    protected final HashSet<TYPE> whiteList = new HashSet<>();

    public void setListType(ListType type)
    {
        this.type = type;
    }

    public ListType getListType()
    {
        return this.type;
    }

    public void setListContents(List<String> namesBlacklist, List<String> namesWhitelist)
    {
        this.setValuesForList(ListType.BLACKLIST, namesBlacklist);
        this.setValuesForList(ListType.WHITELIST, namesWhitelist);
    }

    public Set<TYPE> getListForType(ListType type)
    {
        return type == ListType.WHITELIST ? this.whiteList : this.blackList;
    }

    public void setValuesForList(ListType type, List<String> names)
    {
        Set<TYPE> set = this.getListForType(type);
        set.clear();

        this.setValuesForList(set, names);
    }

    protected abstract void setValuesForList(Set<TYPE> set, List<String> names);

    public boolean isAllowed(TYPE value)
    {
        if (this.type == ListType.BLACKLIST)
        {
            return this.blackList.contains(value) == false;
        }
        else if (this.type == ListType.WHITELIST)
        {
            return this.whiteList.contains(value);
        }
        else
        {
            return true;
        }
    }

    public enum ListType implements IConfigOptionListEntry, StringIdentifiable
    {
        NONE        ("none",        "malilib.label.list_type.none"),
        BLACKLIST   ("blacklist",   "malilib.label.list_type.blacklist"),
        WHITELIST   ("whitelist",   "malilib.label.list_type.whitelist");

        public static final StringIdentifiable.EnumCodec<ListType> CODEC = StringIdentifiable.createCodec(ListType::values);
        public static final ImmutableList<ListType> VALUES = ImmutableList.copyOf(values());

        private final String configString;
        private final String translationKey;

        ListType(String configString, String translationKey)
        {
            this.configString = configString;
            this.translationKey = translationKey;
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
        public ListType fromString(String name)
        {
            return fromStringStatic(name);
        }

        public static ListType fromStringStatic(String name)
        {
            for (ListType mode : ListType.values())
            {
                if (mode.configString.equalsIgnoreCase(name))
                {
                    return mode;
                }
            }

            return ListType.NONE;
        }

        @Override
        public @Nonnull String asString()
        {
            return this.configString;
        }
    }
}

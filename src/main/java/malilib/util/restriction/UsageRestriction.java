package malilib.util.restriction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionConfigValue;
import malilib.config.value.BlackWhiteList;

public class UsageRestriction<TYPE>
{
    protected final HashSet<TYPE> blackList = new HashSet<>();
    protected final HashSet<TYPE> whiteList = new HashSet<>();
    protected final Predicate<TYPE> valueValidator;
    protected ListType type = ListType.NONE;

    public UsageRestriction()
    {
        this((v) -> true);
    }

    public UsageRestriction(Predicate<TYPE> valueValidator)
    {
        this.valueValidator = valueValidator;
    }

    public ListType getListType()
    {
        return this.type;
    }

    public void setListType(ListType type)
    {
        this.type = type;
    }

    /**
     * Returns either the blacklist or the whitelist Set for the given {@link ListType}.
     * Note: The method returns the whitelist for {@link ListType#WHITELIST}, otherwise the Set for the blacklist
     * (meaning for {@link ListType#NONE} the blacklist Set is returned).
     * @param type
     * @return
     */
    protected Set<TYPE> getListForType(ListType type)
    {
        return type == ListType.WHITELIST ? this.whiteList : this.blackList;
    }

    /**
     * Sets both the black- and whitelist contents based on the provided names, replacing any old values.
     * @param list
     */
    public <T extends TYPE> void setListContents(BlackWhiteList<T> list)
    {
        this.type = list.getListType();
        this.setValuesForList(this.getListForType(ListType.BLACKLIST), list.getBlackList().getValue());
        this.setValuesForList(this.getListForType(ListType.WHITELIST), list.getWhiteList().getValue());
    }

    /**
     * Should set the values for the given Set based on the values in the provided List.
     * @param set
     * @param values
     */
    protected <T extends TYPE> void setValuesForList(Set<TYPE> set, List<T> values)
    {
        set.clear();

        for (TYPE value : values)
        {
            if (value != null && this.valueValidator.test(value))
            {
                set.add(value);
            }
        }
    }

    /**
     * Check if the given object is currently allowed by this restriction
     * @param value
     * @return
     */
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

    public static class ListType extends BaseOptionConfigValue
    {
        public static final ListType NONE      = new ListType("none",        "malilib.label.list_type.none");
        public static final ListType BLACKLIST = new ListType("blacklist",   "malilib.label.list_type.blacklist");
        public static final ListType WHITELIST = new ListType("whitelist",   "malilib.label.list_type.whitelist");

        public static final ImmutableList<ListType> VALUES = ImmutableList.of(NONE, BLACKLIST, WHITELIST);

        private ListType(String name, String translationKey)
        {
            super(name, translationKey);
        }
    }
}

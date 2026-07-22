package malilib.action;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;

import malilib.action.util.ActionUtils;
import malilib.config.value.BaseOptionConfigValue;
import malilib.registry.Registry;

public class ActionGroup extends BaseOptionConfigValue
{
    public static final ActionGroup ALL             = new ActionGroup("all",             "malilib.name.action_group.all",             Registry.ACTION_REGISTRY::getAllActions);
    public static final ActionGroup BASE            = new ActionGroup("base",            "malilib.name.action_group.base",            Registry.ACTION_REGISTRY::getBaseActions);
    public static final ActionGroup ALIAS           = new ActionGroup("alias",           "malilib.name.action_group.alias",           Registry.ACTION_REGISTRY::getAliases);
    public static final ActionGroup MACRO           = new ActionGroup("macro",           "malilib.name.action_group.macro",           Registry.ACTION_REGISTRY::getMacros);
    public static final ActionGroup PARAMETERIZED   = new ActionGroup("parameterized",   "malilib.name.action_group.parameterized",   Registry.ACTION_REGISTRY::getParameterizedActions);
    public static final ActionGroup PARAMETERIZABLE = new ActionGroup("parameterizable", "malilib.name.action_group.parameterizable", ActionUtils::getParameterizableActions);
    public static final ActionGroup USER_ADDED      = new ActionGroup("user_added",      "malilib.name.action_group.user_added",      ActionUtils::getUserAddedActions);
    public static final ActionGroup SIMPLE          = new ActionGroup("simple",          "malilib.name.action_group.simple",          ActionUtils::getSimpleActions);

    public static final ImmutableList<ActionGroup> VALUES = ImmutableList.of(ALL, BASE, ALIAS, MACRO, PARAMETERIZED, PARAMETERIZABLE, USER_ADDED, SIMPLE);
    public static final ImmutableList<ActionGroup> VALUES_USER_ADDED = ImmutableList.of(ALIAS, MACRO, PARAMETERIZED, USER_ADDED);

    private final Supplier<List<? extends NamedAction>> listSource;

    private ActionGroup(String name, String translationKey, Supplier<List<? extends NamedAction>> listSource)
    {
        super(name, translationKey);

        this.listSource = listSource;
    }

    public ImmutableList<NamedAction> getActions()
    {
        return ImmutableList.copyOf(this.listSource.get());
    }
}

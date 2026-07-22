package malilib.input;

import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionConfigValue;

public class CancelCondition extends BaseOptionConfigValue
{
    public static final CancelCondition NEVER       = new CancelCondition("never",       "malilib.name.cancel_condition.never");
    public static final CancelCondition ALWAYS      = new CancelCondition("always",      "malilib.name.cancel_condition.always");
    public static final CancelCondition ON_SUCCESS  = new CancelCondition("on_success",  "malilib.name.cancel_condition.on_success");
    public static final CancelCondition ON_FAILURE  = new CancelCondition("on_failure",  "malilib.name.cancel_condition.on_failure");

    public static final ImmutableList<CancelCondition> VALUES = ImmutableList.of(NEVER, ALWAYS, ON_SUCCESS, ON_FAILURE);

    public CancelCondition(String name, String translationKey)
    {
        super(name, translationKey);
    }
}

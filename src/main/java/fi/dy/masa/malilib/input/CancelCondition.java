package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

@ApiStatus.Experimental
public class CancelCondition extends BaseOptionListConfigValue
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

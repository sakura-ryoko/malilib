package malilib.overlay.message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionConfigValue;
import malilib.render.text.StyledText;
import malilib.util.ListUtils;

public class MessageOutput extends BaseOptionConfigValue
{
    private static ImmutableList<MessageOutput> VALUES = ImmutableList.of();
    private static final Map<String, MessageOutput> TYPES_BY_NAME = new HashMap<>();

    public static final MessageOutput MESSAGE_OVERLAY = register("message",        DefaultMessageDispatchers::sendOverlayMessageString,                 DefaultMessageDispatchers::sendOverlayMessageText);
    public static final MessageOutput CUSTOM_HOTBAR   = register("custom_hotbar",  DefaultMessageDispatchers::sendCustomHotbarMessageString,            DefaultMessageDispatchers::sendCustomHotbarMessageText);
    public static final MessageOutput VANILLA_HOTBAR  = register("vanilla_hotbar", DefaultMessageDispatchers::sendVanillaHotbarMessageString,           DefaultMessageDispatchers::sendVanillaHotbarMessageText);
    public static final MessageOutput TOAST           = register("toast",          DefaultMessageDispatchers::sendToastMessageString,                   DefaultMessageDispatchers::sendToastMessageText);
    public static final MessageOutput CHAT            = register("chat",           DefaultMessageDispatchers::sendVanillaChatMessageString,             DefaultMessageDispatchers::sendVanillaChatMessageText);
    public static final MessageOutput DEFAULT_TOGGLE  = register("default_toggle", DefaultMessageDispatchers::sendStringToDefaultToggleMessageOutput,   DefaultMessageDispatchers::sendTextToDefaultToggleMessageOutput);
    public static final MessageOutput NONE            = register("none",           DefaultMessageDispatchers::dummyStringMessageDispatcher,             DefaultMessageDispatchers::dummyStyledTextMessageDispatcher);

    protected final BiConsumer<String, MessageDispatcher> stringMessageDispatcher;
    protected final BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher;

    public MessageOutput(String name,
                         String translationKey,
                         BiConsumer<String, MessageDispatcher> stringMessageDispatcher,
                         BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher)
    {
        super(name, translationKey);

        this.stringMessageDispatcher = stringMessageDispatcher;
        this.styledTextMessageDispatcher = styledTextMessageDispatcher;
    }

    public void send(String message, MessageDispatcher messageDispatcher)
    {
        this.stringMessageDispatcher.accept(message, messageDispatcher);
    }

    public void send(StyledText text, MessageDispatcher messageDispatcher)
    {
        this.styledTextMessageDispatcher.accept(text, messageDispatcher);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        MessageOutput that = (MessageOutput) o;

        return this.name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    private static MessageOutput register(String name,
                                          BiConsumer<String, MessageDispatcher> stringMessageDispatcher,
                                          BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher)
    {
        return register(name, "malilib.name.message_output." + name,
                        stringMessageDispatcher, styledTextMessageDispatcher);
    }

    public static MessageOutput register(String name,
                                         String translationKey,
                                         BiConsumer<String, MessageDispatcher> stringMessageDispatcher,
                                         BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher)
    {
        
        MessageOutput type = new MessageOutput(name, translationKey, stringMessageDispatcher, styledTextMessageDispatcher);

        // The type is compared by name only, so replace any potential old value with the new value
        VALUES = ListUtils.replaceOrAddValue(VALUES, type, type, true);
        TYPES_BY_NAME.put(name, type);

        return type;
    }

    public static MessageOutput getByName(String name)
    {
        return TYPES_BY_NAME.getOrDefault(name, NONE);
    }

    public static ImmutableList<MessageOutput> getValues()
    {
        return VALUES;
    }
}

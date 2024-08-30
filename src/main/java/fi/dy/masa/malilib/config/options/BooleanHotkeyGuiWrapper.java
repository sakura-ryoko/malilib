package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IKeybind;

/**
 * This is just a temporary hack solution to get booleans and hotkeys to the same line in the config GUIs.
 * DO NOT USE this for actual config values!! This will not get serialized and deserialized properly!!
 * This is only intended as a wrapper type in the config GUIs for now,
 * until the proper malilib rewrite from 1.12.2 is ready to be ported!
 */
public class BooleanHotkeyGuiWrapper extends ConfigBoolean
{
    protected final IConfigBoolean booleanConfig;
    protected final IKeybind keybind;

    public BooleanHotkeyGuiWrapper(String name, IConfigBoolean booleanConfig, IKeybind keybind)
    {
        super(name, booleanConfig.getDefaultBooleanValue(), booleanConfig.getComment(), booleanConfig.getPrettyName(), booleanConfig.getTranslatedName());
        this.booleanConfig = booleanConfig;
        this.keybind = keybind;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.booleanConfig.getBooleanValue();
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.booleanConfig.setBooleanValue(value);
    }

    @Override
    public BooleanHotkeyGuiWrapper translatedName(String translatedName)
    {
        return (BooleanHotkeyGuiWrapper) super.translatedName(translatedName);
    }

    @Override
    public BooleanHotkeyGuiWrapper apply(String translatePrefix)
    {
        return (BooleanHotkeyGuiWrapper) super.apply(translatePrefix);
    }

    @Override
    @Nullable
    public String getTranslatedName()
    {
        return this.booleanConfig.getTranslatedName();
    }

    @Override
    public boolean isModified()
    {
        // Note: calling isModified() for the IHotkey here directly would not work
        // with multi-type configs like the FeatureToggle in Tweakeroo!
        // Thus we need to get the IKeybind and call it for that specifically.
        return this.booleanConfig.isModified() || this.getKeybind().isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.booleanConfig.resetToDefault();
        this.getKeybind().resetToDefault();
    }

    public IConfigBoolean getBooleanConfig()
    {
        return this.booleanConfig;
    }

    public IKeybind getKeybind()
    {
        return this.keybind;
    }
}

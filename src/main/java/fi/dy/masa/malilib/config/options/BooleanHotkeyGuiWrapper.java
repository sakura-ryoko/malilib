package fi.dy.masa.malilib.config.options;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

/**
 * This is just a temporary hack solution to get booleans and hotkeys to the same line in the config GUIs.
 * DO NOT USE this for actual config values!! This will not get serialized and deserialized properly!!
 * This is only intended as a wrapper type in the config GUIs for now,
 * until the proper malilib rewrite from 1.12.2 is ready to be ported!
 */
public class BooleanHotkeyGuiWrapper extends ConfigBoolean
{
    public static final Codec<BooleanHotkeyGuiWrapper> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                                        PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                                        PrimitiveCodec.BOOL.fieldOf("defaultValue").forGetter(get -> get.booleanConfig.getDefaultBooleanValue()),
                                        PrimitiveCodec.BOOL.fieldOf("value").forGetter(get -> get.booleanConfig.getBooleanValue()),
                                        PrimitiveCodec.STRING.fieldOf("defaultHotkey").forGetter(get -> get.keybind.getDefaultStringValue()),
                                        KeybindSettings.CODEC.fieldOf("keybindSettings").forGetter(get -> get.keybind.getSettings()),
                                        PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                                        PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                                        PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
                                )
                                .apply(instance, BooleanHotkeyGuiWrapper::new)
    );
    protected final IConfigBoolean booleanConfig;
    protected final IKeybind keybind;

    public BooleanHotkeyGuiWrapper(String name, IConfigBoolean booleanConfig, IKeybind keybind)
    {
        super(name, booleanConfig.getDefaultBooleanValue(), booleanConfig.getComment(), booleanConfig.getPrettyName(), booleanConfig.getTranslatedName());
        this.booleanConfig = booleanConfig;
        this.keybind = keybind;
    }

    private BooleanHotkeyGuiWrapper(String name, boolean defaultValue, boolean value, String defaultHotkey, KeybindSettings settings, String comment, String prettyName, String translatedName)
    {
        super(name, defaultValue, comment, prettyName, translatedName);
        this.booleanConfig = this;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.booleanConfig.getBooleanValue();
    }

    @Override
    public void setBooleanValue(boolean value)
    {
		boolean oldValue = this.booleanConfig.getBooleanValue();
        this.booleanConfig.setBooleanValue(value);

		if (oldValue != this.booleanConfig.getBooleanValue())
		{
			this.onValueChanged();
		}
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
//		boolean oldValue = this.booleanConfig.getBooleanValue();
        this.booleanConfig.resetToDefault();
        this.getKeybind().resetToDefault();

//		if (this.getKeybind().isDirty() || oldValue != this.booleanConfig.getBooleanValue())
//		{
//			this.getKeybind().markClean();
//			this.onValueChanged();
//		}
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

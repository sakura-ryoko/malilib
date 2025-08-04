package malilib.input;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.config.util.ConfigUtils;
import malilib.registry.Registry;
import malilib.util.BackupUtils;
import malilib.util.data.json.JsonUtils;

public class CustomHotkeyManager implements HotkeyProvider
{
    public static final CustomHotkeyManager INSTANCE = new CustomHotkeyManager();

    protected final List<CustomHotkeyDefinition> allHotkeys = new ArrayList<>();
    protected final List<CustomHotkeyDefinition> enabledHotkeys = new ArrayList<>();
    protected boolean dirty;

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return this.allHotkeys;
    }

    @Override
    public List<CustomHotkeyDefinition> getEnabledHotkeys()
    {
        return this.enabledHotkeys;
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(new HotkeyCategory(MaLiLibReference.MOD_INFO,
                                                   "malilib.hotkeys.category.custom", this::getAllHotkeys));
    }

    public List<CustomHotkeyDefinition> getAllCustomHotkeys()
    {
        return this.allHotkeys;
    }

    public void addCustomHotkey(CustomHotkeyDefinition hotkey)
    {
        this.allHotkeys.add(hotkey);
        this.dirty = true;
    }

    public void removeCustomHotkey(CustomHotkeyDefinition hotkey)
    {
        this.allHotkeys.remove(hotkey);
        this.dirty = true;
    }

    public void clear()
    {
        this.allHotkeys.clear();
        this.enabledHotkeys.clear();
        this.dirty = false;
    }

    public void markDirty()
    {
        this.dirty = true;
    }

    public boolean checkDirtySaveAndUpdate()
    {
        boolean dirty = this.dirty;

        if (dirty == false)
        {
            for (CustomHotkeyDefinition hotkey : this.allHotkeys)
            {
                if (hotkey.getKeyBind().isDirty())
                {
                    dirty = true;
                    break;
                }
            }
        }

        this.updateEnabledHotkeysList();

        if (dirty)
        {
            this.saveToFile();
            Registry.HOTKEY_MANAGER.updateUsedKeys();
        }

        return dirty;
    }

    protected void updateEnabledHotkeysList()
    {
        this.enabledHotkeys.clear();

        for (CustomHotkeyDefinition hotkey : this.allHotkeys)
        {
            if (hotkey.isEnabled())
            {
                this.enabledHotkeys.add(hotkey);
            }
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            return this.saveToFile();
        }

        return false;
    }

    protected JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (CustomHotkeyDefinition hotkey : this.allHotkeys)
        {
            arr.add(hotkey.toJson());
            hotkey.getKeyBind().cacheSavedValue();
        }

        obj.add("hotkeys", arr);

        return obj;
    }

    protected void fromJson(JsonElement el)
    {
        this.clear();

        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();
            JsonUtils.getArrayElementsIfExists(obj, "hotkeys", this::readAndAddHotkey);
        }

        this.updateEnabledHotkeysList();
    }

    protected void readAndAddHotkey(JsonElement el)
    {
        CustomHotkeyDefinition hotkey = CustomHotkeyDefinition.fromJson(el);

        if (hotkey != null)
        {
            this.allHotkeys.add(hotkey);
        }
    }

    public boolean saveToFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_hotkeys.json");
        Path backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID);

        if (BackupUtils.createRegularBackup(saveFile, backupDir) &&
            JsonUtils.writeJsonToFile(this.toJson(), saveFile))
        {
            this.dirty = false;
            return true;
        }

        return false;
    }

    public void loadFromFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_hotkeys.json");
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }
}

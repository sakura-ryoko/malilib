package malilib.config.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.multiplayer.ServerData;

import malilib.MaLiLib;
import malilib.MaLiLibConfigs;
import malilib.MaLiLibReference;
import malilib.action.NamedAction;
import malilib.config.ConfigManagerImpl;
import malilib.config.ModConfig;
import malilib.config.category.ConfigOptionCategory;
import malilib.config.option.BaseGenericConfig;
import malilib.config.option.ConfigOption;
import malilib.config.serialization.JsonConfigSerializerRegistry.ConfigFromJsonOverrider;
import malilib.input.Hotkey;
import malilib.input.HotkeyManagerImpl;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.FileNameUtils;
import malilib.util.FileUtils;
import malilib.util.HttpUtils;
import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.GameWrap;

public class ConfigLockHandler
{
    protected final Map<Pair<ConfigOptionCategory, BaseGenericConfig<?>>, Pair<JsonElement, String>> configOverrides = new HashMap<>();
    protected final Map<ModInfo, Map<ConfigOptionCategory, List<BaseGenericConfig<?>>>> overridableConfigs = new HashMap<>();
    protected final Map<ModInfo, List<NamedAction>> allBaseActions = new HashMap<>();
    protected final Map<ModInfo, List<Hotkey>> allHotkeys = new HashMap<>();
    protected final Map<NamedAction, String> lockedActions = new HashMap<>();
    protected final Map<Hotkey, String> lockedHotkeys = new HashMap<>();

    public void readAndApplyLocks()
    {
        this.initLocks();

        // Overrides that come later will override previous definitions.
        // The server overrides are handled last so that they have priority over other types.
        try
        {
            this.readLocksFromLocalCommonConfig();
            this.readLocksFromLocalPerWorldConfig();

            if (GameWrap.isSinglePlayer())
            {
                this.readLocksFromSinglePlayerWorldConfig();
            }
            else
            {
                this.readLocksFromServer();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to apply config overrides", e);
        }

        this.applyLocksAndPrintMessage();
    }

    public void applyLocksFromServer(JsonObject obj)
    {
        this.initLocks();
        this.readLockConfigFromJson(obj);
        this.applyLocksAndPrintMessage();
    }

    public void clearLocks()
    {
        this.fetchAllLockableConfigs();
        this.overridableConfigs.values().forEach(map -> map.values().forEach(list -> list.forEach(BaseGenericConfig::disableOverride)));

        Registry.ACTION_REGISTRY.clearActionLocks();
        ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).clearHotkeyLocks();
    }

    protected void initLocks()
    {
        this.configOverrides.clear();
        this.lockedActions.clear();
        this.lockedHotkeys.clear();

        this.fetchAllLockableConfigs();
        this.fetchAllActions();
        this.fetchAllHotkeys();

        if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
        {
            int configCount = this.overridableConfigs.values().stream().mapToInt(m -> m.values().stream().mapToInt(List::size).sum()).sum();
            MaLiLib.debugLog("  > There are {} overridable configs in the game", configCount);

            int actionCount = this.allBaseActions.values().stream().mapToInt(List::size).sum();
            MaLiLib.debugLog("  > There are {} lockable actions in the game", actionCount);

            int hotkeyCount = this.allHotkeys.values().stream().mapToInt(List::size).sum();
            MaLiLib.debugLog("  > There are {} lockable hotkeys in the game", hotkeyCount);
        }
    }

    protected void fetchAllLockableConfigs()
    {
        this.overridableConfigs.clear();

        for (ModConfig modConfig : ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigs())
        {
            Map<ConfigOptionCategory, List<BaseGenericConfig<?>>> map = new HashMap<>();

            for (ConfigOptionCategory category : modConfig.getConfigOptionCategories())
            {
                List<BaseGenericConfig<?>> list = new ArrayList<>();

                for (ConfigOption<?> config : category.getConfigOptions())
                {
                    ConfigFromJsonOverrider<?> overrider = Registry.JSON_CONFIG_SERIALIZER.getOverrider(config);

                    if (overrider != null && config instanceof BaseGenericConfig)
                    {
                        list.add((BaseGenericConfig<?>) config);
                    }
                }

                if (list.isEmpty() == false)
                {
                    map.put(category, list);
                }
            }

            if (map.isEmpty() == false)
            {
                this.overridableConfigs.put(modConfig.getModInfo(), map);
            }
        }
    }

    protected void fetchAllActions()
    {
        this.allBaseActions.clear();

        for (NamedAction action : Registry.ACTION_REGISTRY.getBaseActions())
        {
            this.allBaseActions.computeIfAbsent(action.getModInfo(), k -> new ArrayList<>()).add(action);
        }
    }

    protected void fetchAllHotkeys()
    {
        this.allHotkeys.clear();

        List<Hotkey> list = new ArrayList<>();
        Registry.HOTKEY_MANAGER.getHotkeyCategories().forEach(c -> list.addAll(c.getHotkeys()));

        for (Hotkey hotkey : list)
        {
            this.allHotkeys.computeIfAbsent(hotkey.getKeyBind().getModInfo(), k -> new ArrayList<>()).add(hotkey);
        }
    }

    protected void applyLocksAndPrintMessage()
    {
        try
        {
            MaLiLib.debugLog("  > Applying {} config overrides...", this.configOverrides.size());
            int count = this.applyConfigOverrides();

            MaLiLib.debugLog("  > Applied {} config overrides", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.config_overrides_applied", count);
            }

            count = this.applyActionLocks();

            MaLiLib.debugLog("  > Applied {} action locks", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.action_locks_applied", count);
            }

            count = this.applyHotkeyLocks();

            MaLiLib.debugLog("  > Applied {} hotkey locks", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.hotkey_locks_applied", count);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to apply config locks", e);
        }
    }

    protected void readLocksFromSinglePlayerWorldConfig()
    {
        Path worldDir = GameWrap.getCurrentSinglePlayerWorldDirectory();
        String fileName = "malilib_config_locks.json";
        Path file = worldDir.resolve(fileName);

        this.readLockConfigFromFile(file);
    }

    protected void readLocksFromLocalPerWorldConfig()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        String worldName = malilib.util.StringUtils.getWorldOrServerNameOrDefault("fallback");
        worldName = FileNameUtils.generateSimpleSafeFileName(worldName);
        String fileName = "config_locks_world_" + worldName + ".json";
        Path file = configDir.resolve(MaLiLibReference.MOD_ID).resolve(fileName);

        this.readLockConfigFromFile(file);
    }

    protected void readLocksFromLocalCommonConfig()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path file = configDir.resolve(MaLiLibReference.MOD_ID).resolve("config_locks_common.json");

        this.readLockConfigFromFile(file);
    }

    protected void readLocksFromServer()
    {
        ServerData serverData = GameWrap.getClient().getCurrentServerData();

        if (serverData != null)
        {
            String motd = serverData.serverMOTD;
            String[] lines = motd.split("\\n");

            for (int index = 3; index < lines.length; ++index)
            {
                String str = lines[index];

                if (this.readLockConfigFromString(str))
                {
                    return;
                }

                if (this.readLockConfigFromURL(str))
                {
                    return;
                }
            }
        }
    }

    protected void readLockConfigFromFile(Path file)
    {
        if (Files.isRegularFile(file))
        {
            String str = FileUtils.readFileAsString(file, -1);

            if (str != null)
            {
                this.readLockConfigFromString(str);
            }
        }
    }

    protected boolean readLockConfigFromURL(String str)
    {
        str = str.trim();

        if (str.startsWith("http://") || str.startsWith("https://"))
        {
            if (str.endsWith("§r"))
            {
                // Strip away the ending '§r'
                str = str.substring(0, str.length() - 2);
            }

            String pageContent = HttpUtils.tryFetchPage(str, 2000);

            if (pageContent != null)
            {
                return this.readLockConfigFromString(pageContent);
            }
        }

        return false;
    }

    protected boolean readLockConfigFromString(String str)
    {
        str = str.trim();

        if (StringUtils.isBlank(str) || str.charAt(0) != '{')
        {
            return false;
        }

        int lastBrace = str.lastIndexOf('}');

        if (lastBrace <= 1)
        {
            return false;
        }

        // Strip away any junk from the end, like the ending '§r'
        if (lastBrace < str.length() - 1)
        {
            str = str.substring(0, lastBrace + 1);
        }

        JsonElement el = JsonUtils.parseJsonFromString(str);

        MaLiLib.debugLog("Cleaned up lock config JSON as string: '{}'", str);
        MaLiLib.debugLog("Parsed lock JSON: '{}'", el);

        if (el != null && el.isJsonObject())
        {
            return this.readLockConfigFromJson(el.getAsJsonObject());
        }

        return false;
    }

    protected boolean readLockConfigFromJson(JsonObject root)
    {
        boolean foundOverrides = false;

        foundOverrides |= this.lockReader(root, "malilib_config_overrides", this::readLocksFrom);
        foundOverrides |= this.lockReader(root, "malilib_action_locks", this::readActionLocksFrom);
        foundOverrides |= this.lockReader(root, "malilib_hotkey_locks", this::readHotkeyLocksFrom);

        return foundOverrides;
    }

    protected boolean lockReader(JsonObject root, String arrayName, BiFunction<JsonObject, TestsAndMessage, TestsAndMessage> overrideHandler)
    {
        if (JsonUtils.hasArray(root, arrayName))
        {
            JsonArray overridesArray = root.get(arrayName).getAsJsonArray();
            MaLiLib.debugLog("  > Found {} top level lock definitions", overridesArray.size());
            TestsAndMessage data = new TestsAndMessage(s -> true, s -> true, s -> true, null);
            JsonUtils.getArrayElementsAsObjects(overridesArray, o -> this.readLockFromArrayElement(o, data, overrideHandler));

            return true;
        }

        return false;
    }

    protected void readLockFromArrayElement(JsonObject obj,
                                            TestsAndMessage data,
                                            BiFunction<JsonObject, TestsAndMessage, TestsAndMessage> overrideHandler)
    {
        TestsAndMessage data2 = overrideHandler.apply(obj, data);

        if (JsonUtils.hasArray(obj, "overrides"))
        {
            JsonArray overrideArr = obj.get("overrides").getAsJsonArray();
            MaLiLib.debugLog("    > Found {} policy overrides", overrideArr.size());
            JsonUtils.getArrayElementsAsObjects(overrideArr, o -> this.readLockFromArrayElement(o, data2, overrideHandler));
        }
    }

    protected TestsAndMessage readLocksFrom(JsonObject obj, TestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> categoryTest = this.getCategoryTestOrDefault(obj, data.categoryTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new TestsAndMessage(modTest, categoryTest, nameTest, message);

        JsonElement overrideValue = obj.get("override_value");
        Pair<JsonElement, String> overrideData = Pair.of(overrideValue, message);
        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean enableOverride = "override".equalsIgnoreCase(policy);
        int overrideCount = 0;

        if (overrideValue == null && enableOverride)
        {
            MaLiLib.debugLog("      > Error: no 'override_value' found in override definition {}", obj);
            return data;
        }

        MaLiLib.debugLog("      > Reading override rule, policy = {}", policy);

        for (ModInfo modInfo : this.overridableConfigs.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (Map<ConfigOptionCategory, List<BaseGenericConfig<?>>> map : this.overridableConfigs.values())
            {
                for (Map.Entry<ConfigOptionCategory, List<BaseGenericConfig<?>>> entry : map.entrySet())
                {
                    if (categoryTest.test(entry.getKey().getName()) == false)
                    {
                        continue;
                    }

                    for (BaseGenericConfig<?> cfg : entry.getValue())
                    {
                        if (nameTest.test(cfg.getName()) == false)
                        {
                            continue;
                        }

                        Pair<ConfigOptionCategory, BaseGenericConfig<?>> cac = Pair.of(entry.getKey(), cfg);

                        if (enableOverride)
                        {
                            this.configOverrides.put(cac, overrideData);
                        }
                        else
                        {
                            this.configOverrides.remove(cac);
                        }

                        ++overrideCount;
                    }
                }
            }
        }

        if (enableOverride)
        {
            MaLiLib.debugLog("      > Found {} override rules", overrideCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} override removal rules", overrideCount);
        }

        return data;
    }

    protected TestsAndMessage readActionLocksFrom(JsonObject obj, TestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new TestsAndMessage(modTest, null, nameTest, message);

        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean shouldDisable = "disable".equalsIgnoreCase(policy);
        int ruleCount = 0;

        MaLiLib.debugLog("      > Reading action lock rule, policy = {}", policy);

        for (ModInfo modInfo : this.allBaseActions.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (NamedAction action : this.allBaseActions.get(modInfo))
            {
                if (nameTest.test(action.getName()) == false)
                {
                    continue;
                }

                if (shouldDisable)
                {
                    this.lockedActions.put(action, message);
                }
                else
                {
                    this.lockedActions.remove(action);
                }

                ++ruleCount;
            }
        }

        if (shouldDisable)
        {
            MaLiLib.debugLog("      > Found {} action lock rules", ruleCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} action lock removal rules", ruleCount);
        }

        return data;
    }

    protected TestsAndMessage readHotkeyLocksFrom(JsonObject obj, TestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new TestsAndMessage(modTest, null, nameTest, message);

        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean shouldDisable = "disable".equalsIgnoreCase(policy);
        int ruleCount = 0;

        MaLiLib.debugLog("      > Reading hotkey lock rule, policy = {}", policy);

        for (ModInfo modInfo : this.allHotkeys.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (Hotkey hotkey : this.allHotkeys.get(modInfo))
            {
                if (nameTest.test(hotkey.getName()) == false)
                {
                    continue;
                }

                if (shouldDisable)
                {
                    this.lockedHotkeys.put(hotkey, message);
                }
                else
                {
                    this.lockedHotkeys.remove(hotkey);
                }

                ++ruleCount;
            }
        }

        if (shouldDisable)
        {
            MaLiLib.debugLog("      > Found {} hotkey lock rules", ruleCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} hotkey lock removal rules", ruleCount);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    protected <T, C extends BaseGenericConfig<T>> int applyConfigOverrides()
    {
        int count = 0;

        for (Map.Entry<Pair<ConfigOptionCategory, BaseGenericConfig<?>>, Pair<JsonElement, String>> entry : this.configOverrides.entrySet())
        {
            Pair<ConfigOptionCategory, BaseGenericConfig<?>> pair = entry.getKey();
            Pair<JsonElement, String> overrideData = entry.getValue();
            JsonElement overrideValue = overrideData.getLeft();
            String name = "";

            if (overrideValue == null)
            {
                MaLiLib.LOGGER.warn("ConfigOverrideHandler#applyConfigOverrides(): Missing override value for '{}'", pair.getRight().getName());
                continue;
            }

            try
            {
                C cfg = (C) pair.getRight();
                name = cfg.getName();
                ConfigFromJsonOverrider<C> overrider = Registry.JSON_CONFIG_SERIALIZER.getOverrider(cfg);

                if (overrider != null)
                {
                    String msg = entry.getValue().getRight();

                    if (cfg instanceof BaseGenericConfig)
                    {
                        T oldValue = cfg.getValue();
                        overrider.overrideConfigValue(cfg, overrideValue);
                        cfg.setOverrideMessage(msg);

                        Object newValue = ((BaseGenericConfig<?>) cfg).getValue();
                        MaLiLib.debugLog("    Overrode '{}.{}' from '{}' to '{}' with message '{}'",
                                         pair.getLeft().getName(), name, oldValue, newValue, msg);
                    }
                    else
                    {
                        overrider.overrideConfigValue(cfg, overrideValue);
                        cfg.setOverrideMessage(msg);

                        MaLiLib.debugLog("    Overrode '{}.{}' from (unknown value) to '{}' with message '{}'",
                                         pair.getLeft().getName(), name, overrideValue, msg);
                    }

                    ++count;
                }

            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("applyOverrides(): Exception while trying to override the value of '{}'", name);
            }
        }

        return count;
    }

    protected int applyActionLocks()
    {
        Registry.ACTION_REGISTRY.setLockedActions(this.lockedActions);
        return this.lockedActions.size();
    }

    protected int applyHotkeyLocks()
    {
        ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).setLockedHotkeys(this.lockedHotkeys);
        return this.lockedHotkeys.size();
    }

    @Nullable
    protected Predicate<String> getStringTest(JsonObject obj, String keyName, String errorMsgName)
    {
        Predicate<String> test = null;

        if (JsonUtils.hasString(obj, keyName))
        {
            String str = JsonUtils.getString(obj, keyName);

            try
            {
                Pattern pattern = Pattern.compile(str);
                test = (name) -> pattern.matcher(name).matches();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to compile {} regex '{}'", errorMsgName, str);
                test = (name) -> false;
            }
        }

        return test;
    }

    @Nullable
    protected Predicate<String> getNameTest(JsonObject obj)
    {
        Predicate<String> test = this.getStringTest(obj, "name_regex", "mod name");

        if (test == null && JsonUtils.hasArray(obj, "names"))
        {
            final Set<String> names = new HashSet<>();
            final String prefix = JsonUtils.getStringOrDefault(obj, "name_prefix", "");
            final String suffix = JsonUtils.getStringOrDefault(obj, "name_suffix", "");
            JsonArray arr = obj.get("names").getAsJsonArray();
            final int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                names.add(prefix + arr.get(i).getAsString() + suffix);
            }

            test = names::contains;
        }

        return test;
    }

    protected Predicate<String> getModTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getStringTest(obj, "mod", "mod name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected Predicate<String> getCategoryTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getStringTest(obj, "category", "category name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected Predicate<String> getNameTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getNameTest(obj);
        return filter != null ? filter : defaultFilter;
    }

    protected static class TestsAndMessage
    {
        @Nullable public final Predicate<String> modTest;
        @Nullable public final Predicate<String> categoryTest;
        @Nullable public final Predicate<String> nameTest;
        @Nullable public final String message;

        public TestsAndMessage(@Nullable Predicate<String> modTest,
                               @Nullable Predicate<String> categoryTest,
                               @Nullable Predicate<String> nameTest,
                               @Nullable String message)
        {
            this.modTest = modTest;
            this.categoryTest = categoryTest;
            this.nameTest = nameTest;
            this.message = message;
        }
    }
}

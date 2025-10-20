package fi.dy.masa.malilib.config.options;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigTable;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigTable extends ConfigBase<ConfigTable> implements IConfigTable {
    public static final Codec<ConfigTable> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                    PrimitiveCodec.STRING.fieldOf("comment").forGetter(ConfigBase::getComment),
                    PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(ConfigBase::getPrettyName),
                    PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(ConfigBase::getTranslatedName),
                    PrimitiveCodec.STRING.fieldOf("displayString").forGetter(get -> get.displayString == null ? "n" : "s" + get.displayString),
                    Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("defaultTable").forGetter(get -> {
                        List<List<String>> table = new ArrayList<>();
                        for (Entry entry : get.getDefaultTable()) {
                            List<String> entry2 = new ArrayList<>();
                            for (Object obj : entry.list) {
                                switch (obj) {
                                    case String str -> entry2.add("str" + str);
                                    case Integer integer -> entry2.add("int" + integer);
                                    case Double dbl -> entry2.add("dbl" + dbl);
                                    default ->
                                            throw new IllegalStateException("Unsupported type: " + obj.getClass().getName());
                                }
                            }
                            table.add(entry2);
                        }
                        return table;
                    }),
                    Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("table").forGetter(get -> {
                        List<List<String>> table = new ArrayList<>();
                        for (Entry list : get.getTable()) {
                            List<String> entry = new ArrayList<>();
                            for (Object obj : list.list) {
                                switch (obj) {
                                    case String str -> entry.add("str" + str);
                                    case Integer integer -> entry.add("int" + integer);
                                    case Double dbl -> entry.add("dbl" + dbl);
                                    default ->
                                            throw new IllegalStateException("Unsupported type: " + obj.getClass().getName());
                                }
                            }
                            table.add(entry);
                        }
                        return table;
                    }),
                    Codecs.listOrSingle(PrimitiveCodec.STRING).fieldOf("labels").forGetter(ConfigTable::getLabels),
                    PrimitiveCodec.BOOL.fieldOf("showEntryNumbers").forGetter(ConfigTable::showEntryNumbers),
                    PrimitiveCodec.BOOL.fieldOf("allowNewEntry").forGetter(ConfigTable::allowNewEntry),
                    PrimitiveCodec.STRING.listOf().fieldOf("types").forGetter(get -> {
                        List<String> typeNames = new ArrayList<>();
                        for (Class<?> type : get.types) {
                            if (type == String.class) {
                                typeNames.add("str");
                            } else if (type == Integer.class) {
                                typeNames.add("int");
                            } else if (type == Double.class) {
                                typeNames.add("dbl");
                            } else {
                                throw new IllegalStateException("Unsupported type: " + type.getName());
                            }
                        }
                        return typeNames;
                    })
            ).apply(inst, ConfigTable::new)
    );

    private final ImmutableList<Entry> defaultTable;
    private final List<Entry> table = new ArrayList<>();
    private final @Nullable String displayString;
    private final ImmutableList<Class<?>> types;
    private final List<String> labels;
    private final boolean allowNewEntry;
    private final boolean showEntryNumbers;

    private ConfigTable(String name, String comment, String prettyName, String translatedName, String displayString, List<List<String>> defaultValue, List<List<String>> value, List<String> labels, Boolean showEntryNumbers, Boolean allowAddNewEntry, List<String> types) {
        this(name, comment, prettyName, translatedName, strip(displayString), parse(defaultValue), labels, showEntryNumbers, allowAddNewEntry, parseTypes(types));
        this.table.addAll(parse(value));
    }

    private static List<Entry> parse(List<List<String>> defaultValue) {
        List<Entry> temp = new ArrayList<>();
        for (List<String> list : defaultValue) {
            Entry entryList = new Entry();
            for (String entry : list) {
                String typeName = entry.substring(0, 3);
                String valueString = entry.substring(3);
                switch (typeName) {
                    case "str" -> entryList.add(valueString);
                    case "int" -> entryList.add(Integer.valueOf(valueString));
                    case "dbl" -> entryList.add(Double.valueOf(valueString));
                    default -> throw new IllegalStateException("Unsupported type name: " + typeName);
                }
            }
            temp.add(entryList);
        }
        return temp;
    }

    private static Class<?>[] parseTypes(List<String> types) {
        List<Class<?>> temp = new ArrayList<>();
        for (String typeName : types) {
            switch (typeName) {
                case "str" -> temp.add(String.class);
                case "int" -> temp.add(Integer.class);
                case "dbl" -> temp.add(Double.class);
                default -> throw new IllegalStateException("Unsupported type name: " + typeName);
            }
        }
        return temp.toArray(new Class<?>[0]);
    }

    private static @Nullable String strip(String displayString) {
        if (displayString.equals("n")) {
            return null;
        } else if (displayString.startsWith("s")) {
            return displayString.substring(1);
        } else {
            throw new IllegalStateException("Unsupported display string: " + displayString);
        }
    }

    private ConfigTable(String name, String comment, String prettyName, String translatedName,
                       @Nullable String displayString, List<Entry> defaultValue,
                       List<String> labels, boolean showEntryNumbers, boolean allowAddNewEntry,
                       Class<?>... types) {
        super(null, name, comment, prettyName, translatedName);
        this.labels = labels;
        this.allowNewEntry = allowAddNewEntry;
        this.showEntryNumbers = showEntryNumbers;

        ImmutableList.Builder<Class<?>> ilb = ImmutableList.builder();
        for (Class<?> type : types) {
            ilb.add(type);
        }

        this.types = ilb.build();
        this.displayString = displayString;
        ImmutableList.Builder<Entry> ilb2 = ImmutableList.builder();
        for (Entry list : defaultValue) {
            Entry newEntry = new Entry();
            newEntry.list.addAll(List.copyOf(list.list));
            ilb2.add(newEntry);
        }
        this.defaultTable = ilb2.build();
        this.table.addAll(defaultTable);
    }

    @Override
    public List<Entry> getTable() {
        return table;
    }

    @Override
    public List<List<Object>> getRawTable() {
        List<List<Object>> rawTable = new ArrayList<>();
        for (Entry entry : table) {
            List<Object> rawEntry = new ArrayList<>(entry.list);
            rawTable.add(rawEntry);
        }
        return rawTable;
    }

    @Override
    public ImmutableList<Entry> getDefaultTable() {
        return defaultTable;
    }

    @Override
    public ImmutableList<List<Object>> getDefaultRawTable() {
        ImmutableList.Builder<List<Object>> ilb = new ImmutableList.Builder<>();
        for (Entry entry : defaultTable) {
            List<Object> rawEntry = new ArrayList<>(entry.list);
            ilb.add(rawEntry);
        }
        return ilb.build();
    }

    @Override
    public void setTable(List<Entry> newTable) {
        if (!this.table.equals(newTable)) {
            this.table.clear();
            this.table.addAll(newTable);
            this.onValueChanged();
        }
    }

    @Override
    public void setModified() {
        this.onValueChanged();
    }

    @Override
    public @Nullable String getDisplayString() {
        return this.displayString;
    }

    @Override
    public List<Class<?>> getTypes() {
        return types;
    }

    @Override
    public void resetToDefault() {
        setTable(defaultTable);
    }

    @Override
    public boolean isModified() {
        return !table.equals(defaultTable);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        List<List<Object>> oldTable = new ArrayList<>();
        for (Entry entry : table) {
            oldTable.add(new ArrayList<>(entry.list));
        }
        table.clear();
        try {
            JsonArray arr = element.getAsJsonArray();

            for (JsonElement el : arr) {
                if (!(el instanceof JsonArray jarr)) {
                    throw new Exception();

                }
                List<Object> tempList = new ArrayList<>();
                for (JsonElement el2 : jarr) {
                    if (el2.isJsonPrimitive()) {
                        if (el2.getAsJsonPrimitive().isString()) {
                            tempList.add(el2.getAsString());
                        } else if (el2.getAsJsonPrimitive().isNumber()) {
                            Number num = el2.getAsNumber();
                            try {
                                tempList.add(Integer.valueOf(el2.getAsString()));
                            } catch (Exception e) {
                                tempList.add(num.doubleValue());
                            }
                        } else {
                            throw new Exception();
                        }
                    } else {
                        throw new Exception();
                    }
                }
                table.add(new Entry(tempList));
            }

            if (!table.equals(oldTable)) {
                onValueChanged();
            }
        } catch (Exception e) {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        JsonArray tableArr = new JsonArray();

        for (var entry : table) {
            JsonArray entryArr = new JsonArray();
            for (var obj : entry.list) {
                if (obj instanceof String str) {
                    entryArr.add(str);
                } else if (obj instanceof Integer integer) {
                    entryArr.add(integer);
                } else if (obj instanceof Double dbl) {
                    entryArr.add(dbl);
                }
            }
            tableArr.add(entryArr);
        }

        return tableArr;
    }

    @Override
    public List<String> getLabels() {
        return labels;
    }

    @Override
    public boolean allowNewEntry() {
        return allowNewEntry;
    }

    @Override
    public boolean showEntryNumbers() {
        return showEntryNumbers;
    }

    public static class Entry {
        public final List<Object> list;

        public Entry() {
            this.list = new ArrayList<>();
        }

        public Entry(List<Object> list) {
            this.list = list;
        }

        public Entry(Object... objs) {
            this.list = new ArrayList<>();
            Collections.addAll(this.list, objs);
        }

        public static Entry of(Object... objs) {
            Entry entry = new Entry();
            for (Object obj : objs) {
                entry.add(obj);
            }
            return entry;
        }

        void add(Object obj) {
            this.list.add(obj);
        }

        Object get(int index) {
            return this.list.get(index);
        }
        
        Integer getInt(int index) {
            return (Integer) this.list.get(index);
        }
        
        Double getDouble(int index) {
            return (Double) this.list.get(index);
        }
        
        String getString(int index) {
            return (String) this.list.get(index);
        }
    }

    public static class Builder {
        private static @NotNull Entry getDummy(List<Class<?>> types) {
            Entry dummy = new Entry();
            for (Class<?> type : types) {
                if (type == String.class) {
                    dummy.add("");
                } else if (type == Integer.class) {
                    dummy.add(0);
                } else if (type == Double.class) {
                    dummy.add(0.0);
                } else {
                    throw new IllegalStateException("Unsupported type: " + type.getName());
                }
            }
            return dummy;
        }

        private String name;
        private String comment = null;
        private String prettyName = null;
        private String translatedName = null;
        private @Nullable String displayString = null;
        private List<Entry> defaultValue = null;
        private List<String> labels = List.of();
        private boolean showEntryNumbers = true;
        private boolean allowAddNewEntry = true;
        private Class<?>[] types;

        private int entryCount = -1;

        public Builder(String name, Class<?>... types) {
            this.name = name;
            this.types = types;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setPrettyName(String prettyName) {
            this.prettyName = prettyName;
            return this;
        }

        public Builder setTranslatedName(String translatedName) {
            this.translatedName = translatedName;
            return this;
        }

        public Builder setDisplayString(@Nullable String displayString) {
            this.displayString = displayString;
            return this;
        }

        public Builder setDefaultValue(List<Entry> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setDefaultValue(Entry... defaultValue) {
            this.defaultValue = List.of(defaultValue);
            return this;
        }

        public Builder setLabels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder setLabels(String... labels) {
            this.labels = List.of(labels);
            return this;
        }

        public Builder setShowEntryNumbers(boolean showEntryNumbers) {
            this.showEntryNumbers = showEntryNumbers;
            return this;
        }

        public Builder setAllowAddNewEntry(boolean allowAddNewEntry) {
            this.allowAddNewEntry = allowAddNewEntry;
            return this;
        }

        public Builder setTypes(Class<?>... types) {
            this.types = types;
            return this;
        }

        public Builder setEntryCount(@Range(from = 1, to = Integer.MAX_VALUE) int count) {
            this.entryCount = count;
            return this;
        }

        public ConfigTable build() {
            if (defaultValue == null){
                defaultValue = new ArrayList<>();
            }
            if (defaultValue.size() == 1 && entryCount > 0) {
                for (int i = 0; i < entryCount; i++) {
                    defaultValue.add(new Entry(defaultValue.getFirst().list));
                }
            } else if (entryCount > 0){
                for (int i = 0; i < entryCount; i++) {
                    defaultValue.add(getDummy(List.of(types)));
                }
            } else {
                defaultValue.add(getDummy(List.of(types)));
            }
            if (comment == null) comment = name + " Comment?";
            if (prettyName == null) prettyName = name;
            if (translatedName == null) translatedName = name;

            if (labels.size() != types.length) {
                throw new IllegalArgumentException("Labels size mismatch: expected " + types.length + " but got " + labels.size());
            }
            for (Entry v : defaultValue) {
                for (int j = 0; j < types.length; j++) {
                    if (v.list.get(j).getClass() != types[j] || (types[j] != Integer.class && types[j] != Double.class && types[j] != String.class)) {
                        throw new IllegalArgumentException("Type mismatch: expected " + types[j] + " but got " + v.list.get(j).getClass());
                    }
                }
            }

            return new ConfigTable(name, comment, prettyName, translatedName, displayString, defaultValue, labels, showEntryNumbers, allowAddNewEntry, types);
        }
    }
}

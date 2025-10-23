package fi.dy.masa.malilib.config.options.table;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.table.type.*;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
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
                        for (TableRow row : get.getDefaultTable()) {
                            List<String> temp = new ArrayList<>();
                            for (Entry entry : row.list) {
                                switch (entry) {
                                    case StringEntry str -> temp.add("str" + str.getValue());
                                    case IntegerEntry integer -> temp.add("int" + integer.getValue());
                                    case DoubleEntry dbl -> temp.add("dbl" + dbl.getValue());
                                    case KeybindEntry kbe -> temp.add("key" + kbe.getStringValue());
                                    default ->
                                            throw new IllegalStateException("Unsupported type: " + entry.getType());
                                }
                            }
                            table.add(temp);
                        }
                        return table;
                    }),
                    Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("table").forGetter(get -> {
                        List<List<String>> table = new ArrayList<>();
                        for (TableRow row : get.getTable()) {
                            List<String> temp = new ArrayList<>();
                            for (Entry entry : row.list) {
                                switch (entry) {
                                    case StringEntry str -> temp.add("str" + str.getValue());
                                    case IntegerEntry integer -> temp.add("int" + integer.getValue());
                                    case DoubleEntry dbl -> temp.add("dbl" + dbl.getValue());
                                    case KeybindEntry kbe -> temp.add("key" + kbe.getStringValue());
                                    default ->
                                            throw new IllegalStateException("Unsupported type: " + entry.getType());
                                }
                            }
                            table.add(temp);
                        }
                        return table;
                    }),
                    Codecs.listOrSingle(PrimitiveCodec.STRING).fieldOf("labels").forGetter(ConfigTable::getLabels),
                    PrimitiveCodec.BOOL.fieldOf("showEntryNumbers").forGetter(ConfigTable::showEntryNumbers),
                    PrimitiveCodec.BOOL.fieldOf("allowNewEntry").forGetter(ConfigTable::allowNewEntry),
                    PrimitiveCodec.STRING.listOf().fieldOf("types").forGetter(get -> {
                        List<String> typeNames = new ArrayList<>();
                        for (EntryTypes type : get.types) {
                            switch (type) {
                                case EntryTypes.STRING -> typeNames.add("str");
                                case EntryTypes.INTEGER -> typeNames.add("int");
                                case EntryTypes.DOUBLE -> typeNames.add("dbl");
                                default -> throw new IllegalStateException("Unsupported type: " + type.name());
                            }
                        }
                        return typeNames;
                    })
            ).apply(inst, ConfigTable::new)
    );

    private final ImmutableList<TableRow> defaultTable;
    private final List<TableRow> table = new ArrayList<>() {
        @Override
        public TableRow get(int index) {
            return super.get(index);
        }

        @Override
        public TableRow getFirst() {
            return super.getFirst();
        }

        @Override
        public TableRow getLast() {
            return super.getLast();
        }

        @Override
        public TableRow set(int index, TableRow element) {
            return super.set(index, element);
        }

        @Override
        public boolean add(TableRow tableRow) {
            return super.add(tableRow);
        }

        @Override
        public void add(int index, TableRow element) {
            super.add(index, element);
        }

        @Override
        public void addFirst(TableRow element) {
            super.addFirst(element);
        }

        @Override
        public void addLast(TableRow element) {
            super.addLast(element);
        }

        @Override
        public TableRow remove(int index) {
            return super.remove(index);
        }

        @Override
        public TableRow removeFirst() {
            return super.removeFirst();
        }

        @Override
        public TableRow removeLast() {
            return super.removeLast();
        }

        @Override
        public void clear() {
            super.clear();
        }
    };
    private final @Nullable String displayString;
    private final ImmutableList<EntryTypes> types;
    private final List<String> labels;
    private final boolean allowNewEntry;
    private final boolean showEntryNumbers;

    private ConfigTable(String name, String comment, String prettyName, String translatedName, String displayString, List<List<String>> defaultValue, List<List<String>> value, List<String> labels, Boolean showEntryNumbers, Boolean allowAddNewEntry, List<String> types) {
        this(name, comment, prettyName, translatedName, strip(displayString), parse(defaultValue), labels, showEntryNumbers, allowAddNewEntry, parseTypes(types));
        this.table.addAll(parse(value));
    }

    private static List<TableRow> parse(List<List<String>> defaultValue) {
        List<TableRow> temp = new ArrayList<>();
        for (List<String> list : defaultValue) {
            TableRow entryList = new TableRow();
            for (String entry : list) {
                String typeName = entry.substring(0, 3);
                String valueString = entry.substring(3);
                switch (typeName) {
                    case "str" -> entryList.add(StringEntry.of(valueString));
                    case "int" -> entryList.add(IntegerEntry.of(Integer.parseInt(valueString)));
                    case "dbl" -> entryList.add(DoubleEntry.of(Double.parseDouble(valueString)));
                    case "key" -> entryList.add(KeybindEntry.from(valueString));
                    default -> throw new IllegalStateException("Unsupported type name: " + typeName);
                }
            }
            temp.add(entryList);
        }
        return temp;
    }

    private static EntryTypes[] parseTypes(List<String> types) {
        List<EntryTypes> temp = new ArrayList<>();
        for (String typeName : types) {
            switch (typeName) {
                case "str" -> temp.add(EntryTypes.STRING);
                case "int" -> temp.add(EntryTypes.INTEGER);
                case "dbl" -> temp.add(EntryTypes.DOUBLE);
                default -> throw new IllegalStateException("Unsupported type name: " + typeName);
            }
        }
        return temp.toArray(new EntryTypes[0]);
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
                       @Nullable String displayString, List<TableRow> defaultValue,
                       List<String> labels, boolean showEntryNumbers, boolean allowAddNewEntry,
                        EntryTypes... types) {
        super(null, name, comment, prettyName, translatedName);
        this.labels = labels;
        this.allowNewEntry = allowAddNewEntry;
        this.showEntryNumbers = showEntryNumbers;

        ImmutableList.Builder<EntryTypes> ilb = ImmutableList.builder();
        for (EntryTypes type : types) {
            ilb.add(type);
        }

        this.types = ilb.build();
        this.displayString = displayString;
        ImmutableList.Builder<TableRow> ilb2 = ImmutableList.builder();
        for (TableRow list : defaultValue) {
            TableRow newEntry = new TableRow();
            newEntry.list.addAll(List.copyOf(list.list));
            ilb2.add(newEntry);
        }
        this.defaultTable = ilb2.build();
        this.table.addAll(defaultTable);
    }

    @Override
    public List<TableRow> getTable() {
        return table;
    }

    @Override
    public List<List<Object>> getRawTable() {
        List<List<Object>> rawTable = new ArrayList<>();
        for (TableRow entry : table) {
            List<Object> rawEntry = new ArrayList<>(entry.list);
            rawTable.add(rawEntry);
        }
        return rawTable;
    }

    @Override
    public ImmutableList<TableRow> getDefaultTable() {
        return defaultTable;
    }

    @Override
    public ImmutableList<List<Object>> getDefaultRawTable() {
        ImmutableList.Builder<List<Object>> ilb = new ImmutableList.Builder<>();
        for (TableRow entry : defaultTable) {
            List<Object> rawEntry = new ArrayList<>(entry.list);
            ilb.add(rawEntry);
        }
        return ilb.build();
    }

    @Override
    public void setTable(List<TableRow> newTable) {
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
    public List<EntryTypes> getTypes() {
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
        List<TableRow> oldTable = new ArrayList<>();
        for (TableRow entry : table) {
            oldTable.add(new TableRow(entry.list));
        }
        table.clear();
        try {
            JsonArray arr = element.getAsJsonArray();

            for (JsonElement el : arr) {
                if (!(el instanceof JsonArray jarr)) {
                    throw new Exception();

                }
                List<Entry> tempList = new ArrayList<>();
                for (JsonElement el2 : jarr) {
                    if (el2.isJsonObject()) {
                        JsonObject obj = el2.getAsJsonObject();
                        if (obj.has("type")) {
                            switch (obj.get("type").getAsString()) {
                                case "keybind" -> tempList.add(KeybindEntry.getFromJsonObject(obj));
                                case "string"  -> tempList.add(StringEntry.getFromJsonObject(obj));
                                case "integer" -> tempList.add(IntegerEntry.getFromJsonObject(obj));
                                case "double"  -> tempList.add(DoubleEntry.getFromJsonObject(obj));
                            }
                        }
                    } else {
                        throw new Exception();
                    }
                }
                table.add(new TableRow(tempList));
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

        for (var row : table) {
            JsonArray entryArr = new JsonArray();
            for (Entry entry : row.list) {
                entryArr.add(entry.getAsJsonObject());
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

    public static @NotNull TableRow getDummy(List<EntryTypes> types) {
        TableRow dummy = new TableRow();
        for (EntryTypes type : types) {
            if (type == EntryTypes.STRING) {
                dummy.add(StringEntry.of(""));
            } else if (type == EntryTypes.INTEGER) {
                dummy.add(IntegerEntry.of(0));
            } else if (type == EntryTypes.DOUBLE) {
                dummy.add(DoubleEntry.of(0.0));
            } else if (type == EntryTypes.KEYBIND) {
                dummy.add(KeybindEntry.of(""));
            } else {
                throw new IllegalStateException("Unsupported type: " + type.name());
            }
        }
        return dummy;
    }

    public static class Builder {
        private String name;
        private String comment = null;
        private String prettyName = null;
        private String translatedName = null;
        private @Nullable String displayString = null;
        private List<TableRow> defaultValue = null;
        private List<String> labels = List.of();
        private boolean showEntryNumbers = true;
        private boolean allowAddNewEntry = true;
        private EntryTypes[] types;

        private int entryCount = -1;

        public Builder(String name, EntryTypes... types) {
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

        public Builder setDefaultValue(List<TableRow> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setDefaultValue(TableRow... defaultValue) {
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

        public Builder setTypes(EntryTypes... types) {
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
            } else {
                defaultValue = new ArrayList<>(defaultValue);
            }
            if (defaultValue.size() == 1 && entryCount > 0) {
                for (int i = 0; i < entryCount; i++) {
                    defaultValue.add(new TableRow(defaultValue.getFirst().list));
                }
            } else if (entryCount > 0){
                for (int i = 0; i < entryCount; i++) {
                    defaultValue.add(getDummy(List.of(types)));
                }
            }
            if (comment == null) comment = name + " Comment?";
            if (prettyName == null) prettyName = name;
            if (translatedName == null) translatedName = name;
            for (TableRow v : defaultValue) {
                for (int j = 0; j < types.length; j++) {
                    if (v.list.get(j).getType() != types[j]) {
                        throw new IllegalArgumentException("Type mismatch: expected " + types[j] + " but got " + v.list.get(j).getType().name());
                    }
                }
            }

            return new ConfigTable(name, comment, prettyName, translatedName, displayString, defaultValue, labels, showEntryNumbers, allowAddNewEntry, types);
        }
    }
}

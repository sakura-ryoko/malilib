package fi.dy.masa.malilib.config.options.table;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.table.type.*;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.dynamic.Codecs;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApiStatus.Experimental
public class ConfigTable extends ConfigBase<ConfigTable> implements IConfigTable
{
	public static final Codec<ConfigTable> CODEC = RecordCodecBuilder.create(
			inst -> inst.group(
					PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
					PrimitiveCodec.STRING.fieldOf("comment").forGetter(ConfigBase::getComment),
					PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(ConfigBase::getPrettyName),
					PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(ConfigBase::getTranslatedName),
					PrimitiveCodec.STRING.fieldOf("displayString").forGetter(get -> get.displayString == null ? "n" : "s" + get.displayString),
					Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("defaultTable").forGetter(get ->
					                                                                                      {
						                                                                                      List<List<String>> table = new ArrayList<>();
						                                                                                      for (TableRow row : get.getDefaultTable())
						                                                                                      {
							                                                                                      List<String> temp = new ArrayList<>();
							                                                                                      for (Entry entry : row.list())
							                                                                                      {
								                                                                                      switch (entry)
								                                                                                      {
									                                                                                      case StringEntry str ->
											                                                                                      temp.add("str" + str.getValue());
									                                                                                      case LabelEntry lbl ->
											                                                                                      temp.add("lbl" + lbl.getValue().label());
									                                                                                      case IntegerEntry integer ->
											                                                                                      temp.add("int" + integer.getValue());
									                                                                                      case DoubleEntry dbl ->
											                                                                                      temp.add("dbl" + dbl.getValue());
									                                                                                      case BooleanEntry bln ->
											                                                                                      temp.add("bln" + bln.getValue());
//                                    case KeybindEntry kbe -> temp.add("key" + kbe.getStringValue());
									                                                                                      default ->
											                                                                                      throw new IllegalStateException("Unsupported type: " + entry.getType());
								                                                                                      }
							                                                                                      }
							                                                                                      table.add(temp);
						                                                                                      }
						                                                                                      return table;
					                                                                                      }),
					Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("table").forGetter(get ->
					                                                                               {
						                                                                               List<List<String>> table = new ArrayList<>();
						                                                                               for (TableRow row : get.getTable())
						                                                                               {
							                                                                               List<String> temp = new ArrayList<>();
							                                                                               for (Entry entry : row.list())
							                                                                               {
								                                                                               switch (entry)
								                                                                               {
									                                                                               case StringEntry str ->
											                                                                               temp.add("str" + str.getValue());
									                                                                               case LabelEntry lbl ->
											                                                                               temp.add("lbl" + lbl.getValue().label());
									                                                                               case IntegerEntry integer ->
											                                                                               temp.add("int" + integer.getValue());
									                                                                               case DoubleEntry dbl ->
											                                                                               temp.add("dbl" + dbl.getValue());
									                                                                               case BooleanEntry bln ->
											                                                                               temp.add("bln" + bln.getValue());
//                                    case KeybindEntry kbe -> temp.add("key" + kbe.getStringValue());
									                                                                               default ->
											                                                                               throw new IllegalStateException("Unsupported type: " + entry.getType());
								                                                                               }
							                                                                               }
							                                                                               table.add(temp);
						                                                                               }
						                                                                               return table;
					                                                                               }),
					Codecs.listOrSingle(PrimitiveCodec.STRING.listOf()).fieldOf("labels").forGetter(get ->
                                                                                                   {
                                                                                                       ArrayList<List<String>> labels = new ArrayList<>();
                                                                                                       for (Label label : get.getLabels())
                                                                                                       {
                                                                                                           labels.add(List.of(label.label(), label.comment()));
                                                                                                       }
                                                                                                       return labels;
                                                                                                   }),
					PrimitiveCodec.BOOL.fieldOf("showEntryNumbers").forGetter(ConfigTable::showEntryNumbers),
					PrimitiveCodec.BOOL.fieldOf("allowNewEntry").forGetter(ConfigTable::allowNewEntry),
					PrimitiveCodec.STRING.listOf().fieldOf("types").forGetter(get ->
					                                                          {
						                                                          List<String> typeNames = new ArrayList<>();
						                                                          for (EntryTypes type : get.types)
						                                                          {
							                                                          switch (type)
							                                                          {
								                                                          case EntryTypes.STRING ->
										                                                          typeNames.add("str");
								                                                          case EntryTypes.LABEL ->
										                                                          typeNames.add("lbl");
								                                                          case EntryTypes.INTEGER ->
										                                                          typeNames.add("int");
								                                                          case EntryTypes.DOUBLE ->
										                                                          typeNames.add("dbl");
								                                                          case EntryTypes.BOOLEAN ->
										                                                          typeNames.add("bln");
								                                                          default ->
										                                                          throw new IllegalStateException("Unsupported type: " + type.name());
							                                                          }
						                                                          }
						                                                          return typeNames;
					                                                          })
			).apply(inst, ConfigTable::new)
	);

	private final ImmutableList<@NotNull TableRow> defaultTable;
	private final List<TableRow> table = new ArrayList<>();
	private final @Nullable String displayString;
	private final ImmutableList<@NotNull EntryTypes> types;
	private final List<Label> labels;
	private final boolean allowNewEntry;
	private final boolean showEntryNumbers;

	private ConfigTable(String name, String comment, String prettyName, String translatedName, String displayString, List<List<String>> defaultValue, List<List<String>> value, List<List<String>> labels, Boolean showEntryNumbers, Boolean allowAddNewEntry, List<String> types)
	{
		this(name, comment, prettyName, translatedName, strip(displayString), parse(defaultValue), parseLabels(labels), showEntryNumbers, allowAddNewEntry, parseTypes(types));
		this.table.addAll(parse(value));
	}

    private static List<Label> parseLabels(List<List<String>> labels)
    {
        List<Label> labelList = new ArrayList<>();
        for (List<String> label : labels)
        {
            labelList.add(Label.of(label.getFirst(), label.get(1)));
        }
        return labelList;
    }

    private static List<TableRow> parse(List<List<String>> defaultValue)
	{
		List<TableRow> temp = new ArrayList<>();
		for (List<String> list : defaultValue)
		{
			TableRow entryList = new TableRow();
			for (String entry : list)
			{
				String typeName = entry.substring(0, 3);
				String valueString = entry.substring(3);
				switch (typeName)
				{
					case "str" -> entryList.add(StringEntry.of(valueString));
					case "lbl" -> entryList.add(LabelEntry.of(valueString));
					case "int" -> entryList.add(IntegerEntry.of(Integer.parseInt(valueString)));
					case "dbl" -> entryList.add(DoubleEntry.of(Double.parseDouble(valueString)));
					case "bln" -> entryList.add(BooleanEntry.of(Boolean.parseBoolean(valueString)));
//                    case "key" -> entryList.add(KeybindEntry.from(valueString));
					default -> throw new IllegalStateException("Unsupported type name: " + typeName);
				}
			}
			temp.add(entryList);
		}
		return temp;
	}

	private static EntryTypes[] parseTypes(List<String> types)
	{
		List<EntryTypes> temp = new ArrayList<>();
		for (String typeName : types)
		{
			switch (typeName)
			{
				case "str" -> temp.add(EntryTypes.STRING);
				case "lbl" -> temp.add(EntryTypes.LABEL);
				case "int" -> temp.add(EntryTypes.INTEGER);
				case "dbl" -> temp.add(EntryTypes.DOUBLE);
				case "bln" -> temp.add(EntryTypes.BOOLEAN);
//                case "key" -> temp.add(EntryTypes.KEYBIND);
				default -> throw new IllegalStateException("Unsupported type name: " + typeName);
			}
		}
		return temp.toArray(new EntryTypes[0]);
	}

	private static @Nullable String strip(String displayString)
	{
		if (displayString.equals("n"))
		{
			return null;
		}
		else if (displayString.startsWith("s"))
		{
			return displayString.substring(1);
		}
		else
		{
			throw new IllegalStateException("Unsupported display string: " + displayString);
		}
	}

	private ConfigTable(String name, String comment, String prettyName, String translatedName,
	                    @Nullable String displayString, List<TableRow> defaultValue,
	                    List<Label> labels, boolean showEntryNumbers, boolean allowAddNewEntry,
	                    EntryTypes... types)
	{
		super(ConfigType.TABLE, name, comment, prettyName, translatedName);
		this.labels = labels;
		this.allowNewEntry = allowAddNewEntry;
		this.showEntryNumbers = showEntryNumbers;

		ImmutableList.Builder<@NotNull EntryTypes> ilb = ImmutableList.builder();
		for (EntryTypes type : types)
		{
			ilb.add(type);
		}

		this.types = ilb.build();
		this.displayString = displayString;
		ImmutableList.Builder<@NotNull TableRow> ilb2 = ImmutableList.builder();
		for (TableRow list : defaultValue)
		{
			TableRow newEntry = new TableRow();
			newEntry.list().addAll(List.copyOf(list.list()));
			ilb2.add(newEntry);
		}
		this.defaultTable = ilb2.build();
		this.table.addAll(defaultTable);
	}

	@Override
	public List<TableRow> getTable()
	{
		return this.table;
	}

	@Override
	public List<List<Object>> getRawTable()
	{
		List<List<Object>> rawTable = new ArrayList<>();
		for (TableRow entry : this.table)
		{
			List<Object> rawEntry = new ArrayList<>(entry.list());
			rawTable.add(rawEntry);
		}
		return rawTable;
	}

	@Override
	public ImmutableList<@NotNull TableRow> getDefaultTable()
	{
		return this.defaultTable;
	}

	@Override
	public ImmutableList<@NotNull List<Object>> getDefaultRawTable()
	{
		ImmutableList.Builder<@NotNull List<Object>> ilb = new ImmutableList.Builder<>();
		for (TableRow entry : this.defaultTable)
		{
			List<Object> rawEntry = new ArrayList<>(entry.list());
			ilb.add(rawEntry);
		}
		return ilb.build();
	}

	@Override
	public void setTable(List<TableRow> newTable)
	{
		if (!this.table.equals(newTable))
		{
			this.table.clear();
			this.table.addAll(newTable);
			this.onValueChanged();
		}
	}

	@Override
	public void setModified()
	{
		this.onValueChanged();
	}

	@Override
	public @Nullable String getDisplayString()
	{
		return this.displayString;
	}

	@Override
	public List<EntryTypes> getTypes()
	{
		return this.types;
	}

	@Override
	public void resetToDefault()
	{
		setTable(this.defaultTable);
	}

	@Override
	public boolean isModified()
	{
		for (int i = 0; i < this.table.size() && i < this.defaultTable.size(); i++)
		{
			if (!this.table.get(i).equals(this.defaultTable.get(i)))
			{
				return true;
			}
		}

		return false;
	}

	@Override
    public void setValueFromJsonElement(JsonElement element)
    {
        List<TableRow> oldTable = new ArrayList<>();
        List<TableRow> tempTable = new ArrayList<>();

        for (TableRow entry : this.table)
        {
            oldTable.add(new TableRow(entry.list()));
        }

        try
        {
            JsonArray arr = element.getAsJsonArray();
            List<EntryTypes> inferredEntryTypes = new ArrayList<>();

            for (int row = 0; row < arr.size(); row++)
            {
                JsonElement el = arr.get(row);
                if (!(el instanceof JsonArray jarr))
                {
                    throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; row is not a JSON array");
                }

                if (row != 0 && jarr.size() != inferredEntryTypes.size())
                {
                    throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; row length mismatch");
                }

                List<Entry> tempList = new ArrayList<>();

                for (int col = 0; col < jarr.size(); col++)
                {
                    JsonElement el2 = jarr.get(col);
                    if (!el2.isJsonObject())
                    {
                        throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; entry in row is not a json object");
                    }

                    JsonObject obj = el2.getAsJsonObject();
                    if (!obj.has("type"))
                    {
                        throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; entry missing 'type'");
                    }

                    String type = obj.get("type").getAsString();

                    if (row == 0)
                    {
                        inferredEntryTypes.add(EntryTypes.valueOf(type.toUpperCase(Locale.ROOT)));
                    }
                    else
                    {
                        EntryTypes expected = inferredEntryTypes.get(col);
                        if (!expected.name().equalsIgnoreCase(type))
                        {
                            throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; not all stored rows have the same types (mismatch at column " + col + ")");
                        }
                    }

                    switch (type)
                    {
                        case "string"  -> tempList.add(StringEntry.getFromJsonObject(obj));
                        case "integer" -> tempList.add(IntegerEntry.getFromJsonObject(obj));
                        case "double"  -> tempList.add(DoubleEntry.getFromJsonObject(obj));
                        case "boolean" -> tempList.add(BooleanEntry.getFromJsonObject(obj));
                        case "label"   -> tempList.add(LabelEntry.getFromJsonObject(obj));
                    }
                }

                tempTable.add(new TableRow(tempList));
            }

            if (!this.types.equals(inferredEntryTypes) && !inferredEntryTypes.isEmpty())
            {
                throw new IllegalArgumentException("Data for " + this.getName() + " is corrupted; types in the config don't match the stored types, expected " + this.types + " but got " + inferredEntryTypes);
            }

            this.table.clear();
            this.table.addAll(tempTable);

            if (!this.table.equals(oldTable))
            {
                onValueChanged();
            }
        }
        catch (Exception e)
        {
            // I believe this should be `error()` and not `warn()` considering the Entry classes also use error on failed parsing
            MaLiLib.LOGGER.error("Failed to set config value for '{}' from the JSON element '{}': {}", this.getName(), element, e.getMessage(), e);
        }
    }

	@Override
	public JsonElement getAsJsonElement()
	{
		JsonArray tableArr = new JsonArray();

		for (var row : this.table)
		{
			JsonArray entryArr = new JsonArray();
			for (Entry entry : row.list())
			{
				entryArr.add(entry.getAsJsonObject());
			}
			tableArr.add(entryArr);
		}

		return tableArr;
	}

	@Override
	public List<Label> getLabels()
	{
		return this.labels;
	}

	@Override
	public boolean allowNewEntry()
	{
		return this.allowNewEntry;
	}

	@Override
	public boolean showEntryNumbers()
	{
		return this.showEntryNumbers;
	}

	public static @NotNull TableRow getDummy(List<EntryTypes> types)
	{
		TableRow dummy = new TableRow();
		for (EntryTypes type : types)
		{
			if (type == EntryTypes.STRING)
			{
				dummy.add(StringEntry.of(""));
			}
			else if (type == EntryTypes.LABEL)
			{
				dummy.add(LabelEntry.of(""));
			}
			else if (type == EntryTypes.INTEGER)
			{
				dummy.add(IntegerEntry.of(0));
			}
			else if (type == EntryTypes.DOUBLE)
			{
				dummy.add(DoubleEntry.of(0.0));
			}
			else if (type == EntryTypes.BOOLEAN)
			{
				dummy.add(BooleanEntry.of(false));
//            } else if (type == EntryTypes.KEYBIND) {
//                dummy.add(KeybindEntry.of(""));
			}
			else
			{
				throw new IllegalStateException("Unsupported type: " + type.name());
			}
		}
		return dummy;
	}

	public static class Builder
	{
		private String name;
		private String comment = null;
		private String prettyName = null;
		private String translatedName = null;
		private @Nullable String displayString = null;
		private List<TableRow> defaultValue = null;
        private List<Label> labels = List.of();
		private boolean showEntryNumbers = true;
		private boolean allowAddNewEntry = true;
		private EntryTypes[] types;

		private int entryCount = -1;

		public Builder(String name, EntryTypes... types)
		{
            if (types.length == 0)
            {
                throw new IllegalStateException("There must be at least one type.");
            }
			this.name = name;
			this.types = types;
		}

		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder setComment(String comment)
		{
			this.comment = comment;
			return this;
		}

		public Builder setPrettyName(String prettyName)
		{
			this.prettyName = prettyName;
			return this;
		}

		public Builder setTranslatedName(String translatedName)
		{
			this.translatedName = translatedName;
			return this;
		}

		public Builder setDisplayString(@Nullable String displayString)
		{
			this.displayString = displayString;
			return this;
		}

		public Builder setDefaultValue(TableRow... defaultValue)
		{
			this.defaultValue = List.of(defaultValue);
			return this;
		}

		public Builder setLabels(List<Object> labels)
		{
            return setLabels(labels.toArray(new Object[]{}));
		}

		public Builder setLabels(Object... labels)
		{
            this.labels = new ArrayList<>();
            for (Object o : labels)
            {
                if (o instanceof String str)
                {
                    this.labels.add(Label.of(str, ""));
                }
                else if (o instanceof Label label)
                {
                    this.labels.add(label);
                }
                else
                {
                    throw new IllegalArgumentException("labels contains an instance of type " + o.getClass().getSimpleName() + " which is not String or Label");
                }
            }
            return this;
		}

		public Builder setShowEntryNumbers(boolean showEntryNumbers)
		{
			this.showEntryNumbers = showEntryNumbers;
			return this;
		}

		public Builder setAllowAddNewEntry(boolean allowAddNewEntry)
		{
			this.allowAddNewEntry = allowAddNewEntry;
			return this;
		}

		public Builder setTypes(EntryTypes... types)
		{
            if (types.length == 0)
            {
                throw new IllegalStateException("There must be at least one type.");
            }
			this.types = types;
			return this;
		}

		public Builder setEntryCount(@Range(from = 1, to = Integer.MAX_VALUE) int count)
		{
			this.entryCount = count;
			return this;
		}

        public ConfigTable build()
        {
            return build(false);
        }

		public ConfigTable build(boolean ignoreWarning)
		{
            // argument checking
            if (this.defaultValue != null && this.defaultValue.size() != this.entryCount && this.entryCount > 0 && this.defaultValue.size() > 1)
            {
                throw new IllegalArgumentException("Default value (" + this.defaultValue.size() + ") must have the same count as entryCount (" + this.entryCount + ")");
            }

			if (this.defaultValue == null)
			{
				this.defaultValue = new ArrayList<>();
			}
			else
			{
				this.defaultValue = new ArrayList<>(this.defaultValue);
			}
			if (this.defaultValue.size() == 1 && this.entryCount > 0)
			{
				for (int i = 0; i < this.entryCount; i++)
				{
					this.defaultValue.add(new TableRow(this.defaultValue.getFirst().list()));
				}
			}
			else if (this.entryCount > 0 && this.defaultValue.isEmpty())
			{
				for (int i = 0; i < this.entryCount; i++)
				{
					this.defaultValue.add(getDummy(List.of(this.types)));
				}
			}
			if (this.comment == null)
			{
				this.comment = this.name + " Comment?";
			}
			if (this.prettyName == null)
			{
				this.prettyName = this.name;
			}
			if (this.translatedName == null)
			{
				this.translatedName = this.name;
			}
			for (TableRow v : this.defaultValue)
			{
				for (int j = 0; j < this.types.length; j++)
				{
					if (v.list().get(j).getType() != this.types[j])
					{
						throw new IllegalArgumentException("Type mismatch: expected " + this.types[j] + " but got " + v.list().get(j).getType().name());
					}

                    if (this.allowAddNewEntry && this.types[j] == EntryTypes.LABEL && FabricLoader.getInstance().isDevelopmentEnvironment() && !ignoreWarning)
                    {
                        MaLiLib.LOGGER.warn("You probably shouldn't enable allowAddNewEntry if you are using labels.");
                    }
				}
			}

			return new ConfigTable(this.name, this.comment, this.prettyName, this.translatedName, this.displayString, this.defaultValue, this.labels, this.showEntryNumbers, this.allowAddNewEntry, this.types);
		}
	}
}

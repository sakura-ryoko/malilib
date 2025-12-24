package fi.dy.masa.malilib.config.options.table;

import fi.dy.masa.malilib.config.options.table.type.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record TableRow(List<Entry> list)
{
	public TableRow()
	{
		this(new ArrayList<>());
	}

	public TableRow(Entry... objs)
	{
		this(new ArrayList<>());
		Collections.addAll(this.list, objs);
	}

	public static TableRow of(Entry... entries)
	{
		TableRow tableRow = new TableRow();
		for (Entry entry : entries)
		{
			tableRow.add(entry);
		}
		return tableRow;
	}

	public static TableRow of(Object... entries)
	{
		TableRow tableRow = new TableRow();
		for (Object entry : entries)
		{
			switch (entry)
			{
				case String s -> tableRow.add(new StringEntry(s));
				case Integer i -> tableRow.add(new IntegerEntry(i));
				case Double v -> tableRow.add(new DoubleEntry(v));
				case Boolean b -> tableRow.add(new BooleanEntry(b));
//                case IKeybind k -> tableRow.add(new KeybindEntry(k));
                case Label l -> tableRow.add(new LabelEntry(l));
				case Entry e -> tableRow.add(e);
				default -> throw new IllegalArgumentException("Unsupported entry type: " + entry.getClass().getName());
			}
		}
		return tableRow;
	}

	public void add(Entry entry)
	{
		this.list.add(entry);
	}

	public Entry get(int index)
	{
		return this.list.get(index);
	}

	public IntegerEntry getIntEntry(int index)
	{
		return (IntegerEntry) this.list.get(index);
	}

	public DoubleEntry getDoubleEntry(int index)
	{
		return (DoubleEntry) this.list.get(index);
	}

	public BooleanEntry getBooleanEntry(int index)
	{
		return (BooleanEntry) this.list.get(index);
	}

//    public KeybindEntry getKeybindEntry(int index) {
//        return (KeybindEntry) this.list.get(index);
//    }

	public StringEntry getStringEntry(int index)
	{
		return (StringEntry) this.list.get(index);
	}

	public LabelEntry getLabelEntry(int index)
	{
		return (LabelEntry) this.list.get(index);
	}

	public Integer getInt(int index)
	{
		return ((IntegerEntry) this.list.get(index)).getValue();
	}

	public Double getDouble(int index)
	{
		return ((DoubleEntry) this.list.get(index)).getValue();
	}

	public Boolean getBoolean(int index)
	{
		return ((BooleanEntry) this.list.get(index)).getValue();
	}

	public String getString(int index)
	{
		return ((StringEntry) this.list.get(index)).getValue();
	}
	public Label getLabel(int index)
	{
		return ((LabelEntry) this.list.get(index)).getValue();
	}

//    public IKeybind getKeybind(int index) {
//        return ((KeybindEntry) this.list.get(index)).getKeybind();
//    }

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof TableRow(List<Entry> list1)))
		{
			return false;
		}
		if (this.list.size() != list1.size())
		{
			return false;
		}
		for (int i = 0; i < this.list.size(); i++)
		{
			if (!this.list.get(i).equals(list1.get(i)))
			{
				return false;
			}
		}
		return true;
	}
}

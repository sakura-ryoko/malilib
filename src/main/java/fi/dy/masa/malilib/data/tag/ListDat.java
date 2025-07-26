package fi.dy.masa.malilib.data.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import com.google.common.collect.Iterables;

import fi.dy.masa.malilib.MaLiLib;

public class ListDat<E>
{
    private final List<Dat<E>> valueList;
    private final Class<E> type;
    private final Dat.Type datType;

    public ListDat(Class<E> type, List<E> list)
    {
        this.valueList = new ArrayList<>();
        this.type = type;
        this.datType = Dat.Type.getType(type);
        list.forEach(
                entry ->
                        this.valueList.add(new Dat<>(type, entry))
        );
    }

    public List<Dat<E>> getValueList() { return this.valueList; }

    public Class<E> getType() { return this.type; }

    public Dat.Type getDatType() { return this.datType; }

    public @Nullable E get(int index)
    {
        if (index >= this.valueList.size())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting list index [{}]; Out Of Bounds", index);
            return null;
        }

        return this.valueList.get(index).getValue();

    }

    public @Nullable E set(int index, E newValue)
    {
        if (index >= this.valueList.size())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting setting list index [{}]; Out Of Bounds", index);
            return null;
        }

        try
        {
            return this.valueList.set(index, new Dat<>(this.type, newValue)).getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting setting list index [{}]; {}", index, err.getLocalizedMessage());
            return null;
        }
    }

    public boolean add(E newValue)
    {
        try
        {
            return this.valueList.add(new Dat<>(this.type, newValue));
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting adding to list; {}", err.getLocalizedMessage());
            return false;
        }
    }

    public boolean isEmpty() { return this.valueList.isEmpty(); }

    public int size() { return this.valueList.size(); }

    public void clear() { this.valueList.clear(); }

    public List<E> toList()
    {
        List<E> list = new ArrayList<>();

        this.valueList.forEach(
                entry ->
                        list.add(entry.getValue())
        );

        return list;
    }

    @SuppressWarnings("unchecked")
    public <F> @Nullable ListDat<E> copyFrom(ListDat<F> otherList)
    {
        if (this.type != otherList.getType() || this.datType != otherList.getDatType())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting copying list from other list; Type Mismatch [{} != {}]", this.type.getName(), otherList.type.getName());
            return null;
        }

        otherList.valueList.forEach(
                entry ->
                        this.valueList.add(new Dat<>(this.type, (E) entry.getValue()))
        );

        return this;
    }

    public Stream<Dat<E>> stream() { return this.valueList.stream(); }

    public Stream<E> streamValues()
    {
        return this.toList().stream();
    }

    public Iterable<Dat<E>> iterator()
    {
        return Iterables.concat(this.valueList);
    }
    public Iterable<E> iteratorValues()
    {
        return Iterables.concat(this.toList());
    }
}

package fi.dy.masa.malilib.data.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import com.google.common.collect.Iterables;

import fi.dy.masa.malilib.MaLiLib;

public class ListDat<E> implements IDat<List<Dat<E>>>
{
//    public static final Codec<ListDat<?>> CODEC = Codec.of(
//            new Encoder<>()
//            {
//                @Override
//                public <T> DataResult<T> encode(ListDat<?> input, DynamicOps<T> ops, T prefix)
//                {
//                    return null;
//                }
//            },
//            new Decoder<>()
//            {
//                @Override
//                public <T> DataResult<Pair<ListDat<?>, T>> decode(DynamicOps<T> ops, T input)
//                {
//                    return null;
//                }
//            }
//        );

    private final List<Dat<E>> valueList;
    private final Class<E> type;
    private final Dat.Type datType;

    public ListDat(Class<E> type, List<E> list)
    {
        this.valueList = new ArrayList<>();
        this.type = type;
        this.datType = Dat.Type.LIST;

        if (!list.isEmpty())
        {
            list.forEach(
                    entry ->
                            this.valueList.add(new Dat<>(type, this.datType, entry))
            );
        }
    }

    @Override
    public Class<List<Dat<E>>> getType()
    {
        // Not useful
        return null;
    }

    public Class<E> getListType()
    {
        return this.type;
    }

    @Override
    public List<Dat<E>> getValue()
    {
        return this.valueList;
    }

    @Override
    public void setValue(List<Dat<E>> newValue)
    {
        this.clear();
        this.valueList.addAll(newValue);
    }

    public Dat.Type getDatType() { return this.datType; }

    public @Nullable E get(int index)
    {
        if (index >= this.valueList.size())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting list index [{}]; Out Of Bounds", index);
            return null;
        }

        try
        {
            return this.valueList.get(index).getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting list index [{}]; {}", index, err.getLocalizedMessage());
            return null;
        }
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
            return this.valueList.set(index, new Dat<>(this.type, this.datType, newValue)).getValue();
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
            return this.valueList.add(new Dat<>(this.type, this.datType, newValue));
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting adding to list; {}", err.getLocalizedMessage());
            return false;
        }
    }

    public @Nullable E getFirst()
    {
        if (this.valueList.isEmpty())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting first index; Out Of Bounds (Empty)");
            return null;
        }

        try
        {
            return this.valueList.getFirst().getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting first index; {}", err.getLocalizedMessage());
            return null;
        }
    }

    public @Nullable E getLast()
    {
        if (this.valueList.isEmpty())
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting last index; Out Of Bounds (Empty)");
            return null;
        }

        try
        {
            return this.valueList.getLast().getValue();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("ListDat: Excepting getting last index; {}", err.getLocalizedMessage());
            return null;
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
                        this.valueList.add(new Dat<>(this.type, this.datType, (E) entry.getValue()))
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

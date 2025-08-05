package malilib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

public class ListUtils
{
    /**
     * Filters entries from the first list by the given Predicate and adds them to the second list.
     * If <b>removeMatched</b> is true, then the matched entries are also removed from the first list.
     * <br><br>
     * Note: If removeMatched is true, then the first list will be cleared and re-filled!
     */
    public static <T> void extractEntriesToSecondList(List<T> listFrom,
                                                      List<T> listTo,
                                                      Predicate<T> filter,
                                                      boolean removeMatched)
    {
        ArrayList<T> newList = new ArrayList<>();

        for (T entry : listFrom)
        {
            boolean matches = filter.test(entry);

            if (matches)
            {
                listTo.add(entry);
            }
            else if (removeMatched)
            {
                newList.add(entry);
            }
        }

        if (removeMatched)
        {
            listFrom.clear();
            listFrom.addAll(newList);
        }
    }

    /**
     * Returns either the next or the previous entry in the list, depending on the reverse argument.
     */
    public static <T> T getNextEntry(List<T> list, T currentValue, boolean reverse)
    {
        return getNextEntry(list, currentValue, reverse, (v) -> true);
    }

    /**
     * Returns either the next or the previous entry in the list, depending on the reverse argument,
     * that passes the test in predicate.
     */
    public static <T> T getNextEntry(List<T> list, T currentValue, boolean reverse, Predicate<T> predicate)
    {
        final int size = list.size();

        if (size > 1)
        {
            int newIndex = list.indexOf(currentValue);

            if (newIndex != -1)
            {
                final int maxIndex = size - 1;
                final int increment = (reverse ? -1 : 1);

                for (int i = 0; i < size; ++i)
                {
                    newIndex += increment;

                    if (newIndex >= size)
                    {
                        newIndex = 0;
                    }
                    else if (newIndex < 0)
                    {
                        newIndex = maxIndex;
                    }

                    T tmp = list.get(newIndex);

                    if (predicate.test(tmp))
                    {
                        return tmp;
                    }
                }
            }
        }

        return currentValue;
    }

    public static <T> ImmutableList<T> getAppendedList(Collection<? extends T> originalList, Collection<? extends T> additionalEntries)
    {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        builder.addAll(originalList);
        builder.addAll(additionalEntries);
        return builder.build();
    }

    public static <T> ImmutableList<T> getAppendedList(Collection<? extends T> originalList, T newEntry)
    {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        builder.addAll(originalList);
        builder.add(newEntry);
        return builder.build();
    }

    public static <T> ImmutableList<T> getPrependedList(T newEntry, Collection<? extends T> originalList)
    {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        builder.add(newEntry);
        builder.addAll(originalList);
        return builder.build();
    }

    /**
     * Returns a new list where the value oldValue is replaced by the value newValue.
     * If replaceInPlace is true, then the value is replaced in the same list position.
     * If replaceInPlace is false, then the old value is omitted, and the new value is added
     * at the end of the list.
     * If the old value was not found at all, then the new value is appended at the end of the list.
     */
    public static <T> ImmutableList<T> replaceOrAddValue(Collection<? extends T> originalList,
                                                         T oldValue, T newValue, boolean replaceInPlace)
    {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        boolean replaced = false;

        for (T value : originalList)
        {
            if (value.equals(oldValue) == false)
            {
                builder.add(value);
            }
            else if (replaceInPlace)
            {
                builder.add(newValue);
                replaced = true;
            }
        }

        if (replaceInPlace == false || replaced == false)
        {
            builder.add(newValue);
        }

        return builder.build();
    }

    public static <T> boolean containsAny(Collection<T> hayStack, Collection<T> needles)
    {
        for (T needle : needles)
        {
            if (hayStack.contains(needle))
            {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static <T> T getFirstEntry(Collection<T> collection)
    {
        for (T entry : collection)
        {
            return entry;
        }

        return null;
    }
}

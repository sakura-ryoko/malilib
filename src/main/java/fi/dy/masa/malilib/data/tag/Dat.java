package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public class Dat<T> implements IDat<T>
{
    protected T value;
    private final Class<T> type;
    private final Type datType;

    protected Dat(Class<T> type, Type datType, T value)
    {
        this.type = type;
        this.value = value;
        this.datType = datType;
    }

    @Override
    public Class<T> getType() { return this.type; }

    @Override
    public T getValue() { return this.value; }

    @Override
    public void setValue(T newValue) { this.value = newValue; }

    @Override
    public Type getDatType() { return this.datType; }

    public enum Type
    {
        BOOL,
        INT,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
        FRACTION,
        LIST
    }
}

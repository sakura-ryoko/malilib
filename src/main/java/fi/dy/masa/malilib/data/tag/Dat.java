package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.log.AnsiLogger;

@ApiStatus.NonExtendable
public class Dat<T>
{
    private static final AnsiLogger LOGGER = new AnsiLogger(Dat.class, true, true);
    protected T value;
    private final Class<T> type;
    private final Type datType;

    protected Dat(Class<T> type, T value)
    {
        this.type = type;
        this.value = value;
        this.datType = Type.getType(type);
    }

    public Class<T> getType() { return this.type; }

    public String getTypeName() { return this.type.getName(); }

    public T getValue() { return this.value; }

    public void setValue(T newValue) { this.value = newValue; }

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
        OTHER,
        ;

        public static Type getType(Class<?> type)
        {
            final String name = type.getName();
            LOGGER.debug("getType(): name: [{}]", name);

            switch (name)
            {
                case "Boolean" -> { return Type.BOOL; }
                case "Integer" -> { return Type.INT; }
                case "Short" -> { return Type.SHORT; }
                case "Long" -> { return Type.LONG; }
                case "Float" -> { return Type.FLOAT; }
                case "Double" -> { return Type.DOUBLE; }
                case "Fraction" -> { return Type.FRACTION; }
                default -> { return Type.OTHER; }
            }
        }
    }
}

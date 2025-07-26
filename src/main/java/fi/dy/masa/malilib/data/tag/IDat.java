package fi.dy.masa.malilib.data.tag;

public interface IDat<T>
{
    Class<T> getType();
//    Codec<T> codec();
    T getValue();
    void setValue(T newValue);
    Dat.Type getDatType();
}

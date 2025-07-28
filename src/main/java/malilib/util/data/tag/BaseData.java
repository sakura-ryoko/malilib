package malilib.util.data.tag;

public abstract class BaseData
{
    protected final int type;
    protected final String displayName;

    protected BaseData(int type, String displayName)
    {
        this.type = type;
        this.displayName = displayName;
    }

    public int getType()
    {
        return this.type;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public abstract BaseData copy();
}

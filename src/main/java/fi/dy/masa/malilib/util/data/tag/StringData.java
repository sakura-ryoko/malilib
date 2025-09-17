package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class StringData extends BaseData
{
    public static final String TAG_NAME = "TAG_String";

    public final String value;

    public StringData(String value)
    {
        super(Constants.NBT.TAG_STRING, TAG_NAME);

        this.value = value;
    }

    public String getString()
    {
        return this.value;
    }

    @Override
    public StringData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return quoteAndEscape(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeUTF(this.value);
    }

    public static StringData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        String str = input.readUTF();
        sizeTracker.increment(str.length() + 2);
        return new StringData(str);
    }

    public static String quoteAndEscape(String str)
    {
        StringBuilder sb = new StringBuilder("\"");

        for(int i = 0; i < str.length(); ++i)
        {
            char c = str.charAt(i);

            if (c == '\\' || c == '"')
            {
                sb.append('\\');
            }

            sb.append(c);
        }

        return sb.append('"').toString();
    }
}

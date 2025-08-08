package malilib.util;

import io.netty.buffer.ByteBuf;

public class ByteBufUtils
{
    public static int getVarIntSize(int value)
    {
        // The VarInt has 7 useful bits per byte

        if (value <= 127)		// 7 bits
            return 1;

        if (value <= 16383)		// 14 bits
            return 2;

        if (value <= 2097151)	// 21 bits
            return 3;

        if (value <= 268435455)	// 28 bits
            return 4;

        /*
        for (int i = 1; i < 5; ++i)
        {
            if ((value & (0xFFFFFFFF << (i * 7))) == 0)
            {
                return i;
            }
        }
        */

        return 5;
    }

    public static int readVarInt(ByteBuf buf)
    {
        int value = 0;
        int valueCount = 0;

        byte b;

        do {
            b = buf.readByte();
            value |= (b & 0x7F) << (valueCount * 7);
            valueCount++;

            if (valueCount > 5)  // Too large
            {
                return value;
            }
        } while((b & 0x80) != 0);

        return value;
    }

    public static void writeVarInt(ByteBuf buf, int value)
    {
        while ((value & 0xFFFFFF80) != 0)
        {
            buf.writeByte(0x80 | (value & 0x7F));
            value >>>= 7;
        }

        buf.writeByte(value);
    }
}

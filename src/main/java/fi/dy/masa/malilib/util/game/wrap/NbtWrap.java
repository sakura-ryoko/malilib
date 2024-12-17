package fi.dy.masa.malilib.util.game.wrap;

import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.nbt.*;

import fi.dy.masa.malilib.util.Constants;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class NbtWrap
{
    public static boolean containsByte(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE);
    }

    public static boolean containsShort(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_SHORT);
    }

    public static boolean containsInt(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT);
    }

    public static boolean containsLong(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG);
    }

    public static boolean containsFloat(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean containsDouble(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean containsString(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_STRING);
    }

    public static boolean containsCompound(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean containsList(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LIST);
    }

    public static boolean containsByteArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean containsIntArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean containsLongArray(NbtCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG_ARRAY);
    }

    public static boolean contains(NbtCompound tag, String name, int typeId)
    {
        return tag.contains(name, typeId);
    }

    public static boolean hasUUID(NbtCompound tag)
    {
        return hasUUID(tag, "UUIDM", "UUIDL");
    }

    public static boolean hasUUID(NbtCompound tag, String keyM, String keyL)
    {
        return containsLong(tag, keyM) && containsLong(tag, keyL);
    }

    public static boolean getBoolean(NbtCompound tag, String name)
    {
        return tag.getBoolean(name);
    }

    public static byte getByte(NbtCompound tag, String name)
    {
        return tag.getByte(name);
    }

    public static short getShort(NbtCompound tag, String name)
    {
        return tag.getShort(name);
    }

    public static int getInt(NbtCompound tag, String name)
    {
        return tag.getInt(name);
    }

    public static long getLong(NbtCompound tag, String name)
    {
        return tag.getLong(name);
    }

    public static float getFloat(NbtCompound tag, String name)
    {
        return tag.getFloat(name);
    }

    public static double getDouble(NbtCompound tag, String name)
    {
        return tag.getDouble(name);
    }

    public static String getString(NbtCompound tag, String name)
    {
        return tag.getString(name);
    }

    public static NbtCompound getCompound(NbtCompound tag, String name)
    {
        return tag.getCompound(name);
    }

    public static NbtList getList(NbtCompound tag, String name, int type)
    {
        return tag.getList(name, type);
    }

    public static byte[] getByteArray(NbtCompound tag, String name)
    {
        return tag.getByteArray(name);
    }

    public static int[] getIntArray(NbtCompound tag, String name)
    {
        return tag.getIntArray(name);
    }

    public static NbtElement getTag(NbtCompound tag, String name)
    {
        return tag.get(name);
    }

    public static boolean getBooleanOrDefault(NbtCompound tag, String name, boolean defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) != 0 : defaultValue;
    }

    public static byte getByteOrDefault(NbtCompound tag, String name, byte defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) : defaultValue;
    }

    public static short getShortOrDefault(NbtCompound tag, String name, short defaultValue)
    {
        return containsShort(tag, name) ? getShort(tag, name) : defaultValue;
    }

    public static int getIntOrDefault(NbtCompound tag, String name, int defaultValue)
    {
        return containsInt(tag, name) ? getInt(tag, name) : defaultValue;
    }

    public static long getLongOrDefault(NbtCompound tag, String name, long defaultValue)
    {
        return containsLong(tag, name) ? getLong(tag, name) : defaultValue;
    }

    public static float getFloatOrDefault(NbtCompound tag, String name, float defaultValue)
    {
        return containsFloat(tag, name) ? getFloat(tag, name) : defaultValue;
    }

    public static double getDoubleOrDefault(NbtCompound tag, String name, double defaultValue)
    {
        return containsDouble(tag, name) ? getDouble(tag, name) : defaultValue;
    }

    public static String getStringOrDefault(NbtCompound tag, String name, String defaultValue)
    {
        return containsString(tag, name) ? getString(tag, name) : defaultValue;
    }

    public static String getCommandFeedbackName(NbtElement tag)
    {
        return tag.getNbtType().toString();
    }

    public static int getTypeId(NbtElement tag)
    {
        return tag.getType();
    }

    public static NbtByte asByteTag(byte value)
    {
        return NbtByte.of(value);
    }

    public static NbtShort asShortTag(short value)
    {
        return NbtShort.of(value);
    }

    public static NbtInt asIntTag(int value)
    {
        return NbtInt.of(value);
    }

    public static NbtLong asLongTag(long value)
    {
        return NbtLong.of(value);
    }

    public static NbtFloat asFloatTag(float value)
    {
        return NbtFloat.of(value);
    }

    public static NbtDouble asDoubleTag(double value)
    {
        return NbtDouble.of(value);
    }

    public static NbtString asStringTag(String value)
    {
        return NbtString.of(value);
    }

    public static void putBoolean(NbtCompound tag, String name, boolean value)
    {
        tag.putBoolean(name, value);
    }

    public static void putByte(NbtCompound tag, String name, byte value)
    {
        tag.putByte(name, value);
    }

    public static void putShort(NbtCompound tag, String name, short value)
    {
        tag.putShort(name, value);
    }

    public static void putInt(NbtCompound tag, String name, int value)
    {
        tag.putInt(name, value);
    }

    public static void putLong(NbtCompound tag, String name, long value)
    {
        tag.putLong(name, value);
    }

    public static void putFloat(NbtCompound tag, String name, float value)
    {
        tag.putFloat(name, value);
    }

    public static void putDouble(NbtCompound tag, String name, double value)
    {
        tag.putDouble(name, value);
    }

    public static void putString(NbtCompound tag, String name, String value)
    {
        tag.putString(name, value);
    }

    public static void putTag(NbtCompound tag, String name, NbtElement value)
    {
        tag.put(name, value);
    }

    public static void putByteArray(NbtCompound tag, String name, byte[] value)
    {
        tag.putByteArray(name, value);
    }

    public static void putIntArray(NbtCompound tag, String name, int[] value)
    {
        tag.putIntArray(name, value);
    }

    public static void remove(NbtCompound tag, String name)
    {
        tag.remove(name);
    }

    public static void addTag(NbtList listTag, NbtElement value)
    {
        listTag.add(value);
    }

    public static Set<String> getKeys(NbtCompound tag)
    {
        return tag.getKeys();
    }

    public static int getListSize(NbtList list)
    {
        return list.size();
    }

    public static int getListStoredType(NbtList listTag)
    {
        return listTag.getHeldType();
    }

    public static NbtList getListOfCompounds(NbtCompound tag, String name)
    {
        return getList(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static double getDoubleAt(NbtList listTag, int index)
    {
        return listTag.getDouble(index);
    }

    public static int getIntAt(NbtList listTag, int index)
    {
        return listTag.getInt(index);
    }

    public static NbtCompound getCompoundAt(NbtList listTag, int index)
    {
        return listTag.getCompound(index);
    }

    public static NbtCompound copy(NbtCompound tag)
    {
        return tag.copy();//copy();
    }

    public static NbtList copy(NbtList tag)
    {
        return tag.copy();//copy();
    }
}

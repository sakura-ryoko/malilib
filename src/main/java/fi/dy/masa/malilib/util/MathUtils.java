package fi.dy.masa.malilib.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Post-ReWrite code
 */
public class MathUtils
{
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

    /**
     * @return The average value of the elements in the given array
     */
    public static double average(int[] values)
    {
        final int size = values.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (int value : values)
        {
            sum += value;
        }

        return (double) sum / (double) size;
    }

    /**
     * @return The average value of the elements in the given array
     */
    public static double average(long[] values)
    {
        final int size = values.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (long value : values)
        {
            sum += value;
        }

        return (double) sum / (double) size;
    }

    /**
     * @return The average value of the elements in the given array
     */
    public static double average(double[] values)
    {
        final int size = values.length;

        if (size == 0)
        {
            return 0;
        }

        double sum = 0;

        for (double value : values)
        {
            sum += value;
        }

        return sum / (double) size;
    }

    public static int clamp(int value, int min, int max)
    {
        if (value < min)
        {
            return min;
        }
        else
        {
            return Math.min(value, max);
        }
    }

    public static long clamp(long value, long min, long max)
    {
        if (value < min)
        {
            return min;
        }
        else
        {
            return Math.min(value, max);
        }
    }

    public static float clamp(float value, float min, float max)
    {
        if (value < min)
        {
            return min;
        }
        else
        {
            return Math.min(value, max);
        }
    }

    public static double clamp(double value, double min, double max)
    {
        if (value < min)
        {
            return min;
        }
        else
        {
            return Math.min(value, max);
        }
    }

    public static int floor(float value)
    {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    public static int floor(double value)
    {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    public static float round(float value, int decimalPlaces)
    {
        if (decimalPlaces < 0 || decimalPlaces > 9)
        {
            decimalPlaces = 0;
        }

        float fixedDec = value;
        double scale = Math.pow(10.0, decimalPlaces);

        fixedDec *= (float) scale;
        fixedDec = Math.round(fixedDec);

        return (fixedDec / (float) scale);
    }

    public static double round(double value, int decimalPlaces)
    {
        if (decimalPlaces < 0 || decimalPlaces > 9)
        {
            decimalPlaces = 0;
        }

        int scale = (int) Math.pow(10, decimalPlaces);
        double scaledUp = value * scale;
        double dec = scaledUp % 1d;
        double fixedDec = Math.round(dec * 10) / 10.;
        double newValue = scaledUp + fixedDec;

        return (double) Math.round(newValue) / scale;
    }

    public static int roundDown(int value, int interval)
    {
        if (interval == 0 || value == 0)
        {
            return 0;
        }
        else
        {
            if (value < 0)
            {
                interval *= -1;
            }

            int remainder = value % interval;

            return remainder == 0 ? value : value - remainder;
        }
    }

    public static double roundDown(double value, double interval)
    {
        if (interval == 0.0 || value == 0.0)
        {
            return 0.0;
        }
        else
        {
            if (value < 0.0)
            {
                interval *= -1.0;
            }

            double remainder = value % interval;

            return remainder == 0.0 ? value : value - remainder;
        }
    }

    public static int roundUp(int value, int interval)
    {
        if (interval == 0)
        {
            return 0;
        }
        else if (value == 0)
        {
            return interval;
        }
        else
        {
            if (value < 0)
            {
                interval *= -1;
            }

            int remainder = value % interval;

            return remainder == 0 ? value : value + interval - remainder;
        }
    }

    public static double roundUp(double value, double interval)
    {
        if (interval == 0.0)
        {
            return 0.0;
        }
        else if (value == 0.0)
        {
            return interval;
        }
        else
        {
            if (value < 0.0)
            {
                interval *= -1.0;
            }

            double remainder = value % interval;

            return remainder == 0.0 ? value : value + interval - remainder;
        }
    }

    public static long roundUp(long number, long interval)
    {
        if (interval == 0)
        {
            return 0;
        }
        else if (number == 0)
        {
            return interval;
        }
        else
        {
            if (number < 0)
            {
                interval *= -1;
            }

            long i = number % interval;
            return i == 0 ? number : number + interval - i;
        }
    }

    public static float sqrtf(double value)
    {
        return (float) Math.sqrt(value);
    }

    /**
     * Wraps/normalizes the given angle to the range 0 ... 2 * Pi
     */
    public static double wrapRadianAngle(double angle)
    {
        double twoPi = 2 * Math.PI;
        angle %= twoPi;

        if (angle < 0)
        {
            angle += twoPi;
        }

        return angle;
    }

    public static double distanceFromPointToLine(double pointX, double pointY,
                                                 double line1X, double line1Y,
                                                 double line2X, double line2Y)
    {
        // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        double num = Math.abs((line2X - line1X) * (line1Y - pointY) - (line1X - pointX) * (line2Y - line1Y));
        double diffX = line2X - line1X;
        double diffY = line2Y - line1Y;
        double den = Math.sqrt(diffX * diffX + diffY * diffY);

        return num / den;
    }

    /**
     * @return The minimum value from the given array
     */
    public static int getMinValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * @return The maximum value from the given array
     */
    public static int getMaxValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }

    /**
     * @return The minimum value from the given array
     */
    public static long getMinValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * @return The maximum value from the given array
     */
    public static long getMaxValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }

    public static float positiveModulo(float numerator, float denominator)
    {
        return (numerator % denominator + denominator) % denominator;
    }

    public static double positiveModulo(double numerator, double denominator)
    {
        return (numerator % denominator + denominator) % denominator;
    }

    /**
     * Adjust the angle so that its value is in range [-180;180[
     */
    public static float wrapDegrees(float value)
    {
        value %= 360.0f;

        if (value >= 180.0f)
        {
            value -= 360.0f;
        }

        if (value < -180.0f)
        {
            value += 360.0f;
        }

        return value;
    }

    /**
     * Adjust the angle so that its value is in range [-180;180[
     */
    public static double wrapDegrees(double value)
    {
        value %= 360.0;

        if (value >= 180.0)
        {
            value -= 360.0;
        }

        if (value < -180.0)
        {
            value += 360.0;
        }

        return value;
    }

    /**
     * Adjust the angle so that its value is in range [-180;180[
     */
    public static int wrapDegrees(int angle)
    {
        angle %= 360;

        if (angle >= 180)
        {
            angle -= 360;
        }

        if (angle < -180)
        {
            angle += 360;
        }

        return angle;
    }

    public static Vec3d getRotationVector(float yaw, float pitch)
    {
        double f = Math.cos(-yaw * (Math.PI / 180.0) - Math.PI);
        double g = Math.sin(-yaw * (Math.PI / 180.0) - Math.PI);
        double h = -Math.cos(-pitch * (Math.PI / 180.0));
        double i = Math.sin(-pitch * (Math.PI / 180.0));

        return new Vec3d(g * h, i, f * h);
    }

    public static long getCoordinateRandom(int x, int y, int z)
    {
        long l = (long) (x * 3129871L) ^ (long) z * 116129781L ^ (long) y;
        return l * l * 42317861L + l * 11L;
    }

    public static long getPositionRandom(Vec3i pos)
    {
        return getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
    }

    public static int smallestEncompassingPowerOfTwo(int value)
    {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    /**
     * "Is the given value a power of two?  (1, 2, 4, 8, 16, ...)"
     */
    private static boolean isPowerOfTwo(int value)
    {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * "Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two
     * of the given value. Optimized for cases where the input value is a power-of-two.
     * If the input value is not a power-of-two, then subtract 1 from the return value."
     */
    public static int log2DeBruijn(int value)
    {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) value * 125613361L >> 27) & 31];
    }

    /**
     * "Efficiently calculates the floor of the base-2 log of an integer value.
     * This is effectively the index of the highest bit that is set.
     * For example, if the number in binary is 0...100101, this will return 5."
     */
    public static int log2(int value)
    {
        if (isPowerOfTwo(value))
        {
            return log2DeBruijn(value);
        }

        return log2DeBruijn(value) - 1;
    }

    // TODO until util.position.Vec3d gets added (RayTraceUtils)
    public static Vec3d scale(Vec3d vec, double factor)
    {
        return new Vec3d(vec.x * factor, vec.y * factor, vec.z * factor);
    }
}

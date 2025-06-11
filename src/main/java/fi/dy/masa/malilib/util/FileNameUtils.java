package fi.dy.masa.malilib.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Post-ReWrite code
 */
public class FileNameUtils
{
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    public static final ImmutableSet<Character> ILLEGAL_CHARACTERS = ImmutableSet.of('/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' );
    public static final String REGEX_SANITIZE = "[\\\\/:*?\"<>|]|\\p{C}|\\p{M}";

    /**
     * Checks if the given filename contains characters or strings that would be invalid in file names.
     * Most of these are just invalid on Windows...
     */
    public static boolean doesFileNameContainIllegalCharacters(String filename)
    {
        for (int i = 0; i < filename.length(); ++i)
        {
            char c = filename.charAt(i);

            if (ILLEGAL_CHARACTERS.contains(c))
            {
                return true;
            }
        }

        return filename.contains("COM") || filename.contains("PRN");
    }

    public static String getDateTimeString()
    {
        return DATE_TIME_FORMAT.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @return The file name extension (if any) after the last dot.
     *         If there are no dots, or there are no characters before or after the last dot,
     *         then an empty string is returned.
     */
    public static String getFileNameExtension(String name)
    {
        int i = name.lastIndexOf(".");
        return i > 0 && name.length() > i + 1 ? name.substring(i + 1) : "";
    }

    /**
     * @return The file name without the extension and the dot (if any).
     *         The last dot and anything after it is removed.
     */
    public static String getFileNameWithoutExtension(String name)
    {
        int i = name.lastIndexOf(".");
        return i > 0 ? name.substring(0, i) : name;
    }

    /**
     * Adds {@param extension} to the end of {@param name},
     * if {@param name} doesn't already end with {@param extension}.
     * Note that dot is not added, it should be part of {@param extension}.
     */
    public static String addExtensionIfNotExists(String name, String extension)
    {
        return name.endsWith(extension) ? name : name + extension;
    }

    public static String generateSimpleSafeFileName(String name)
    {
        boolean endsInUnderscore = name.length() >= 1 && name.charAt(name.length() - 1) == '_';
        String baseName = getFileNameWithoutExtension(name);
        String extension = getFileNameExtension(name);
        baseName = baseName.toLowerCase(Locale.US).replaceAll("[^a-z0-9_.-]", "_").replaceAll("__", "_");
        name = baseName;

        if (StringUtils.isBlank(extension) == false)
        {
            extension = extension.toLowerCase(Locale.US).replaceAll("[^a-z0-9_.-]", "_").replaceAll("__", "_");
            name += "." + extension;
        }

        // Remove the generated trailing underscore, if any
        if (endsInUnderscore == false && name.length() >= 2 && name.charAt(name.length() - 1) == '_')
        {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    public static String generateSimpleUnicodeSafeFileName(String fileIn)
    {
        return fileIn.replaceAll(REGEX_SANITIZE, "");
    }

    public static String generateSafeFileName(String name)
    {
        StringBuilder sb = new StringBuilder(name.length());

        for (int i = 0; i < name.length(); ++i)
        {
            char c = name.charAt(i);

            if (ILLEGAL_CHARACTERS.contains(c) == false)
            {
                sb.append(c);
            }
        }

        // Some weird reserved windows keywords apparently... FFS >_>
        return sb.toString().replaceAll("COM", "").replaceAll("PRN", "");
    }
}

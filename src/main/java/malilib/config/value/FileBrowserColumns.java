package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class FileBrowserColumns extends BaseOptionConfigValue
{
    public static final FileBrowserColumns NONE       = new FileBrowserColumns("none",       "malilib.name.file_browser_columns.none");
    public static final FileBrowserColumns SIZE       = new FileBrowserColumns("size",       "malilib.name.file_browser_columns.size");
    public static final FileBrowserColumns MTIME      = new FileBrowserColumns("mtime",      "malilib.name.file_browser_columns.mtime");
    public static final FileBrowserColumns SIZE_MTIME = new FileBrowserColumns("size_mtime", "malilib.name.file_browser_columns.size_mtime");

    public static final ImmutableList<FileBrowserColumns> VALUES = ImmutableList.of(NONE, SIZE, MTIME, SIZE_MTIME);

    private FileBrowserColumns(String name, String translationKey)
    {
        super(name, translationKey);
    }
}

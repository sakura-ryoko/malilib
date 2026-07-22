package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class FileWriteType extends BaseOptionConfigValue
{
    public static final FileWriteType NORMAL_WRITE    = new FileWriteType("normal_write",    "malilib.name.file_write_type.normal_write");
    public static final FileWriteType TEMP_AND_RENAME = new FileWriteType("temp_and_rename", "malilib.name.file_write_type.temp_and_rename");

    public static final ImmutableList<FileWriteType> VALUES = ImmutableList.of(NORMAL_WRITE, TEMP_AND_RENAME);

    private FileWriteType(String name, String translationKey)
    {
        super(name, translationKey);
    }
}

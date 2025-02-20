package fi.dy.masa.malilib.hotkeys;

import java.util.List;

import fi.dy.masa.malilib.util.StringUtils;

public class KeybindCategory implements Comparable<KeybindCategory>
{
    // todo how do you make a CODEC of a generic list type, as an interface? (IHotkey)
    /*
    public static final Codec<KeybindCategory> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.STRING.fieldOf("modName").forGetter(get -> get.modName),
                    PrimitiveCodec.STRING.fieldOf("categoryName").forGetter(get -> get.categoryName)
            ).apply(inst, KeybindCategory::new)
    );
    private static final Codec<List<? extends IHotkey>> LIST_CODEC = IHotk
     */
    private final String modName;
    private final String categoryName;
    private final List<? extends IHotkey> hotkeys;

    public KeybindCategory(String modName, String categoryName, List<? extends IHotkey> hotkeys)
    {
        this.modName = modName;
        this.categoryName = categoryName;
        this.hotkeys = hotkeys;
    }

    public String getModName()
    {
        return this.modName;
    }

    public String getCategory()
    {
        return StringUtils.translate(this.categoryName);
    }

    public List<? extends IHotkey> getHotkeys()
    {
        return this.hotkeys;
    }

    @Override
    public int compareTo(KeybindCategory other)
    {
        int val = this.modName.compareTo(other.modName);

        if (val != 0)
        {
            return val;
        }

        return this.categoryName.compareTo(other.categoryName);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeybindCategory other = (KeybindCategory) obj;
        if (categoryName == null)
        {
            if (other.categoryName != null)
                return false;
        }
        else if (!categoryName.equals(other.categoryName))
            return false;
        if (modName == null)
        {
            if (other.modName != null)
                return false;
        }
        else if (!modName.equals(other.modName))
            return false;
        return true;
    }
}

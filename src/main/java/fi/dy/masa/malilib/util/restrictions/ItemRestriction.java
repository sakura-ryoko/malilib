package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemRestriction extends UsageRestriction<Item>
{
    @Override
    protected void setValuesForList(Set<Item> set, List<String> names)
    {
        for (String name : names)
        {
            Identifier rl = null;

            try
            {
                rl = Identifier.tryParse(name);
            }
            catch (Exception ignore) {}

            //Item item = rl != null ? Registries.ITEM.get(rl) : null;
            Optional<RegistryEntry.Reference<Item>> opt = Registries.ITEM.getEntry(rl);

            if (opt.isPresent())
            {
                set.add(opt.get().value());
            }
            else
            {
                MaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_item_blacklist_entry", name));
            }
        }
    }
}

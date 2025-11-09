package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemRestriction extends UsageRestriction<Item>
{
    @Override
    protected void setValuesForList(Set<Item> set, List<String> names)
    {
        for (String name : names)
        {
            ResourceLocation rl = null;

            try
            {
                rl = ResourceLocation.tryParse(name);
            }
            catch (Exception ignore) {}

			if (rl == null) continue;
            Optional<Holder.Reference<Item>> opt = BuiltInRegistries.ITEM.get(rl);

            if (opt.isPresent())
            {
                set.add(opt.get().value());
            }
            else
            {
                MaLiLib.LOGGER.warn(StringUtils.translate("malilib.error.invalid_item_blacklist_entry", name));
            }
        }
    }
}

package fi.dy.masa.malilib.util.i18n;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.NonNull;

import net.minecraft.util.StringRepresentable;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ImmutableCopy;

public class i18nConfig implements IConfigOptionListEntry, StringRepresentable
{
	private final ImmutableList<i18nOption> options;
	private final i18nManager manager;
	private i18nOption selectedOption;
	private int selectedIndex;

	public i18nConfig(@Nonnull i18nManager manager)
	{
		this.manager = manager;
		this.options = ImmutableCopy.of(manager.getLanguageOptions()).toList();
		this.selectedOption = manager.getLang().toOption();
		this.calculateIndex();
	}

	private void calculateIndex()
	{
		for (int i = 0; i < this.options.size(); i++)
		{
			i18nOption option = this.options.get(i);

			if (option == this.selectedOption)
			{
				this.selectedIndex = i;
				break;
			}
		}
	}

	public boolean isModified()
	{
		return !this.getManager().getDefaultLang().getLangCode().equalsIgnoreCase(this.getManager().getLang().getLangCode());
	}

	public void resetToDefault()
	{
		this.manager.resetLangToDefault();
		this.selectedOption = manager.getLang().toOption();
		this.calculateIndex();
	}

	public i18nManager getManager()
	{
		return this.manager;
	}

	public i18nOption getSelectedOption()
	{
		return this.selectedOption;
	}

	public i18nLang getDefaultLang()
	{
		return this.manager.getDefaultLang();
	}

	public i18nLang getLang()
	{
		return this.manager.getLang();
	}

	@Override
	public String getStringValue()
	{
		return this.selectedOption.getKey();
	}

	@Override
	public String getDisplayName()
	{
		return this.selectedOption.getTranslatedName();
	}

	@Override
	public List<String> getHoverText()
	{
		List<String> credits = this.selectedOption.getCredits();

		if (!credits.isEmpty())
		{
			List<String> result = new ArrayList<>();

			result.add(StringUtils.translate("malilib.gui.button.hovertext.lang_credits.title")+" §f[§e"+this.selectedOption.getKey()+"§f]§r");

			for (String credit : credits)
			{
				result.add(StringUtils.translate("malilib.gui.button.hovertext.lang_credits.credit", credit));
			}

			return result;
		}

		return List.of();
	}

	@Override
	public @NonNull String getSerializedName()
	{
		return this.getStringValue();
	}

	@Override
	public i18nConfig cycle(boolean forward)
	{
		int id = this.selectedIndex;
		int length = this.options.size();

		if (forward)
		{
			if (++id >= length)
			{
				id = 0;
			}
		}
		else
		{
			if (--id < 0)
			{
				id = length - 1;
			}
		}

		this.selectedIndex = id % length;
		this.selectedOption = this.options.get(this.selectedIndex);
		this.manager.setLang(this);

		return this;
	}

	@Override
	public i18nConfig fromString(String value)
	{
		this.selectedOption = i18nOption.fromString(value);
		this.manager.setLang(this);
		return this;
	}
}

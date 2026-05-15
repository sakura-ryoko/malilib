package fi.dy.masa.malilib.util.i18n;

import java.util.List;

// TODO; Add more languages as needed
public enum i18nOption
{
	AR_SA   ("ar_sa", "العربية (العالم العربي)", List.of()),
	CA_ES   ("ca_es", "Català (Catalunya)", List.of()),
	CS_CZ   ("cs_cz", "Čeština (Česko)", List.of()),
	DE_DE   ("de_de", "Deutsch (Deutschland)", List.of()),
	EL_GR   ("el_gr", "Ελληνικά (Ελλάδα)", List.of()),
	EN_GB   ("en_gb", "English (UK)", List.of()),
	EN_US   ("en_us", "English (US)", List.of("masa", "sakura-ryoko")),
	ES_ES   ("es_es", "Español (España)", List.of("DanteMezz", "typomc", "garciarojodiego")),
	ES_MX   ("es_mx", "Español (México)", List.of()),
	FI_FI   ("fi_fi", "Suomi (Suomi)", List.of()),
	FIL_PH  ("fil_ph","Filipino (Pilipinas)", List.of()),
	FR_CA   ("fr_ca", "Français (Canada)", List.of()),
	FR_FR   ("fr_fr", "Français (France)", List.of()),
	GA_IE   ("ga_ie", "Gaeilge (Éire)", List.of()),
	HE_IL   ("he_il", "עברית (ישראל)", List.of()),
	HI_IN   ("hi_in", "हिंदी (भारत)", List.of()),
	HR_HR   ("hr_hr", "Hrvatski (Hrvatska)", List.of()),
	HU_HU   ("hu_hu", "Magyar (Magyarország)", List.of()),
	ID_ID   ("id_id", "Bahasa Indonesia (Indonesia)", List.of()),
	IG_NG   ("ig_ng", "Igbo (Naigeria)", List.of()),
	IS_IS   ("is_is", "Íslenska (Ísland)", List.of()),
	IT_IT   ("it_it", "Italiano (Italia)", List.of("VladAndreiMorariu")),
	JA_JP   ("ja_jp", "日本語 (日本)", List.of("whiwhiw", "co-91")),
	KO_KR   ("ko_kr", "한국어 (대한민국)", List.of("MagPlum")),
	LA_LA   ("la_la", "Latina (Latium)", List.of()),
	LI_LI   ("li_li", "Limburgs (Limburg)", List.of()),
	LZH     ("lzh",   "文言 (華夏)", List.of("Kaohaaa")),
	MK_MK   ("mk_mk", "Македонски (Северна Македонија)", List.of()),
	NL_BE   ("nl_be", "Vlaams (België)", List.of()),
	NL_NL   ("nl_nl", "Nederlands (Nederland)", List.of()),
	PL_PL   ("pl_pl", "Polski (Polska)", List.of()),
	PT_BR   ("pt_br", "Português (Brasil)", List.of()),
	PT_PT   ("pt_pt", "Português (Portugal)", List.of()),
	RO_RO   ("ro_ro", "Română (România)", List.of()),
	RU_RU   ("ru_ru", "Русский (Россия)", List.of("TheEvilM", "xw1w1", "Lomt1c", "Felix14-v2")),
	SE_NO   ("se_no", "Davvisámegiella (Sápmi)", List.of()),
	SK_SK   ("sk_sk", "Slovenčina (Slovensko)", List.of()),
	SL_SI   ("sl_si", "Slovenščina (Slovenija)", List.of()),
	SO_SO   ("so_so", "Af-Soomaali (Soomaaliya)", List.of()),
	SQ_AL   ("sq_al", "Shqip (Shqiperia)", List.of()),
	SV_SE   ("sv_se", "Svenska (Sverige)", List.of("el97")),
	TA_IN   ("ta_in", "தமிழ் (இந்தியா)", List.of()),
	TH_TH   ("th_th", "ไทย (ประเทศไทย)", List.of()),
	TL_PH   ("tl_ph", "Tagalog (Pilipinas)", List.of()),
	TR_TR   ("tr_tr", "Türkçe (Türkiye)", List.of("Carex", "egeesin")),
	UK_UA   ("uk_ua", "Українська (Україна)", List.of("StarmanMine142", "GIGABAIT93")),
	VAL_ES  ("val_es","Català (Valencià)", List.of()),
	VI_VN   ("vi_vn", "Tiếng Việt (Việt Nam)", List.of()),
	YO_NG   ("yo_ng", "Yorùbá (Nàìjíríà)", List.of()),
	ZH_CN   ("zh_cn", "简体中文 (中国大陆)", List.of("金合欢酱喵~ (acaciachan)", "s-yh-china (violetc)", "zly2006 (Liyan Zhao)", "BiliXWhite", "OyatsuSuki", "DreamingLri")),
	ZH_HK   ("zh_hk", "繁體中文 (香港特別行政區)", List.of()),
	ZH_TW   ("zh_tw", "繁體中文 (台灣)", List.of("notlin4", "Blackrowtw", "TNTsky", "GahahaWang")),
	ZLM_ARAB("zlm_arab","بهاس ملايو (مليسيا)", List.of()),
	UNKNOWN ("unknown", "UNK: 'xxx' not found", List.of()),
	;

	private final String key;
	private final String translatedName;
	private final List<String> credits;
	private String description;

	i18nOption(String key, String translatedName, List<String> credits)
	{
		this.key = key;
		this.translatedName = translatedName;
		this.credits = credits;
		this.description = "";
	}

	public String getKey()
	{
		return this.key;
	}

	public String getTranslatedName()
	{
		if (!this.description.isEmpty())
		{
			return this.description;
		}

		return translatedName;
	}

	public List<String> getCredits()
	{
		return this.credits;
	}

	private i18nOption setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public static i18nOption fromString(String key)
	{
		// Remove file extension, if present.
		if (key.contains(".json"))
		{
			key = key.replace(".json", "");
		}

		for (i18nOption e : values())
		{
			if (e.key.equalsIgnoreCase(key))
			{
				return e;
			}
		}

		// Not Mapped
		return UNKNOWN.setDescription(String.format("Custom (%s)", key));
	}
}

package fi.dy.masa.malilib.util.i18n;

// TODO; Add more languages as needed
public enum i18nOption
{
	AR_SA   ("ar_sa", "العربية (العالم العربي)"),
	CA_ES   ("ca_es", "Català (Catalunya)"),
	CS_CZ   ("cs_cz", "Čeština (Česko)"),
	DE_DE   ("de_de", "Deutsch (Deutschland)"),
	EL_GR   ("el_gr", "Ελληνικά (Ελλάδα)"),
	EN_GB   ("en_gb", "English (UK)"),
	EN_US   ("en_us", "English (US)"),
	ES_ES   ("es_es", "Español (España)"),
	ES_MX   ("es_mx", "Español (México)"),
	FI_FI   ("fi_fi", "Suomi (Suomi)"),
	FIL_PH  ("fil_ph","Filipino (Pilipinas)"),
	FR_CA   ("fr_ca", "Français (Canada)"),
	FR_FR   ("fr_fr", "Français (France)"),
	GA_IE   ("ga_ie", "Gaeilge (Éire)"),
	HE_IL   ("he_il", "עברית (ישראל)"),
	HI_IN   ("hi_in", "हिंदी (भारत)"),
	HR_HR   ("hr_hr", "Hrvatski (Hrvatska)"),
	HU_HU   ("hu_hu", "Magyar (Magyarország)"),
	ID_ID   ("id_id", "Bahasa Indonesia (Indonesia)"),
	IG_NG   ("ig_ng", "Igbo (Naigeria)"),
	IS_IS   ("is_is", "Íslenska (Ísland)"),
	IT_IT   ("it_it", "Italiano (Italia)"),
	JA_JP   ("ja_jp", "日本語 (日本)"),
	KO_KR   ("ko_kr", "한국어 (대한민국)"),
	LA_LA   ("la_la", "Latina (Latium)"),
	LI_LI   ("li_li", "Limburgs (Limburg)"),
	LZH     ("lzh",   "文言 (華夏)"),
	MK_MK   ("mk_mk", "Македонски (Северна Македонија)"),
	NL_BE   ("nl_be", "Vlaams (België)"),
	NL_NL   ("nl_nl", "Nederlands (Nederland)"),
	PL_PL   ("pl_pl", "Polski (Polska)"),
	PT_BR   ("pt_br", "Português (Brasil)"),
	PT_PT   ("pt_pt", "Português (Portugal)"),
	RO_RO   ("ro_ro", "Română (România)"),
	RU_RU   ("ru_ru", "Русский (Россия)"),
	SE_NO   ("se_no", "Davvisámegiella (Sápmi)"),
	SK_SK   ("sk_sk", "Slovenčina (Slovensko)"),
	SL_SI   ("sl_si", "Slovenščina (Slovenija)"),
	SO_SO   ("so_so", "Af-Soomaali (Soomaaliya)"),
	SQ_AL   ("sq_al", "Shqip (Shqiperia)"),
	SV_SE   ("sv_se", "Svenska (Sverige)"),
	TA_IN   ("ta_in", "தமிழ் (இந்தியா)"),
	TH_TH   ("th_th", "ไทย (ประเทศไทย)"),
	TL_PH   ("tl_ph", "Tagalog (Pilipinas)"),
	TR_TR   ("tr_tr", "Türkçe (Türkiye)"),
	UK_UA   ("uk_ua", "Українська (Україна)"),
	VAL_ES  ("val_es","Català (Valencià)"),
	VI_VN   ("vi_vn", "Tiếng Việt (Việt Nam)"),
	YO_NG   ("yo_ng", "Yorùbá (Nàìjíríà)"),
	ZH_CN   ("zh_cn", "简体中文 (中国大陆)"),
	ZH_HK   ("zh_hk", "繁體中文 (香港特別行政區)"),
	ZH_TW   ("zh_tw", "繁體中文 (台灣)"),
	ZLM_ARAB("zlm_arab","بهاس ملايو (مليسيا)"),
	ERROR   ("error", "ERROR; xxx not found"),
	;

	private final String key;
	private final String translatedName;
	private String description;

	i18nOption(String key, String translatedName)
	{
		this.key = key;
		this.translatedName = translatedName;
		this.description = "";
	}

	public String getKey()
	{
		return key;
	}

	public String getTranslatedName()
	{
		if (!this.description.isEmpty())
		{
			return this.description;
		}

		return translatedName;
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
		return ERROR.setDescription(String.format("ERROR; '%s' Not mapped", key));
	}
}

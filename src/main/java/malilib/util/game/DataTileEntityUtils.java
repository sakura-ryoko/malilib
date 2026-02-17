package malilib.util.game;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import malilib.util.data.Constants;
import malilib.util.data.DyeColorCode;
import malilib.util.data.Identifier;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.converter.DataConverterNbt;
import malilib.util.data.tag.util.DataTypeUtils;
import malilib.util.game.wrap.RegistryUtils;
import malilib.util.nbt.NbtKeys;
import malilib.util.position.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * The purpose of this Library is to fully utilize {@link malilib.util.data.tag.DataView} Tags to parse NBT tags for use in downstream mods;
 * such as {@link TileEntity} data for MiniHUD's Info Lines; or other specific {@link malilib.util.data.tag.DataView} needs.
 * It should not fail to mimic the various 'readNbt()' / 'writeNbt()' type functions under Vanilla's {@link TileEntity} system,
 * without needing to instance a new {@link TileEntity} object; which can potentially break other people's mods if used too often.
 * These are more important in later versions of Minecraft; especially when it can be hard to remember every NBT tag name;
 * or CODEC method; to say; export a Skulls's {@link GameProfile} from Raw Tags, and then find a specific value; for example.
 * Utilizing {@link NbtKeys} is also very helpful to track changes across versions of Minecraft.
 */
public class DataTileEntityUtils
{
	/**
	 * Get the Tile Entity Type from the Data Tag
	 * @param data -
	 * @return -
	 * @implNote In the future, BlockEntityType<T> is utilized
	 */
	public static Optional<Identifier> getTileEntityType(@Nonnull CompoundData data)
	// <T extends TileEntity> BlockEntityType<T> type
	{
		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
		{
			Identifier id = Identifier.of(data.getString(NbtKeys.ID));

			if (id != null && RegistryUtils.isTileEntityValid(id))
			{
				return Optional.of(id);
			}
		}

		return Optional.empty();
	}

	/**
	 * Write the Block Entity ID tag.
	 * @param te -
	 * @param dataIn -
	 * @return -
	 * @implNote In the future, BlockEntityType<T> is utilized
	 */
	public static <T extends TileEntity> CompoundData setTileEntityType(Class<T> te, @Nullable CompoundData dataIn)
	// BlockEntityType<T> type
	{
		CompoundData data = dataIn != null ? dataIn : new CompoundData();
		ResourceLocation rl = TileEntity.getKey(te);

		if (rl != null)
		{
			return data.putString(NbtKeys.ID, rl.toString());
		}

		return data;
	}

	/**
	 * Get the Text Component of the Custom Name
	 * @param data -
	 * @return -
	 * @implNote In the future; the Text Serialization changes multiple times
	 */
	public static Optional<ITextComponent> getCustomName(@Nonnull CompoundData data, @Nullable String key)
	// Component / @Nonnull RegistryAccess registry
	{
		if (key == null && data.contains(NbtKeys.CUSTOM_NAME, Constants.NBT.TAG_STRING))
		{
			return Optional.of(new TextComponentString(data.getString(NbtKeys.CUSTOM_NAME)));
		}
		else if (data.contains(key, Constants.NBT.TAG_STRING))
		{
			return Optional.of(new TextComponentString(data.getString(key)));
		}

		return Optional.empty();
	}

	/**
	 * Get a RecordItem as an ItemStack from Data tag
	 * @param data -
	 * @return -
	 * @implNote After ~1.21, the Registry is required
	 */
	public static Optional<ItemStack> getRecordItem(@Nonnull CompoundData data)
	// @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.RECORD_ITEM, Constants.NBT.TAG_COMPOUND))
		{
			return DataTypeUtils.toItemStack(data.getCompound(NbtKeys.RECORD_ITEM));
		}
		else if (data.contains(NbtKeys.RECORD, Constants.NBT.TAG_INT))
		{
			try
			{
				final Item item = Item.getItemById(data.getInt(NbtKeys.RECORD));
				return Optional.of(new ItemStack(item));
			}
			catch (Exception ignored) {}
		}

		return Optional.empty();
	}

	/**
	 * Get Banner patterns and colors from Data tag
	 * @param data -
	 * @return -
	 */
	public static Optional<BannerPatternsData> getBannerPatterns(@Nonnull CompoundData data)
	{
		DyeColorCode color = DyeColorCode.WHITE;
		List<BannerPattern> patterns = new ArrayList<>();
		List<DyeColorCode> colors = new ArrayList<>();

		if (data.contains(NbtKeys.BASE_COLOR, Constants.NBT.TAG_INT))
		{
			color = DyeColorCode.getByMeta(data.getInt(NbtKeys.BASE_COLOR));
		}

		if (data.contains(NbtKeys.PATTERNS, Constants.NBT.TAG_COMPOUND))
		{
			ListData listData = data.getList(NbtKeys.PATTERNS, Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < listData.size(); i++)
			{
				CompoundData entry = listData.getCompoundAt(i);
				getEachBannerPattern(entry, patterns, colors);
			}
		}

		if (patterns.isEmpty() && colors.isEmpty() && color == DyeColorCode.WHITE)
		{
			return Optional.empty();
		}

		return Optional.of(new BannerPatternsData(color, patterns, colors));
	}

	/**
	 * Read each Banner Pattern from a List Entry
	 * @param data -
	 * @param patterns -
	 * @param colors -
	 */
	public static void getEachBannerPattern(@Nonnull CompoundData data, @Nonnull List<BannerPattern> patterns, @Nonnull List<DyeColorCode> colors)
	{
		BannerPattern pattern = null;
		int entryColor = -1;

		if (data.contains(NbtKeys.PATTERN, Constants.NBT.TAG_STRING))
		{
			pattern = BannerPattern.byHash(data.getString(NbtKeys.PATTERN));
		}
		if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_INT))
		{
			entryColor = data.getInt(NbtKeys.COLOR);
		}

		if (pattern != null)
		{
			patterns.add(pattern);
			colors.add(DyeColorCode.getByMeta(entryColor));
		}
	}

	public static class BannerPatternsData
	{
		private final DyeColorCode baseColor;
		private final List<BannerPattern> patterns;
		private final List<DyeColorCode> colors;

		BannerPatternsData(DyeColorCode baseColor, List<BannerPattern> patterns, List<DyeColorCode> colors)
		{
			this.baseColor = baseColor;
			this.patterns = patterns;
			this.colors = colors;
		}

		public DyeColorCode baseColor() { return this.baseColor; }

		public List<BannerPattern> patterns() { return this.patterns; }

		public List<DyeColorCode> colors() { return this.colors; }
	}

	/**
	 * Get Beacon Data from Data Tag.
	 * @param data -
	 * @return -
	 */
	public static Optional<BeaconData> getBeaconData(@Nonnull CompoundData data)
	{
		Potion pri = null;
		Potion sec =  null;
		int levels = -1;

		if (data.contains(NbtKeys.PRIMARY_EFFECT, Constants.NBT.TAG_INT))
		{
			pri = Potion.getPotionById(data.getInt(NbtKeys.PRIMARY_EFFECT));
		}
		else if (data.contains(NbtKeys.NEW_PRIMARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			pri = Potion.getPotionFromResourceLocation(data.getString(NbtKeys.NEW_PRIMARY_EFFECT));
		}

		if (data.contains(NbtKeys.SECONDARY_EFFECT, Constants.NBT.TAG_INT))
		{
			sec = Potion.getPotionById(data.getInt(NbtKeys.SECONDARY_EFFECT));
		}
		else if (data.contains(NbtKeys.NEW_SECONDARY_EFFECT, Constants.NBT.TAG_STRING))
		{
			sec = Potion.getPotionFromResourceLocation(data.getString(NbtKeys.NEW_SECONDARY_EFFECT));
		}

		if (data.contains(NbtKeys.LEVELS, Constants.NBT.TAG_INT))
		{
			levels = data.getInt(NbtKeys.LEVELS);
		}

		if (pri == null && sec == null)
		{
			return Optional.empty();
		}

		return Optional.of(new BeaconData(pri, sec, levels));
	}

	public static class BeaconData
	{
		// Potion -> StatusEffect / MobEffect
		public static final Set<Potion> VALID_EFFECTS = collectEffects();
		private final @Nullable Potion primaryEffect;
		private final @Nullable Potion secondaryEffect;
		private final int levels;

		public BeaconData(@Nullable Potion primary, @Nullable Potion secondary, int levels)
		{
			this.primaryEffect = isValidEffect(primary) ? primary : null;
			this.secondaryEffect = isValidEffect(secondary) ? secondary : null;
			this.levels = levels;
		}

		public Optional<Potion> primaryEffect() {return Optional.ofNullable(this.primaryEffect);}

		public Optional<Potion> secondaryEffect() {return Optional.ofNullable(this.secondaryEffect);}

		public int levels() {return this.levels;}

		private static ImmutableSet<Potion> collectEffects()
		{
			ImmutableSet.Builder<Potion> set = new ImmutableSet.Builder<>();

			for (Potion[] pot : TileEntityBeacon.EFFECTS_LIST)
			{
				set.addAll(Arrays.asList(pot));
			}

			return set.build();
		}

		public static boolean isValidEffect(@Nullable Potion effect)
		{
			if (effect == null) { return false; }
			return VALID_EFFECTS.contains(effect);
		}
	}

	/**
	 * Get the End Gateway's Exit Portal from Data Tag.
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Long, BlockPos>> getEndGatewayData(@Nonnull CompoundData data)
	{
		long age = -1;
		BlockPos pos = null;

		if (data.contains(NbtKeys.AGE, Constants.NBT.TAG_LONG))
		{
			age = data.getLong(NbtKeys.AGE);
		}

		if (data.contains(NbtKeys.EXIT_PORTAL, Constants.NBT.TAG_COMPOUND))
		{
			pos = DataTypeUtils.readPrefixedPosFromTag(data.getCompound(NbtKeys.EXIT_PORTAL), "");
		}
		else if (data.contains(NbtKeys.NEW_EXIT, Constants.NBT.TAG_INT_ARRAY))
		{
			pos = DataTypeUtils.readBlockPosFromArrayTag(data, NbtKeys.NEW_EXIT);
		}

		if (pos == null && age < 0)
		{
			return Optional.empty();
		}

		return Optional.of(Pair.of(age, pos));
	}

	/**
	 * Read Sign Text from Data tag
	 * NOTE:  This code makes NO attempts to "resolve" the various command codes
	 * @param data -
	 * @return -
	 * @implNote Signs from 1.20.2+ are two-sided and can be Waxed
	 */
	public static Optional<SignTextData> getSignText(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		if (data.contains(NbtKeys.NEW_FRONT_TEXT, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData fd =  data.getCompound(NbtKeys.NEW_FRONT_TEXT);
			CompoundData bd = new CompoundData();
			boolean waxed = false;

			if (data.contains(NbtKeys.NEW_BACK_TEXT, Constants.NBT.TAG_COMPOUND))
			{
				bd =  fd.getCompound(NbtKeys.NEW_BACK_TEXT);
			}

			if (data.contains(NbtKeys.NEW_WAXED, Constants.NBT.TAG_COMPOUND))
			{
				waxed = data.getBoolean(NbtKeys.NEW_WAXED);
			}

			SignTextData.Side front = getSignTextEachSide(fd).orElse(null);
			SignTextData.Side back = getSignTextEachSide(bd).orElse(null);

			if (front == null && back == null)
			{
				return Optional.empty();
			}

			return Optional.of(new SignTextData(front, back, waxed));
		}
		else
		{
			SignTextData.Side front = getSignTextLegacySide(data).orElse(null);

			if (front == null)
			{
				return Optional.empty();
			}

			return Optional.of(new SignTextData(front, null, false));
		}
	}

	/**
	 * Get The Front Side of a Sign
	 * @param data -
	 * @return -
	 */
	public static Optional<SignTextData.Side> getSignTextLegacySide(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		ITextComponent[] front = SignTextData.emptyArray();
		ITextComponent[] filter = SignTextData.emptyArray();
		DyeColorCode color = DyeColorCode.BLACK;
		boolean glow = false;

		if (data.contains(NbtKeys.COLOR, Constants.NBT.TAG_STRING))
		{
			color = DyeColorCode.fromStringOrDefault(data.getString(NbtKeys.COLOR), DyeColorCode.BLACK);
		}
		if (data.contains(NbtKeys.SIGN_GLOWING, Constants.NBT.TAG_BYTE))
		{
			glow = data.getBoolean(NbtKeys.SIGN_GLOWING);
		}

		for (int i = 0; i < 4; i++)
		{
			final int index = i;

			if (data.contains(NbtKeys.SIGN_TEXT_PREFIX+index, Constants.NBT.TAG_STRING))
			{
				DataTypeUtils.toTextComponent(data, NbtKeys.SIGN_TEXT_PREFIX+i).ifPresent(e -> front[index] = e);
			}

			if (data.contains(NbtKeys.SIGN_FILTER_PREFIX+index, Constants.NBT.TAG_STRING))
			{
				DataTypeUtils.toTextComponent(data, NbtKeys.SIGN_FILTER_PREFIX+i).ifPresent(e -> filter[index] = e);
			}
			else
			{
				filter[index] = front[index].createCopy();
			}
		}

		if (front.length != 4)
		{
			return Optional.empty();
		}

		return Optional.of(new SignTextData.Side(front, filter, color, glow));
	}

	public static Optional<SignTextData.Side> getSignTextEachSide(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		ITextComponent[] text = SignTextData.emptyArray();
		ITextComponent[] filter = null;
		DyeColorCode color = DyeColorCode.BLACK;
		boolean glow = false;

		if (data.containsList(NbtKeys.NEW_SIGN_MESSAGES, Constants.NBT.TAG_STRING))
		{
			ListData list = data.getListOrDefault(NbtKeys.NEW_SIGN_MESSAGES, Constants.NBT.TAG_STRING, new ListData(Constants.NBT.TAG_STRING));

			for (int i = 0; i < list.size() && i < 4; i++)
			{
				text[i] = DataTypeUtils.toTextComponent(list.getStringAt(i)).orElse(SignTextData.EMPTY);
			}
		}

		if (data.containsList(NbtKeys.NEW_SIGN_FILTERED, Constants.NBT.TAG_STRING))
		{
			ListData list = data.getListOrDefault(NbtKeys.NEW_SIGN_FILTERED, Constants.NBT.TAG_STRING, new ListData(Constants.NBT.TAG_STRING));
			filter = SignTextData.emptyArray();

			for (int i = 0; i < list.size() && i < 4; i++)
			{
				filter[i] = DataTypeUtils.toTextComponent(list.getStringAt(i)).orElse(SignTextData.EMPTY);
			}
		}

		if (data.contains(NbtKeys.NEW_SIGN_COLOR, Constants.NBT.TAG_INT))
		{
			color = DyeColorCode.getByMeta(data.getInt(NbtKeys.NEW_SIGN_COLOR));
		}

		if (data.contains(NbtKeys.NEW_SIGN_GLOW, Constants.NBT.TAG_BYTE))
		{
			glow = data.getBoolean(NbtKeys.NEW_SIGN_GLOW);
		}

		if (text.length != 4)
		{
			return Optional.empty();
		}

		return Optional.of(new SignTextData.Side(text, filter, color, glow));
	}

	/**
	 * Designed to be compliant with 1.20.2+ with a front / back {@link Side}
	 */
	public static class SignTextData
	{
		public static final ITextComponent EMPTY = new TextComponentString("");
		private final Side front;
		private final @Nullable Side back;
		private final boolean waxed;

		SignTextData(Side front, @Nullable Side back, boolean waxed)
		{
			this.front = front;
			this.back = back;
			this.waxed = waxed;
		}

		public Side front() { return this.front; }

		public @Nullable Side back() { return this.back; }

		public boolean waxed() { return this.waxed; }

		public static ITextComponent[] emptyArray()
		{
			return new ITextComponent[]{EMPTY, EMPTY, EMPTY, EMPTY};
		}

		public static class Side
		{
			private final ITextComponent[] text;
			private final ITextComponent[] filter;
			private final DyeColorCode color;
			private final boolean glow;

			Side(@Nonnull ITextComponent[] text, @Nullable ITextComponent[] filter, @Nullable DyeColorCode color, boolean glow)
			{
				this.text = text;
				this.filter = filter != null ? filter : text.clone();
				this.color = color != null ? color : DyeColorCode.BLACK;
				this.glow = glow;
			}

			public List<ITextComponent> text() { return new ArrayList<>(Arrays.asList(this.text)); }

			public List<ITextComponent> filter() { return new ArrayList<>(Arrays.asList(this.filter)); }

			public DyeColorCode color() { return this.color != null ? this.color : DyeColorCode.BLACK; }

			public boolean glow() { return this.glow; }
		}
	}

	/**
	 * Get the Skull Data from Data tag
	 * @param data -
	 * @return -
	 */
	public static Optional<SkullProfileData> getSkullData(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		ITextComponent customName = null;
		Identifier sound = null;

		if (data.contains(NbtKeys.NEW_SKULL_CUSTOM_NAME, Constants.NBT.TAG_COMPOUND))
		{
			customName = DataTypeUtils.toTextComponent(data, NbtKeys.NEW_SKULL_CUSTOM_NAME).orElse(null);
		}

		if (data.contains(NbtKeys.NEW_NOTE, Constants.NBT.TAG_STRING))
		{
			sound = Identifier.of(data.getStringOrDefault(NbtKeys.NEW_NOTE, ""));
		}

		if (data.contains(NbtKeys.NEW_SKULL_PROFILE, Constants.NBT.TAG_COMPOUND))
		{
			GameProfile profile = getNewSkullProfile(data.getCompound(NbtKeys.NEW_SKULL_PROFILE)).orElse(null);
			UUID uuid;

			if (profile != null && profile.getId() != null)
			{
				uuid = profile.getId();
			}
			else
			{
				uuid = new UUID(0L, 0L);
			}

			return Optional.of(new SkullProfileData(sound, customName, uuid, profile));
		}
		else if (data.contains(NbtKeys.NEW_SKULL_PROFILE, Constants.NBT.TAG_STRING))
		{
			// It's possible, that it "could" be a simple String of the name; according to the code.
			UUID uuid = new UUID(0L, 0L);
			GameProfile profile = new GameProfile(uuid, data.getStringOrDefault(NbtKeys.NEW_SKULL_PROFILE, ""));
			return Optional.of(new SkullProfileData(sound, customName, uuid, profile));
		}
		else
		{
			return getLegacySkullProfile(data);
		}
	}

	/**
	 * Get Legacy Skull Data
	 * @param data -
	 * @return -
	 */
	public static Optional<SkullProfileData> getLegacySkullProfile(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		int type = -1;
		int rot = -1;
		GameProfile profile = null;
		Identifier sound = null;

		if (data.contains(NbtKeys.SKULL_TYPE, Constants.NBT.TAG_BYTE))
		{
			type = data.getByte(NbtKeys.SKULL_TYPE);
		}
		if (data.contains(NbtKeys.SKULL_ROT, Constants.NBT.TAG_BYTE))
		{
			rot = data.getByte(NbtKeys.SKULL_ROT);
		}
		if (data.contains(NbtKeys.NEW_NOTE, Constants.NBT.TAG_STRING))
		{
			sound = Identifier.of(data.getStringOrDefault(NbtKeys.NEW_NOTE, ""));
		}
		if (type == 3)
		{
			if (data.contains(NbtKeys.SKULL_OWNER, Constants.NBT.TAG_COMPOUND))
			{
				profile = NBTUtil.readGameProfileFromNBT(DataConverterNbt.toVanillaCompound(data.getCompound(NbtKeys.SKULL_OWNER)));
			}
			else if (data.contains(NbtKeys.SKULL_EXTRA_TYPE, Constants.NBT.TAG_STRING))
			{
				final String name = data.getStringOrDefault(NbtKeys.SKULL_EXTRA_TYPE, "");

				if (!name.isEmpty())
				{
					profile = new GameProfile(new UUID(0L, 0L), name);
				}
			}
		}

		if (profile == null && type == -1)
		{
			return Optional.empty();
		}

		return Optional.of(new SkullProfileData(type, rot, sound, profile));
	}

	/**
	 * Get the "New" Skull Data
	 * @param data -
	 * @return -
	 */
	public static Optional<GameProfile> getNewSkullProfile(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		String name = null;
		UUID uuid = null;

		if (data.contains(NbtKeys.NEW_SKULL_NAME, Constants.NBT.TAG_STRING))
		{
			name = data.getString(NbtKeys.NEW_SKULL_NAME);
		}

		if (data.contains(NbtKeys.ID, Constants.NBT.TAG_INT_ARRAY))
		{
			uuid = DataTypeUtils.readUuidFromIntArray(data, NbtKeys.ID);
		}

		if (name == null && uuid == null)
		{
			return Optional.empty();
		}

		// We aren't bothering with the "properties" tag here.
		return Optional.of(new GameProfile(uuid, name));
	}

	public static class SkullProfileData
	{
		private final int type;
		private final int rot;
		private final @Nullable Identifier sound;
		private final @Nullable ITextComponent name;
		private final @Nullable UUID uuid;
		private final GameProfile profile;

		// Legacy
		SkullProfileData(int type, int rot, @Nullable Identifier sound, GameProfile profile)
		{
			this(type, rot, sound, null, profile != null ? profile.getId() : null, profile);
		}

		// 1.20.6+
		SkullProfileData(@Nullable Identifier sound, @Nullable ITextComponent name, @Nullable UUID uuid, GameProfile profile)
		{
			this(-1, -1, sound, name, uuid, profile);
		}

		SkullProfileData(int type, int rot, @Nullable Identifier sound, @Nullable ITextComponent name, @Nullable UUID uuid, GameProfile profile)
		{
			this.type = type;
			this.rot = rot;
			this.sound = sound;
			this.name = name;
			this.uuid = uuid;
			this.profile = profile;
		}

		public int type() { return this.type; }

		public int rot() { return this.rot; }

		public Optional<Identifier> sound() { return Optional.ofNullable(this.sound); }

		public Optional<ITextComponent> name() { return Optional.ofNullable(this.name); }

		public Optional<UUID> uuid() { return Optional.ofNullable(this.uuid); }

		public GameProfile profile() { return this.profile; }
	}

	/**
	 * Get Flower Pot Item from Data tag
	 * @param data -
	 * @return -
	 */
	public static Optional<Pair<Item, Integer>> getFlowerPotItem(@Nonnull CompoundData data)
	{
		Item item = null;
		int damage = 0;

		try
		{
			if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_STRING))
			{
				item = Item.getByNameOrId(data.getString(NbtKeys.ITEM));
			}
			else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_INT))
			{
				item = Item.getItemById(data.getInt(NbtKeys.ITEM));
			}
		}
		catch (Exception ignored) {}

		if (data.contains(NbtKeys.POT_DATA, Constants.NBT.TAG_INT))
		{
			damage = data.getInt(NbtKeys.POT_DATA);
		}

		if (item == null && damage == 0)
		{
			return Optional.empty();
		}

		return Optional.of(Pair.of(item, damage));
	}

	/**
	 * Get Bed Color by Data tag
	 * @param data -
	 * @return -
	 */
	public static DyeColorCode getBedColor(@Nonnull CompoundData data)
	{
		DyeColorCode color = DyeColorCode.RED;

		if (data.contains(NbtKeys.BED_COLOR, Constants.NBT.TAG_INT))
		{
			color = DyeColorCode.getByMeta(data.getInt(NbtKeys.BED_COLOR));
		}

		return color;
	}

	/**
	 * Read a Comparator's Output Signal from a Data tag
	 * @param data -
	 * @return -
	 */
	public static int getComparatorOutput(@Nonnull CompoundData data)
	{
		int signal = 0;

		if (data.contains(NbtKeys.OUTPUT_SIGNAL, Constants.NBT.TAG_INT))
		{
			signal = data.getInt(NbtKeys.OUTPUT_SIGNAL);
		}

		return signal;
	}

	/**
	 * Get a Lectern's Book and Page number.
	 *
	 * @param data -
	 * @return -
	 * @implNote Lecterns were added under 1.14.x
	 */
	public static Optional<Pair<ItemStack, Integer>> getBook(@Nonnull CompoundData data) // @Nonnull RegistryAccess registry
	{
		ItemStack book = null;
		int current = -1;

		if (data.contains(NbtKeys.ORG_BOOK, Constants.NBT.TAG_COMPOUND))
		{
			book = DataTypeUtils.toItemStack(data.getCompound(NbtKeys.ORG_BOOK)).orElse(ItemStack.EMPTY);
		}

		if (data.contains(NbtKeys.ORG_PAGE, Constants.NBT.TAG_INT))
		{
			current = data.getInt(NbtKeys.ORG_PAGE);
		}

		if (book == null && current < 0)
		{
			return Optional.empty();
		}

		return Optional.of(Pair.of(book, current));
	}

	/**
	 * Get a Furnaces 'Used Recipes' from Data Tag.
	 * @implNote This seems to have begun around 1.14.x, and is modified around 1.17.x
	 * @param data -
	 * @return -
	 */
	public static Optional<UsedRecipeData> getUsedRecipes(@Nonnull CompoundData data)
	{
		HashMap<Identifier, Integer> recipesUsed = new HashMap<>();

		if (data.contains(NbtKeys.ORG_RECIPES_USED, Constants.NBT.TAG_COMPOUND))
		{
			CompoundData recipes = data.getCompound(NbtKeys.ORG_RECIPES_USED);
			Set<String> keys = recipes.getKeys();

			for (String key : keys)
			{
				Identifier id = Identifier.of(key);
				if (id == null) { continue; }

				int amount = recipes.getIntOrDefault(key, -1);
				recipesUsed.put(id, amount);
			}
		}
		else if (data.contains(NbtKeys.RECIPES_USED_SIZE, Constants.NBT.TAG_SHORT))
		{
			final int total = data.getShortOrDefault(NbtKeys.RECIPES_USED_SIZE, (short) 0);

			for (int i = 0; i < total; i++)
			{
				String rl = "";
				int count = -1;

				if (data.contains(NbtKeys.RECIPE_LOCATION_PREFIX+i, Constants.NBT.TAG_STRING))
				{
					rl =  data.getString(NbtKeys.RECIPE_LOCATION_PREFIX+i);
				}
				if (data.contains(NbtKeys.RECIPE_AMOUNT_PREFIX+i, Constants.NBT.TAG_INT))
				{
					count = data.getInt(NbtKeys.RECIPE_AMOUNT_PREFIX+i);
				}

				Identifier id = Identifier.of(rl);

				if (id != null && count > 0)
				{
					recipesUsed.put(id, count);
				}
			}
		}

		if (recipesUsed.isEmpty())
		{
			return Optional.empty();
		}

		return Optional.of(new UsedRecipeData(recipesUsed));
	}

	public static class UsedRecipeData
	{
		private final HashMap<Identifier, Integer> recipes;

		public UsedRecipeData(HashMap<Identifier, Integer> recipes)
		{
			this.recipes = recipes;
		}

		public HashMap<Identifier, Integer> recipes()
		{
			return this.recipes;
		}
	}

	// todo add Bee's

	/**
	 * Read the Crafter's "locked slots" from Data Tag
	 *
	 * @param data -
	 * @return -
	 * @implNote This first appears under 1.21+
	 */
	public static ImmutableSet<Integer> getDisabledSlots(@Nonnull CompoundData data)
	{
		ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();

		if (data.contains(NbtKeys.NEW_DISABLED_SLOTS, Constants.NBT.TAG_INT_ARRAY))
		{
			int[] is = data.getIntArray(NbtKeys.NEW_DISABLED_SLOTS);

			for (int j : is)
			{
				builder.add(j);
			}
		}

		return builder.build();
	}
}

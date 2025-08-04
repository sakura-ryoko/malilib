package malilib.util.game;

import java.util.Optional;
import com.google.common.collect.ImmutableList;

import malilib.mixin.access.DataFixerMixin;
import malilib.util.game.wrap.GameWrap;

public class MinecraftVersion
{
    public final String displayName;
    public final int dataVersion;
    public final int protocolVersion;

    public MinecraftVersion(String displayName, int dataVersion, int protocolVersion)
    {
        this.displayName = displayName;
        this.dataVersion = dataVersion;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        MinecraftVersion that = (MinecraftVersion) o;

        return this.dataVersion == that.dataVersion;
    }

    public static final MinecraftVersion MC_1_21_8 = new MinecraftVersion("1.21.8",  4440, 772);
    public static final MinecraftVersion MC_1_21_7 = new MinecraftVersion("1.21.7",  4438, 772);
    public static final MinecraftVersion MC_1_21_6 = new MinecraftVersion("1.21.6",  4435, 771);
    public static final MinecraftVersion MC_1_21_5 = new MinecraftVersion("1.21.5",  4325, 770);
    public static final MinecraftVersion MC_1_21_4 = new MinecraftVersion("1.21.4",  4189, 769);
    public static final MinecraftVersion MC_1_21_3 = new MinecraftVersion("1.21.3",  4082, 768);
    public static final MinecraftVersion MC_1_21_2 = new MinecraftVersion("1.21.2",  4080, 768);
    public static final MinecraftVersion MC_1_21_1 = new MinecraftVersion("1.21.1",  3955, 767);
    public static final MinecraftVersion MC_1_21   = new MinecraftVersion("1.21",    3953, 767);

    public static final MinecraftVersion MC_1_20_6 = new MinecraftVersion("1.20.6",  3839, 766);
    public static final MinecraftVersion MC_1_20_5 = new MinecraftVersion("1.20.5",  3837, 766);
    public static final MinecraftVersion MC_1_20_4 = new MinecraftVersion("1.20.4",  3700, 765);
    public static final MinecraftVersion MC_1_20_3 = new MinecraftVersion("1.20.3",  3698, 765);
    public static final MinecraftVersion MC_1_20_2 = new MinecraftVersion("1.20.2",  3578, 764);
    public static final MinecraftVersion MC_1_20_1 = new MinecraftVersion("1.20.1",  3465, 763);
    public static final MinecraftVersion MC_1_20   = new MinecraftVersion("1.20",    3463, 763);

    public static final MinecraftVersion MC_1_19_4 = new MinecraftVersion("1.19.4",  3337, 762);
    public static final MinecraftVersion MC_1_19_3 = new MinecraftVersion("1.19.3",  3218, 761);
    public static final MinecraftVersion MC_1_19_2 = new MinecraftVersion("1.19.2",  3120, 760);
    public static final MinecraftVersion MC_1_19_1 = new MinecraftVersion("1.19.1",  3117, 760);
    public static final MinecraftVersion MC_1_19   = new MinecraftVersion("1.19",    3105, 759);

    public static final MinecraftVersion MC_1_18_2 = new MinecraftVersion("1.18.2",  2975, 758);
    public static final MinecraftVersion MC_1_18_1 = new MinecraftVersion("1.18.1",  2865, 757);
    public static final MinecraftVersion MC_1_18   = new MinecraftVersion("1.18",    2860, 757);

    public static final MinecraftVersion MC_1_17_1 = new MinecraftVersion("1.17.1",  2730, 756);
    public static final MinecraftVersion MC_1_17   = new MinecraftVersion("1.17",    2724, 755);

    public static final MinecraftVersion MC_1_16_5 = new MinecraftVersion("1.16.5",  2586, 754);
    public static final MinecraftVersion MC_1_16_4 = new MinecraftVersion("1.16.4",  2584, 754);
    public static final MinecraftVersion MC_1_16_3 = new MinecraftVersion("1.16.3",  2580, 753);
    public static final MinecraftVersion MC_1_16_2 = new MinecraftVersion("1.16.2",  2578, 751);
    public static final MinecraftVersion MC_1_16_1 = new MinecraftVersion("1.16.1",  2567, 736);
    public static final MinecraftVersion MC_1_16   = new MinecraftVersion("1.16",    2566, 735);

    public static final MinecraftVersion MC_1_15_2 = new MinecraftVersion("1.15.2",  2230, 578);
    public static final MinecraftVersion MC_1_15_1 = new MinecraftVersion("1.15.1",  2227, 575);
    public static final MinecraftVersion MC_1_15   = new MinecraftVersion("1.15",    2225, 573);

    public static final MinecraftVersion MC_1_14_4 = new MinecraftVersion("1.14.4",  1976, 498);
    public static final MinecraftVersion MC_1_14_3 = new MinecraftVersion("1.14.3",  1968, 490);
    public static final MinecraftVersion MC_1_14_2 = new MinecraftVersion("1.14.2",  1963, 485);
    public static final MinecraftVersion MC_1_14_1 = new MinecraftVersion("1.14.1",  1957, 480);
    public static final MinecraftVersion MC_1_14   = new MinecraftVersion("1.14",    1952, 477);

    public static final MinecraftVersion MC_1_13_2 = new MinecraftVersion("1.13.2",  1631, 404);
    public static final MinecraftVersion MC_1_13_1 = new MinecraftVersion("1.13.1",  1628, 401);
    public static final MinecraftVersion MC_1_13   = new MinecraftVersion("1.13",    1519, 393);

    public static final MinecraftVersion MC_1_12_2 = new MinecraftVersion("1.12.2",  1343, 340);
    public static final MinecraftVersion MC_1_12_1 = new MinecraftVersion("1.12.1",  1241, 338);
    public static final MinecraftVersion MC_1_12   = new MinecraftVersion("1.12",    1139, 335);

    public static final MinecraftVersion MC_1_11_2 = new MinecraftVersion("1.11.2",  922, 316);
    public static final MinecraftVersion MC_1_11_1 = new MinecraftVersion("1.11.1",  921, 316);
    public static final MinecraftVersion MC_1_11   = new MinecraftVersion("1.11",    819, 315);

    public static final MinecraftVersion MC_1_10_2 = new MinecraftVersion("1.10.2",  512, 210);
    public static final MinecraftVersion MC_1_10_1 = new MinecraftVersion("1.10.1",  511, 210);
    public static final MinecraftVersion MC_1_10   = new MinecraftVersion("1.10",    510, 210);

    public static final MinecraftVersion MC_1_9_4  = new MinecraftVersion("1.9.4",  184, 110);
    public static final MinecraftVersion MC_1_9_3  = new MinecraftVersion("1.9.3",  183, 110);
    public static final MinecraftVersion MC_1_9_2  = new MinecraftVersion("1.9.2",  176, 109);
    public static final MinecraftVersion MC_1_9_1  = new MinecraftVersion("1.9.1",  175, 108);
    public static final MinecraftVersion MC_1_9    = new MinecraftVersion("1.9",    169, 107);

    public static final MinecraftVersion MC_1_8_9  = new MinecraftVersion("1.8.9",  -1, 47);
    public static final MinecraftVersion MC_1_8_8  = new MinecraftVersion("1.8.8",  -1, 47);
    public static final MinecraftVersion MC_1_8_7  = new MinecraftVersion("1.8.7",  -1, 47);
    public static final MinecraftVersion MC_1_8_6  = new MinecraftVersion("1.8.6",  -1, 47);
    public static final MinecraftVersion MC_1_8_5  = new MinecraftVersion("1.8.5",  -1, 47);
    public static final MinecraftVersion MC_1_8_4  = new MinecraftVersion("1.8.4",  -1, 47);
    public static final MinecraftVersion MC_1_8_3  = new MinecraftVersion("1.8.3",  -1, 47);
    public static final MinecraftVersion MC_1_8_2  = new MinecraftVersion("1.8.2",  -1, 47);
    public static final MinecraftVersion MC_1_8_1  = new MinecraftVersion("1.8.1",  -1, 47);
    public static final MinecraftVersion MC_1_8    = new MinecraftVersion("1.8",    -1, 47);

    public static final MinecraftVersion MC_1_7_10 = new MinecraftVersion("1.7.10", -1, 5);
    public static final MinecraftVersion MC_1_7_9  = new MinecraftVersion("1.7.9",  -1, 5);
    public static final MinecraftVersion MC_1_7_8  = new MinecraftVersion("1.7.8",  -1, 5);
    public static final MinecraftVersion MC_1_7_7  = new MinecraftVersion("1.7.7",  -1, 5);
    public static final MinecraftVersion MC_1_7_6  = new MinecraftVersion("1.7.6",  -1, 5);
    public static final MinecraftVersion MC_1_7_5  = new MinecraftVersion("1.7.5",  -1, 4);
    public static final MinecraftVersion MC_1_7_4  = new MinecraftVersion("1.7.4",  -1, 4);
    public static final MinecraftVersion MC_1_7_3  = new MinecraftVersion("1.7.3",  -1, 4);
    public static final MinecraftVersion MC_1_7_2  = new MinecraftVersion("1.7.2",  -1, 4);
    public static final MinecraftVersion MC_1_7_1  = new MinecraftVersion("1.7.1",  -1, 3);
    public static final MinecraftVersion MC_1_7    = new MinecraftVersion("1.7",    -1, 3);

    public static final MinecraftVersion MC_UNKNOWN = new MinecraftVersion("???",  -1, -1);

    public static final ImmutableList<MinecraftVersion> MINECRAFT_RELEASE_VERSIONS = ImmutableList.of(
        MC_1_7,
        MC_1_7_1,
        MC_1_7_2,
        MC_1_7_3,
        MC_1_7_4,
        MC_1_7_5,
        MC_1_7_6,
        MC_1_7_7,
        MC_1_7_8,
        MC_1_7_9,
        MC_1_7_10,

        MC_1_8,
        MC_1_8_1,
        MC_1_8_2,
        MC_1_8_3,
        MC_1_8_4,
        MC_1_8_5,
        MC_1_8_6,
        MC_1_8_7,
        MC_1_8_8,
        MC_1_8_9,

        MC_1_9,
        MC_1_9_1,
        MC_1_9_2,
        MC_1_9_3,
        MC_1_9_4,

        MC_1_10,
        MC_1_10_1,
        MC_1_10_2,

        MC_1_11,
        MC_1_11_1,
        MC_1_11_2,

        MC_1_12,
        MC_1_12_1,
        MC_1_12_2,

        MC_1_13,
        MC_1_13_1,
        MC_1_13_2,

        MC_1_14,
        MC_1_14_1,
        MC_1_14_2,
        MC_1_14_3,
        MC_1_14_4,

        MC_1_15,
        MC_1_15_1,
        MC_1_15_2,

        MC_1_16,
        MC_1_16_1,
        MC_1_16_2,
        MC_1_16_3,
        MC_1_16_4,
        MC_1_16_5,

        MC_1_17,
        MC_1_17_1,

        MC_1_18,
        MC_1_18_1,
        MC_1_18_2,

        MC_1_19,
        MC_1_19_1,
        MC_1_19_2,
        MC_1_19_3,
        MC_1_19_4,

        MC_1_20,
        MC_1_20_1,
        MC_1_20_2,
        MC_1_20_3,
        MC_1_20_4,
        MC_1_20_5,
        MC_1_20_6,

        MC_1_21,
        MC_1_21_1,
        MC_1_21_2,
        MC_1_21_3,
        MC_1_21_4,
        MC_1_21_5,
        MC_1_21_6,
        MC_1_21_7,
        MC_1_21_8
    );

    public static Optional<MinecraftVersion> getVersionByDataVersion(int dataVersion)
    {
        for (MinecraftVersion v : MINECRAFT_RELEASE_VERSIONS)
        {
            if (v.dataVersion == dataVersion)
            {
                return Optional.of(v);
            }
        }

        return Optional.empty();
    }

    public static MinecraftVersion getOrCreateVersionFromDataVersion(int dataVersion)
    {
        if (dataVersion <= 0)
        {
            return MC_UNKNOWN;
        }

        for (MinecraftVersion v : MINECRAFT_RELEASE_VERSIONS)
        {
            if (v.dataVersion == dataVersion)
            {
                return v;
            }

            if (v.dataVersion > dataVersion)
            {
                return new MinecraftVersion(v.displayName + " snapshot?", dataVersion, -1);
            }
        }

        return MC_UNKNOWN;
    }

    public static final MinecraftVersion CURRENT_VERSION = getOrCreateVersionFromDataVersion(((DataFixerMixin) GameWrap.getClient().getDataFixer()).malilib$getVersion());
}

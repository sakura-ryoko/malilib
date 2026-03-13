package fi.dy.masa.malilib.test.config.value;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;

/**
 * Post-ReWrite code
 */
public class TestOptions extends BaseOptionListConfigValue
{
    public static final TestOptions TEST_OPT_1 = new TestOptions("test_option_1", "malilib.name.test_options.option_1");
    public static final TestOptions TEST_OPT_2 = new TestOptions("test_option_2", "malilib.name.test_options.option_2");
    public static final TestOptions TEST_OPT_3 = new TestOptions("test_option_3", "malilib.name.test_options.option_3");
    public static final TestOptions TEST_OPT_4 = new TestOptions("test_option_4", "malilib.name.test_options.option_4");

    public static final ImmutableList<TestOptions> VALUES = ImmutableList.of(TEST_OPT_1, TEST_OPT_2, TEST_OPT_3, TEST_OPT_4);

    private TestOptions(String name, String translationKey)
    {
        super(name, translationKey);
    }
}

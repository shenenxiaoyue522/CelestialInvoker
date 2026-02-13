package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigRange;
import net.neoforged.neoforge.common.ModConfigSpec;

public class IntConfigEntry extends ConfigHolder<ModConfigSpec.IntValue> {

    private final int value;
    private final int min;
    private final int max;

    public IntConfigEntry(String id, String name, int value, int min, int max, String... text) {
        super(id, name, builder -> builder.defineInRange(id, value, min, max), text);
        this.value = value;
        this.min = min;
        this.max = max;
        getTexts().add(" Default: " + value);
        getTexts().add(new ConfigRange<>(Integer.class, min, max).toString());
    }

    public static IntConfigEntry define(String name, int value, int min, int max, String... text) {
        return new IntConfigEntry(StringHelper.toCamelCase(name), name, value, min, max, text);
    }

    public static IntConfigEntry defineInMaxRange(String name, int value, String... text) {
        return define(name, value, Integer.MIN_VALUE, Integer.MAX_VALUE, text);
    }

    public static IntConfigEntry defineFromZero(String name, int value, int max, String... text) {
            return define(name, value, 0, max, text);
        }

    public int get() {
        return this.value;
    }

    public int min() {
        return this.min;
    }

    public int max() {
        return this.max;
    }
}

package com.xiaoyue.celestial_invoker.config.value;

import com.xiaoyue.celestial_invoker.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;

public class IntConfigEntry extends ConfigHolder<ForgeConfigSpec.IntValue> {

    private final int value;
    private final int min;
    private final int max;

    public IntConfigEntry(String id, String name, int value, int min, int max, String... text) {
        super(id, name, builder -> builder.defineInRange(id, value, min, max), text);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public static IntConfigEntry define(String name, int value, int min, int max, String... text) {
        return new IntConfigEntry(StringCaser.toCamelCase(name), name, value, min, max, text);
    }

    public static IntConfigEntry defineInMaxRange(String name, int value, String... text) {
        return define(name, value, Integer.MIN_VALUE, Integer.MAX_VALUE, text);
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

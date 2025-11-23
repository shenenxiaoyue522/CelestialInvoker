package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;

public class DoubleConfigEntry extends ConfigHolder<ForgeConfigSpec.DoubleValue> {

    private final double value;
    private final double min;
    private final double max;

    public DoubleConfigEntry(String id, String name, double value, double min, double max, String... text) {
        super(id, name, builder -> builder.defineInRange(id, value, min, max), "Range: " + min + " ~ " + max, text);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public static DoubleConfigEntry define(String name, double value, double min, double max, String... text) {
        return new DoubleConfigEntry(StringCaser.toCamelCase(name), name, value, min, max, text);
    }

    public static DoubleConfigEntry defineSmallRange(String name, double value, String... text) {
        return define(name, value, 0.01, 10, text);
    }

    public static DoubleConfigEntry defineBigRange(String name, double value, String... text) {
        return define(name, value, 0.01, 1000, text);
    }

    public static DoubleConfigEntry defineFromMinUsable(String name, double value, double max, String... text) {
        return define(name, value, 0.01, max, text);
    }

    public static DoubleConfigEntry defineChance(String name, double value, String... text) {
        return define(name, value, 0.01, 1, text);
    }

    public static DoubleConfigEntry defineFromZero(String name, double value, double max, String... text) {
        return define(name, value, 0, max, text);
    }

    public static DoubleConfigEntry defineInMaxRange(String name, double value, String... text) {
        return define(name, value, Double.MIN_VALUE, Double.MAX_VALUE, text);
    }

    public double get() {
        return this.value;
    }

    public double min() {
        return this.min;
    }

    public double max() {
        return this.max;
    }

    public double perValue() {
        return this.value * (double) 100.0f;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public float floatMin() {
        return (float) this.min;
    }

    public float floatMax() {
        return (float) this.max;
    }
}

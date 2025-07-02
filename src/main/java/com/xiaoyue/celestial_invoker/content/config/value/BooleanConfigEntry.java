package com.xiaoyue.celestial_invoker.content.config.value;

import com.xiaoyue.celestial_invoker.content.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;

public class BooleanConfigEntry extends ConfigHolder<ForgeConfigSpec.BooleanValue> {
    private final boolean value;

    public BooleanConfigEntry(String id, String name, boolean value, String... text) {
        super(id, name, builder -> builder.define(id, value), text);
        this.value = value;
    }

    public static BooleanConfigEntry define(String name, boolean value, String... text) {
        return new BooleanConfigEntry(StringCaser.toCamelCase(name), name, value, text);
    }

    public boolean get() {
        return this.value;
    }
}

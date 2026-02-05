package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BooleanConfigEntry extends ConfigHolder<ModConfigSpec.BooleanValue> {
    private final boolean value;

    public BooleanConfigEntry(String id, String name, boolean value, String... text) {
        super(id, name, builder -> builder.define(id, value), text);
        this.value = value;
    }

    public static BooleanConfigEntry define(String name, boolean value, String... text) {
        return new BooleanConfigEntry(StringHelper.toCamelCase(name), name, value, text);
    }

    public boolean get() {
        return this.value;
    }
}

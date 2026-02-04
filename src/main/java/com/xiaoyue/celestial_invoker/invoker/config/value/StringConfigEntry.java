package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.neoforged.neoforge.common.ModConfigSpec;

public class StringConfigEntry extends ConfigHolder<ModConfigSpec.ConfigValue<String>> {
    private final String value;

    public StringConfigEntry(String id, String name, String value, String... text) {
        super(id, name, builder -> builder.define(id, value), text);
        this.value = value;
    }

    public static StringConfigEntry define(String name, String value, String... text) {
        return new StringConfigEntry(StringCaser.toCamelCase(name), name, value, text);
    }

    public String get() {
        return this.value;
    }
}

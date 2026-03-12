package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import net.minecraftforge.common.ForgeConfigSpec;

public class StringConfigEntry extends ConfigHolder<ForgeConfigSpec.ConfigValue<String>> {
    private final String value;

    public StringConfigEntry(String id, String name, String value, String... text) {
        super(id, name, builder -> builder.define(id, value), text);
        this.value = value;
    }

    public static StringConfigEntry define(String name, String value, String... text) {
        return new StringConfigEntry(StringHelper.toCamelCase(name), name, value, text);
    }

    public String get() {
        return this.value;
    }
}

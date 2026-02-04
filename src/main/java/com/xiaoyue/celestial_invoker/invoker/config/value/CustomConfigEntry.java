package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.content.common.helper.StringCaser;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CustomConfigEntry<T> extends ConfigHolder<ModConfigSpec.ConfigValue<T>> {
    private final T value;

    public CustomConfigEntry(String id, String name, T value, String... text) {
        super(id, name, builder -> builder.define(id, value), text);
        this.value = value;
    }

    public static <T> CustomConfigEntry<T> define(String name, T value, String... text) {
        return new CustomConfigEntry<>(StringCaser.toCamelCase(name), name, value, text);
    }

    public T get() {
        return this.value;
    }
}

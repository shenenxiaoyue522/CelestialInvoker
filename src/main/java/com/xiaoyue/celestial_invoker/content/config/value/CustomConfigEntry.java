package com.xiaoyue.celestial_invoker.content.config.value;

import com.xiaoyue.celestial_invoker.content.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;

public class CustomConfigEntry<T> extends ConfigHolder<ForgeConfigSpec.ConfigValue<T>> {
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

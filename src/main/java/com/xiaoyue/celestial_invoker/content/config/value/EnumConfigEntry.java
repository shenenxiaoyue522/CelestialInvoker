package com.xiaoyue.celestial_invoker.content.config.value;

import com.xiaoyue.celestial_invoker.content.config.ConfigHolder;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigHolder<ForgeConfigSpec.EnumValue<T>> {
    public final T value;

    public EnumConfigEntry(String id, String name, T value, String... text) {
        super(id, name, builder -> builder.defineEnum(id, value), text);
        this.value = value;
    }

    public static <T extends Enum<T>> EnumConfigEntry<T> define(String name, T value, String... text) {
        return new EnumConfigEntry<>(StringCaser.toCamelCase(name), name, value, text);
    }

    public T get() {
        return this.value;
    }
}

package com.xiaoyue.celestial_invoker.invoker.config.value;

import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigHolder<ModConfigSpec.EnumValue<T>> {
    public final T value;

    public EnumConfigEntry(String id, String name, T value, String... text) {
        super(id, name, builder -> builder.defineEnum(id, value), text);
        this.value = value;
        getTexts().add(" Default: " + value);
    }

    public static <T extends Enum<T>> EnumConfigEntry<T> define(String name, T value, String... text) {
        return new EnumConfigEntry<>(StringHelper.toCamelCase(name), name, value, text);
    }

    public T get() {
        return this.value;
    }
}

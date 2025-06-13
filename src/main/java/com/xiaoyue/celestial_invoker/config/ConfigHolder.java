package com.xiaoyue.celestial_invoker.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ConfigHolder<C> {

    public static final ConfigHolderMap CACHE = new ConfigHolderMap();

    private final String id, name;
    private final String[] texts;
    private final Function<ForgeConfigSpec.Builder, C> action;
    public C entry;

    public ConfigHolder(String id, String name, Function<ForgeConfigSpec.Builder, C> action, String... text) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.texts = text;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTexts() {
        return Arrays.stream(this.texts).toList();
    }

    public void apply(ForgeConfigSpec.Builder builder, ConfigHolderMap map, String title) {
        this.getTexts().forEach((text) -> {
            builder.comment(text);
            map.TEXT_MAP.put(title + this.id, this);
        });
        this.entry = (C)this.action.apply(builder);
    }
}

package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ConfigHolder<C> {

    private final String id, name, rangeText;
    private final List<String> texts = new ArrayList<>();
    private final Function<ForgeConfigSpec.Builder, C> action;
    public C entry;

    public ConfigHolder(String id, String name, Function<ForgeConfigSpec.Builder, C> action, String rangeText, String... text) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.rangeText = rangeText;
        this.texts.addAll(Arrays.stream(text).toList());
    }

    public ConfigHolder(String id, String name, Function<ForgeConfigSpec.Builder, C> action, String... text) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.rangeText = "";
        this.texts.addAll(Arrays.stream(text).toList());
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getRangeText() {
        return rangeText;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void apply(ForgeConfigSpec.Builder builder, ConfigHolderMap map, String title) {
        this.getTexts().forEach((text) -> {
            builder.comment(text);
            map.TEXT_MAP.put(title + this.id, this);
        });
        this.entry = this.action.apply(builder);
    }
}

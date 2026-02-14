package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ConfigHolder<C> {

    private final String id, name;
    private final List<String> texts = new ArrayList<>();
    private final Function<ForgeConfigSpec.Builder, C> action;
    public C entry;

    public ConfigHolder(String id, String name, Function<ForgeConfigSpec.Builder, C> action, String... text) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.texts.addAll(Arrays.stream(text).toList());
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void apply(ForgeConfigSpec.Builder builder) {
        this.getTexts().forEach(builder::comment);
        this.entry = this.action.apply(builder);
    }
}

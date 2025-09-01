package com.xiaoyue.celestial_invoker.invoker.config;

import com.xiaoyue.celestial_invoker.simple.SimpleCompare;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigHolderMap {

    private final List<Function<ForgeConfigSpec.Builder, ?>> extraConfigs = new ArrayList<>();

    private Map<String, Map<String, ConfigHolder<?>>> COMMON_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> SERVER_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> CLIENT_MAP = new TreeMap<>();

    public final Map<String, String> TITLE_MAP = new TreeMap<>();
    public final Map<String, ConfigHolder<?>> TEXT_MAP = new TreeMap<>();

    public ConfigHolderMap compare(BiFunction<String, String, Integer> func) {
        this.COMMON_MAP = new TreeMap<>(new SimpleCompare(func));
        this.SERVER_MAP = new TreeMap<>(new SimpleCompare(func));
        this.CLIENT_MAP = new TreeMap<>(new SimpleCompare(func));
        return this;
    }

    public void initConfigs(ModConfig.Type type) {
        this.initConfigs(type, ConfigLoader.getConfigName(type));
    }

    public void initConfigs(ModConfig.Type type, String fileName) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        String title = ConfigLoader.getActiveModId() + ".configuration.";
        TITLE_MAP.put(title + "title", StringCaser.caseSpaceCapitalize(title));
        this.applyConfig(type, builder, title);
        if (!extraConfigs.isEmpty()) {
            extraConfigs.forEach(builder::configure);
        }
        ModLoadingContext.get().registerConfig(type, builder.build(), fileName);
        String key = title + "section." + fileName.replace("-", ".");
        TITLE_MAP.put(key, ConfigLoader.getConfigTypeText(type));
    }

    public ConfigHolderMap addExtra(Function<ForgeConfigSpec.Builder, ?> extraConfig) {
        this.extraConfigs.add(extraConfig);
        return this;
    }

    public void applyConfig(ModConfig.Type type, ForgeConfigSpec.Builder builder, String title) {
        this.getMap(type).forEach((key, map) -> {
            if (key != null && !key.isEmpty()) {
                builder.push(key);
            }
            map.forEach((id, config) -> config.apply(builder, this, title));
            if (key != null && !key.isEmpty()) {
                builder.pop();
            }
        });
    }

    public Map<String, Map<String, ConfigHolder<?>>> getMap(ModConfig.Type type) {
        if (type == null) {
            return this.COMMON_MAP;
        } else {
            return switch (type) {
                case COMMON -> this.COMMON_MAP;
                case SERVER -> this.SERVER_MAP;
                case CLIENT -> this.CLIENT_MAP;
            };
        }
    }

    public void addConfig(String category, ConfigHolder<?> holder, ModConfig.Type type) {
        Map<String, Map<String, ConfigHolder<?>>> map = this.getMap(type);
        if (map.containsKey(category)) {
            (map.get(category)).put(holder.getId(), holder);
        } else {
            Map<String, ConfigHolder<?>> defMap = new TreeMap<>();
            defMap.put(holder.getId(), holder);
            map.put(category, defMap);
        }
    }
}

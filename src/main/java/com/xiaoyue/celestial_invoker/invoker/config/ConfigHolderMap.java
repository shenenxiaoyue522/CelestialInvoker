package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ConfigHolderMap {

    public final String modid;

    private final Map<String, Map<String, ConfigHolder<?>>> COMMON_MAP = new TreeMap<>(itemTogglesFinal());
    private final Map<String, Map<String, ConfigHolder<?>>> SERVER_MAP = new TreeMap<>(itemTogglesFinal());
    private final Map<String, Map<String, ConfigHolder<?>>> CLIENT_MAP = new TreeMap<>(itemTogglesFinal());

    public ConfigPath configPath = null;

    public ConfigHolderMap(String modid) {
        this.modid = modid;
    }

    public ConfigPath initCelestialConfigs(ModConfig.Type type) {
        return this.initConfigs(type, "celestial_configs/" + ConfigLoader.getConfigName(type));
    }

    public ConfigPath initConfigs(ModConfig.Type type) {
        return this.initConfigs(type, ConfigLoader.getConfigName(type));
    }

    public ConfigPath initConfigs(ModConfig.Type type, String fileName) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.applyConfig(type, builder);
        ModLoadingContext.get().registerConfig(type, builder.build(), fileName);
        ConfigPath path = new ConfigPath(type, fileName);
        this.configPath = path;
        return path;
    }

    public final ConfigHolderMap addExtra(Consumer<ConfigHolderMap> map) {
        map.accept(this);
        return this;
    }

    public void applyConfig(ModConfig.Type type, ForgeConfigSpec.Builder builder) {
        this.getMap(type).forEach((key, map) -> {
            if (key != null && !key.isEmpty()) {
                builder.push(key);
            }
            map.forEach((id, config) -> config.apply(builder));
            if (key != null && !key.isEmpty()) {
                builder.pop();
            }
        });
    }

    public Map<String, Map<String, ConfigHolder<?>>> getMap(@Nullable ModConfig.Type type) {
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

    public Comparator<String> itemTogglesFinal() {
        return (s1, s2) -> {
            if (s1.equals("itemToggles")) return 1;
            if (s2.equals("itemToggles")) return -1;
            return s1.compareTo(s2);
        };
    }

    public record ConfigPath(ModConfig.Type type, String path) {

    }
}

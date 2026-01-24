package com.xiaoyue.celestial_invoker.invoker.config;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.xiaoyue.celestial_invoker.simple.QuickCompare;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigHolderMap {

    private final Map<ModConfig.Type, List<Function<ForgeConfigSpec.Builder, ?>>> extraConfigs = new HashMap<>();

    private Map<String, Map<String, ConfigHolder<?>>> COMMON_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> SERVER_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> CLIENT_MAP = new TreeMap<>();

    public final Map<String, String> TITLE_MAP = new TreeMap<>();
    public final Map<String, ConfigHolder<?>> TEXT_MAP = new TreeMap<>();

    public ConfigPath configPath = null;

    public ConfigHolderMap compare(BiFunction<String, String, Integer> func) {
        this.COMMON_MAP = new TreeMap<>(new QuickCompare(func));
        this.SERVER_MAP = new TreeMap<>(new QuickCompare(func));
        this.CLIENT_MAP = new TreeMap<>(new QuickCompare(func));
        return this;
    }

    public ConfigPath initCelestialConfigs(ModConfig.Type type) {
        return this.initConfigs(type, "celestial_configs/" + ConfigLoader.getConfigName(type));
    }

    public ConfigPath initConfigs(ModConfig.Type type) {
        return this.initConfigs(type, ConfigLoader.getConfigName(type));
    }

    public ConfigPath initConfigs(ModConfig.Type type, String fileName) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        String title = ConfigLoader.getActiveModId() + ".configuration.";
        TITLE_MAP.put(title + "title", StringCaser.caseSpaceCapitalize(title));
        this.applyConfig(type, builder, title);
        if (!extraConfigs.isEmpty()) {
            extraConfigs.forEach((extraType, func) -> {
                if (extraType.equals(type)) func.forEach(builder::configure);
            });
        }
        ModLoadingContext.get().registerConfig(type, builder.build(), fileName);
        String key = title + "section." + fileName.replace("-", ".");
        TITLE_MAP.put(key, ConfigLoader.getConfigTypeText(type));
        ConfigPath path = new ConfigPath(type, fileName);
        this.configPath = path;
        return path;
    }

    @SafeVarargs
    public final ConfigHolderMap addExtra(ModConfig.Type type, Function<ForgeConfigSpec.Builder, ?>... extraConfig) {
        this.extraConfigs.put(type, Arrays.stream(extraConfig).toList());
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

    public void addConfigDesc(LanguageProvider pvd) {
        ConfigHolder.CACHE.TEXT_MAP.forEach((key, config) -> {
            pvd.add(key, config.getName());
            StringBuilder finalText = new StringBuilder(config.getTexts().get(0));
            if (!config.getRangeText().isEmpty()) {
                config.getTexts().add(config.getRangeText());
            }
            if (config.getTexts().size() > 1) {
                for (int i = 1; i < config.getTexts().size(); i++) {
                    finalText.append("/n ").append(config.getTexts().get(i));
                }
            }
            pvd.add(key + ".tooltip", finalText.toString());
        });
    }

    public void generatorLang(AbstractRegistrate<?> registrate) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registrate.addDataGenerator(ProviderType.LANG, pvd -> {
            TITLE_MAP.forEach(pvd::add);
            addConfigDesc(pvd);
        }));

    }

    public record ConfigPath(ModConfig.Type type, String path) {

    }
}

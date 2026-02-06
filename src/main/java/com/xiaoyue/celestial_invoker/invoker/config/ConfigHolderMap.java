package com.xiaoyue.celestial_invoker.invoker.config;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.xiaoyue.celestial_invoker.content.common.DirectCompare;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigHolderMap {

    private final Map<ModConfig.Type, List<Function<ModConfigSpec.Builder, ?>>> EXTRA_CONFIGS = new HashMap<>();

    private Map<String, Map<String, ConfigHolder<?>>> COMMON_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> SERVER_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> CLIENT_MAP = new TreeMap<>();
    private Map<String, Map<String, ConfigHolder<?>>> STARTUP_MAP = new TreeMap<>();

    public final Map<String, String> TITLE_MAP = new TreeMap<>();
    public final Map<String, ConfigHolder<?>> TEXT_MAP = new TreeMap<>();

    public Map<ModConfig.Type, String> configPath = new HashMap<>();

    public ConfigHolderMap compare(BiFunction<String, String, Integer> func) {
        this.COMMON_MAP = new TreeMap<>(new DirectCompare(func));
        this.SERVER_MAP = new TreeMap<>(new DirectCompare(func));
        this.CLIENT_MAP = new TreeMap<>(new DirectCompare(func));
        this.STARTUP_MAP = new TreeMap<>(new DirectCompare(func));
        return this;
    }

    public void initCelestialConfigs(ModConfig.Type type) {
        this.initConfigs(type, "celestial_configs/" + ConfigLoader.getConfigName(type));
    }

    public void initConfigs(ModConfig.Type type) {
        this.initConfigs(type, ConfigLoader.getConfigName(type));
    }

    public void initConfigs(ModConfig.Type type, String fileName) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String title = SimpleInvoker.getActiveModId() + ".configuration.";
        TITLE_MAP.put(title + "title", StringHelper.caseSpaceCapitalize(title));
        this.applyConfig(type, builder, title);
        if (!EXTRA_CONFIGS.isEmpty()) {
            EXTRA_CONFIGS.forEach((extraType, func) -> {
                if (extraType.equals(type)) func.forEach(builder::configure);
            });
        }
        SimpleInvoker.getActiveMod().registerConfig(type, builder.build(), fileName);
        String key = title + "section." + fileName.replace("-", ".");
        TITLE_MAP.put(key, ConfigLoader.getConfigTypeText(type));
        configPath.put(type, fileName);
    }

    @SafeVarargs
    public final ConfigHolderMap addExtra(ModConfig.Type type, Function<ModConfigSpec.Builder, ?>... extraConfig) {
        this.EXTRA_CONFIGS.put(type, Arrays.stream(extraConfig).toList());
        return this;
    }

    public void applyConfig(ModConfig.Type type, ModConfigSpec.Builder builder, String title) {
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
            return this.SERVER_MAP;
        } else {
            return switch (type) {
                case COMMON -> this.COMMON_MAP;
                case SERVER -> this.SERVER_MAP;
                case CLIENT -> this.CLIENT_MAP;
                case STARTUP -> this.STARTUP_MAP;
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
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registrate.addDataGenerator(ProviderType.LANG, pvd -> {
            TITLE_MAP.forEach(pvd::add);
            addConfigDesc(pvd);
        }));
    }
}

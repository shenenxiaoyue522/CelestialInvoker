package com.xiaoyue.celestial_invoker.invoker.config;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ConfigHolderMap {

    private final String modid;
    private boolean allowTitle = true;

    private final Map<String, Map<String, ConfigHolder<?>>> COMMON_MAP = new TreeMap<>();
    private final Map<String, Map<String, ConfigHolder<?>>> SERVER_MAP = new TreeMap<>();
    private final Map<String, Map<String, ConfigHolder<?>>> CLIENT_MAP = new TreeMap<>();
    private final Map<String, Map<String, ConfigHolder<?>>> STARTUP_MAP = new TreeMap<>();

    public final Map<String, String> TITLE_MAP = new TreeMap<>();
    public final Map<String, ConfigHolder<?>> TEXT_MAP = new TreeMap<>();

    public Map<ModConfig.Type, String> configPath = new HashMap<>();

    public ConfigHolderMap(String modid) {
        this.modid = modid;
    }

    public ConfigHolderMap noTitle() {
        allowTitle = false;
        return this;
    }

    public ConfigHolderMap initCelestialConfigs(ModConfig.Type type) {
        return this.initConfigs(type, "celestial_configs/" + ConfigLoader.getConfigName(type, modid));
    }

    public ConfigHolderMap initConfigs(ModConfig.Type type) {
        return this.initConfigs(type, ConfigLoader.getConfigName(type, modid));
    }

    public ConfigHolderMap initConfigs(ModConfig.Type type, String name) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String title = modid + ".configuration.";
        this.applyConfig(type, builder, title);
        ModContainer mod = SimpleInvoker.getMod(modid);
        mod.registerConfig(type, builder.build(), name);
        String key = title + "section." + name.replaceAll("[-_/]", ".");
        if (allowTitle) {
            TITLE_MAP.put(title + "title", StringHelper.caseSpaceCapitalize(title));
            TITLE_MAP.put(key, ConfigLoader.getConfigTypeText(type, modid));
        }
        configPath.put(type, name);
        ConfigLoader.initConfigScreen(mod);
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
        String key = modid + ".configuration." + category;
        Map<String, Map<String, ConfigHolder<?>>> map = this.getMap(type);
        if (map.containsKey(category)) {
            (map.get(category)).put(holder.getId(), holder);
        } else {
            TITLE_MAP.put(key, StringHelper.camelToCapitalizedWords(key));
            Map<String, ConfigHolder<?>> defMap = new TreeMap<>();
            defMap.put(holder.getId(), holder);
            map.put(category, defMap);
        }
    }

    public void addConfigDesc(LanguageProvider pvd) {
        TEXT_MAP.forEach((key, config) -> {
            pvd.add(key, config.getName());
            if (!config.getTexts().isEmpty()) {
                StringBuilder finalText = new StringBuilder(config.getTexts().getFirst());
                for (int i = 1; i < config.getTexts().size(); i++) {
                    finalText.append("\n");
                    finalText.append(config.getTexts().get(i));
                }
                pvd.add(key + ".tooltip", finalText.toString());
            }
        });
    }

    public ConfigHolderMap genLang(AbstractRegistrate<?> registrate) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registrate.addDataGenerator(ProviderType.LANG, pvd -> {
            TITLE_MAP.forEach(pvd::add);
            addConfigDesc(pvd);
        }));
        return this;
    }
}

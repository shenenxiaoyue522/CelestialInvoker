package com.xiaoyue.celestial_invoker.invoker.config.wrapper;

import com.tterrag.registrate.AbstractRegistrate;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import com.xiaoyue.celestial_invoker.invoker.config.ConfigLoader;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigWrapper extends ModConfigSpec.Builder {

    private static final ConcurrentHashMap<String, ConfigWrapper> CONFIG_MAP = new ConcurrentHashMap<>();

    @Nullable
    public static ConfigWrapper get(String path) {
        return CONFIG_MAP.get(path);
    }

    public String file = "";
    public String path = "";
    private ModConfig.Type type;
    private IConfigSpec spec;

    public ModConfig.Type getType() {
        return type;
    }

    public IConfigSpec getSpec() {
        return spec;
    }

    public String getPath() {
        return path;
    }

    public void setCelestial() {
        file("celestial_configs/");
    }

    public void file(String str) {
        this.file = str;
    }

    public static void addTitleTooltip(AbstractRegistrate<?> registrate) {
        String title = registrate.getModid() + ".configuration.";
        registrate.addRawLang(title + "title", StringHelper.caseSpaceCapitalize(title));
    }

    public static <T extends ConfigWrapper> T init(AbstractRegistrate<?> registrate, ModConfig.Type type, Function<Builder, T> factory) {
        var builder = new Builder(registrate);
        var ans = factory.apply(builder);
        var spec = builder.build();
        init(registrate, type, spec, ans);
        return ans;
    }

    private static void init(AbstractRegistrate<?> registrate, ModConfig.Type type, IConfigSpec spec, ConfigWrapper wrapper) {
        ModContainer mod = SimpleInvoker.getMod(registrate.getModid());
        String path = wrapper.file + mod.getModId() + "-" + type.extension() + ".toml";
        mod.registerConfig(type, spec, path);
        wrapper.path = path;
        wrapper.type = type;
        wrapper.spec = spec;
        CONFIG_MAP.put(path, wrapper);
        ConfigLoader.initConfigScreen(mod);
        String title = registrate.getModid() + ".configuration.";
        String key = title + "section." + path.replaceAll("[-_/]", ".");
        registrate.addRawLang(key, ConfigLoader.getConfigTypeText(type, registrate.getModid()));
    }

    public static class Builder extends ModConfigSpec.Builder {

        private final AbstractRegistrate<?> registrate;

        private String name = null;

        Builder(AbstractRegistrate<?> reg) {
            this.registrate = reg;
        }

        @Override
        @Deprecated
        public Builder push(String path) {
            super.push(path);
            return this;
        }

        @Override
        @Deprecated
        public Builder push(List<String> path) {
            super.push(path);
            return this;
        }

        public Builder push(String path, String name) {
            registrate.addRawLang(registrate.getModid() + ".configuration." + path, name);
            super.push(path);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public <T> ModConfigSpec.ConfigValue<T> define(List<String> path, ModConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
            if (name == null) throw new IllegalStateException("Empty name is not allowed");
            registrate.addRawLang(registrate.getModid() + ".configuration." + path.getLast(), name);
            String comment = value.getComment();
            registrate.addRawLang(registrate.getModid() + ".configuration." + path.getLast() + ".tooltip", comment == null ? "" : comment);
            return super.define(path, value, defaultSupplier);
        }
    }
}

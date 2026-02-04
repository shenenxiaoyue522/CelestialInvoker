package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ConfigLangGen extends LanguageProvider {
    public ConfigLangGen(PackOutput output, String modid) {
        super(output, modid, "en_us");
    }

    protected void addTranslations() {
        ConfigHolder.CACHE.TITLE_MAP.forEach(this::add);
        ConfigHolder.CACHE.addConfigDesc(this);
        addNewTranslations();
    }

    protected void addNewTranslations() {
    }
}

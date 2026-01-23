package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ConfigLangGen extends LanguageProvider {
    public ConfigLangGen(PackOutput output, String modid) {
        super(output, modid, "en_us");
    }

    protected void addTranslations() {
        ConfigHolder.CACHE.TITLE_MAP.forEach(this::add);
        ConfigHolder.CACHE.TEXT_MAP.forEach((key, config) -> {
            add(key, config.getName());
            StringBuilder finalText = new StringBuilder(config.getTexts().get(0));
            if (!config.getRangeText().isEmpty()) {
                config.getTexts().add(config.getRangeText());
            }
            if (config.getTexts().size() > 1) {
                for (int i = 1; i < config.getTexts().size(); i++) {
                    finalText.append("/n ").append(config.getTexts().get(i));
                }
            }
            add(key + ".tooltip", finalText.toString());
        });
    }
}

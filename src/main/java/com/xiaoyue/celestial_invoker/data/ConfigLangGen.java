package com.xiaoyue.celestial_invoker.data;

import com.xiaoyue.celestial_invoker.invoker.config.ConfigHolder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ConfigLangGen extends LanguageProvider {
    public ConfigLangGen(PackOutput output, String modid) {
        super(output, modid, "en_us");
    }

    protected void addTranslations() {
        ConfigHolder.CACHE.TITLE_MAP.forEach(this::add);
        ConfigHolder.CACHE.TEXT_MAP.forEach((key, config) -> {
            StringBuilder finalText = new StringBuilder(config.getTexts().get(0));
            add(key, config.getName());
            if (config.getTexts().size() > 1) {
                for (int i = 1; i < config.getTexts().size(); i++) {
                    finalText.append("/n ").append(config.getTexts().get(i));
                }
            }
            add(key + ".tooltip", finalText.toString());
        });
    }
}

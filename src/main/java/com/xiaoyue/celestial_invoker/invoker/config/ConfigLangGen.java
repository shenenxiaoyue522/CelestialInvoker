package com.xiaoyue.celestial_invoker.invoker.config;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ConfigLangGen extends LanguageProvider {
    public final ConfigHolderMap map;

    public ConfigLangGen(PackOutput output, String modid, ConfigHolderMap map) {
        super(output, modid, "en_us");
        this.map = map;
    }

    protected void addTranslations() {
        map.TITLE_MAP.forEach(this::add);
        map.addConfigDesc(this);
        addNewTranslations();
    }

    protected void addNewTranslations() {
    }
}

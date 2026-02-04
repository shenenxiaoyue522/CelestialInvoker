package com.xiaoyue.celestial_invoker.invoker.tooltip;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TooltipLangGen extends LanguageProvider {
    private final TooltipLoader loader;

    public TooltipLangGen(PackOutput output, TooltipLoader loader) {
        super(output, loader.modid, "en_us");
        this.loader = loader;
    }

    @Override
    protected final void addTranslations() {
        loader.map.forEach((key, entry) -> add(key, entry.tooltip));
        addNewTranslations();
    }

    protected void addNewTranslations() {
    }
}

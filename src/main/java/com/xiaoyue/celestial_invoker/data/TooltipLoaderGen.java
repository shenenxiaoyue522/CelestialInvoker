package com.xiaoyue.celestial_invoker.data;

import com.xiaoyue.celestial_invoker.content.tooltip.TooltipLoader;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class TooltipLoaderGen extends LanguageProvider {
    private final TooltipLoader loader;

    public TooltipLoaderGen(PackOutput output, TooltipLoader loader) {
        super(output, loader.modid, "en_us");
        this.loader = loader;
    }

    @Override
    protected void addTranslations() {
        loader.map.forEach((key, entry) -> add(key, entry.tooltip));
    }
}

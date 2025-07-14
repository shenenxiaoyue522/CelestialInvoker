package com.xiaoyue.celestial_invoker.content.config;

import com.xiaoyue.celestial_invoker.simple.SimpleInvoker;
import com.xiaoyue.celestial_invoker.simple.StringCaser;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.Locale;

public class ConfigLoader {

    public static String getConfigTypeText(ModConfig.Type type) {
        String modId = getActiveModId();
        return StringCaser.caseSpaceCapitalize(modId + "." + type.extension() + ".configuration");
    }

    public static String getActiveModId() {
        return ModLoadingContext.get().getActiveContainer().getModId();
    }

    public static String getSpaceCaseModId() {
        return StringCaser.caseSpaceCapitalize(getActiveModId());
    }

    public static String getConfigName(ModConfig.Type type) {
        return String.format(Locale.ROOT, "%s-%s.toml", getActiveModId(), type.extension());
    }

    public static ConfigHolder<?> cast(Object obj) {
        return (ConfigHolder<?>) obj;
    }

    public static ConfigHolderMap mapConfig(String modid) {
        try {
            for(ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, ConfigHolderEntry.class)) {
                String category = (String) data.annotationData().getOrDefault("category", "");
                ModConfig.Type type = (ModConfig.Type) data.annotationData().getOrDefault("type", ModConfig.Type.COMMON);
                Type clazz = data.clazz();
                String name = data.memberName();
                Class<?> annoCls = Class.forName(clazz.getClassName());
                ConfigHolder<?> holder = cast(annoCls.getDeclaredField(name).get(null));
                ConfigHolder.CACHE.addConfig(category, holder, type);
            }
            return ConfigHolder.CACHE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

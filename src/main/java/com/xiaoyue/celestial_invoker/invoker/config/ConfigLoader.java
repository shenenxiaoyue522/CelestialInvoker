package com.xiaoyue.celestial_invoker.invoker.config;

import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import com.xiaoyue.celestial_invoker.content.common.helper.StringHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Locale;

public class ConfigLoader {

    public static String getConfigTypeText(ModConfig.Type type, String modid) {
        return StringHelper.caseSpaceCapitalize(modid + "." + type.extension() + ".configuration");
    }

    public static void initConfigScreen(ModContainer mod) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> mod.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new));
    }

    public static String getSpaceCaseModId(String modid) {
        return StringHelper.caseSpaceCapitalize(modid);
    }

    public static String getConfigName(ModConfig.Type type, String modid) {
        return String.format(Locale.ROOT, "%s-%s.toml", modid, type.extension());
    }

    public static String getCelestialConfigName(ModConfig.Type type) {
        return String.format(Locale.ROOT, "celestial_configs/" + "%s-%s.toml", SimpleInvoker.getActiveModId(), type.extension());
    }

    public static ConfigHolder<?> cast(Object obj) {
        return (ConfigHolder<?>) obj;
    }

    public static ConfigHolderMap mapConfig(String modid) {
        ConfigHolderMap map = new ConfigHolderMap(modid);
        try {
            for(ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, ConfigHolderEntry.class)) {
                String[] category = (String[]) data.annotationData().getOrDefault("category", "");
                ModConfig.Type type = (ModConfig.Type) data.annotationData().getOrDefault("type", ModConfig.Type.COMMON);
                Type clazz = data.clazz();
                String name = data.memberName();
                Class<?> annoCls = Class.forName(clazz.getClassName());
                Field field = annoCls.getDeclaredField(name);
                field.setAccessible(true);
                ConfigHolder<?> holder = cast(field.get(null));
                map.addConfig(category, holder, type);
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

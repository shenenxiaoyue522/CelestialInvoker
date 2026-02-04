package com.xiaoyue.celestial_invoker.simple;

import com.xiaoyue.celestial_invoker.invoker.handler.ForceLoadClass;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SimpleInvoker {

    public static Object getObj(Field field) {
        try {
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getActiveModId() {
        return getActiveMod().getModId();
    }

    public static ModContainer getActiveMod() {
        return ModLoadingContext.get().getActiveContainer();
    }

    public static void postClassLoader(String modid, String type) {
        for (ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, ForceLoadClass.class)) {
            String dataType = (String) data.annotationData().getOrDefault("type", "all");
            if (dataType.equals(type)) {
                try {
                    Class.forName(data.clazz().getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void invokeModMethod(String modid, Class<?> type, Object... args) {
        try {
            for(ModFileScanData.AnnotationData data : getModAnno(modid, type)) {
                Class<?> annoClass = Class.forName(data.clazz().getClassName());
                annoClass.getMethod(data.memberName().split("\\(")[0]).invoke(null, args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ModFileScanData.AnnotationData> getModAnno(String modid, Class<?> type) {
        List<ModFileScanData.AnnotationData> list = new ArrayList<>();
        ModFileScanData file = ModList.get().getModFileById(modid).getFile().getScanResult();
        for(ModFileScanData.AnnotationData data : file.getAnnotations()) {
            if (data.annotationType().getClassName().equals(type.getName())) {
                list.add(data);
            }
        }
        return list;
    }
}

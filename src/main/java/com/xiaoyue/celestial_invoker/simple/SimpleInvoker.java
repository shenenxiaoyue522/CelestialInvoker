package com.xiaoyue.celestial_invoker.simple;

import com.xiaoyue.celestial_invoker.invoker.config.ConfigLoader;
import com.xiaoyue.celestial_invoker.invoker.provider.CelestialStatesProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

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

    public static void invokeAllMethod(Class<?> type, Object... args) {
        try {
            for(ModFileScanData.AnnotationData data : getAllAnno(type)) {
                Class<?> annoClass = Class.forName(data.clazz().getClassName());
                annoClass.getMethod(data.memberName().split("\\(")[0]).invoke(null, args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public static List<ModFileScanData.AnnotationData> getAllAnno(Class<?> type) {
        List<ModFileScanData.AnnotationData> list = new ArrayList<>();
        CelestialStatesProvider.getScanDataList().forEach(file -> list.addAll(file.getAnnotations()
                .stream().filter(anno -> anno.annotationType().getClassName().equals(type.getName())).toList()));
        return list;
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

    public static IModFileInfo getLoadingFiles() {
        return ModList.get().getModFileById(ConfigLoader.getActiveModId());
    }
}

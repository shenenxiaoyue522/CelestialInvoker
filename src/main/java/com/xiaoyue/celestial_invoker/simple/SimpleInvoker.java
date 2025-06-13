package com.xiaoyue.celestial_invoker.simple;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.ArrayList;
import java.util.List;

public class SimpleInvoker {

    public static void invokeModMethod(String modid, Class<?> type) {
        try {
            for(ModFileScanData.AnnotationData data : getModAnno(modid, type)) {
                Class<?> annoClass = Class.forName(data.clazz().getClassName());
                annoClass.getMethod(data.memberName().split("\\(")[0]).invoke((Object)null);
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

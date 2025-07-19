package com.xiaoyue.celestial_invoker.invoker.tooltip;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.xiaoyue.celestial_invoker.data.TooltipLoaderGen;
import com.xiaoyue.celestial_invoker.simple.SimpleInvoker;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class TooltipLoader {

    public final Map<String, TooltipEntry> map = new TreeMap<>();
    public final String modid;

    public TooltipLoader(String modid) {
        this.modid = modid;
        this.loadTooltips();
    }

    public void generator(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(event.includeClient(), new TooltipLoaderGen(gen.getPackOutput(), this));
    }

    public void generator(RegistrateLangProvider pvd) {
        map.forEach((key, entry) -> pvd.add(key, entry.tooltip));
    }

    public void loadTooltips() {
        try {
            for (ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, SubscribeTooltip.class)) {
                String tooltipKey = "";
                String key = (String) data.annotationData().getOrDefault("key", "");
                String id = (String) data.annotationData().getOrDefault("id", "");
                if (!key.isEmpty()) {
                    tooltipKey = key;
                } else if (id != null) {
                    tooltipKey = "tooltip." + modid + "." + id;
                }
                Type clazz = data.clazz();
                Class<?> annoCls = Class.forName(clazz.getClassName());
                Field field = annoCls.getDeclaredField(data.memberName());
                if (field.get(null) instanceof TooltipEntry entry) {
                    entry.setKey(tooltipKey);
                    map.put(tooltipKey, entry);
                } else if (field.get(null) instanceof TooltipHolder holder) {
                    for (int i = 0; i < holder.size(); i++) {
                        TooltipEntry entry = holder.get(i);
                        tooltipKey = tooltipKey + "_" + i;
                        entry.setKey(tooltipKey);
                        map.put(tooltipKey, entry);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

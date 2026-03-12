package com.xiaoyue.celestial_invoker.invoker.tooltip;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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

    private void trySetKey(String key, TooltipEntry entry) {
        if (!key.isEmpty()) {
            entry.setKey(key);
        }
    }

    public void generator(AbstractRegistrate<?> registrate) {
        registrate.addDataGenerator(ProviderType.LANG, pvd ->
                map.forEach((key, entry) -> pvd.add(key, entry.tooltip)));
    }

    public static void generator(String modid, AbstractRegistrate<?> registrate) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new TooltipLoader(modid).generator(registrate));
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
                    tooltipKey = modid + ".tooltip." + id;
                }
                Type clazz = data.clazz();
                Class<?> annoCls = Class.forName(clazz.getClassName());
                Field field = annoCls.getDeclaredField(data.memberName());
                field.setAccessible(true);
                if (field.get(null) instanceof TooltipEntry entry) {
                    trySetKey(tooltipKey, entry);
                    map.put(tooltipKey, entry);
                } else if (field.get(null) instanceof TooltipHolder holder) {
                    String multiKey = tooltipKey + "_";
                    for (int i = 0; i < holder.size(); i++) {
                        TooltipEntry entry = holder.get(i);
                        if (entry.key.isEmpty()) {
                            tooltipKey = multiKey + i;
                        } else {
                            tooltipKey = entry.key;
                        }
                        trySetKey(tooltipKey, entry);
                        map.put(tooltipKey, entry);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

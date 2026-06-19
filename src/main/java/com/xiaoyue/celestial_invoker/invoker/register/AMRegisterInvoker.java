package com.xiaoyue.celestial_invoker.invoker.register;

import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.content.common.SimpleInvoker;
import com.xiaoyue.celestial_invoker.content.common.registrar.NeoForgeRegister;
import com.xiaoyue.celestial_invoker.content.common.registrar.RegistrateExtra;
import com.xiaoyue.celestial_invoker.invoker.handler.RegArmorMaterial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class AMRegisterInvoker {

    public final NeoForgeRegister<ArmorMaterial> register;
    public Map<String, ArmorMaterial> materials = new TreeMap<>();

    public AMRegisterInvoker(NeoForgeRegister<ArmorMaterial> register) {
        this.register = register;
    }

    public AMRegisterInvoker(RegistrateExtra<?> extra) {
        this.register = extra.neoforgeRegister(BuiltInRegistries.ARMOR_MATERIAL);
    }

    public static ArmorMaterial cast(Object obj) {
        return (ArmorMaterial) obj;
    }

    public AMRegisterInvoker scanModAndInject(String modid) {
        try {
            for (ModFileScanData.AnnotationData data : SimpleInvoker.getModAnno(modid, RegArmorMaterial.class)) {
                String id = Bindings.cast(data.annotationData().get("id"), String.class);
                Type clazz = data.clazz();
                Class<?> annoCls = Class.forName(clazz.getClassName());
                Field field = annoCls.getDeclaredField(data.memberName());
                field.setAccessible(true);
                ArmorMaterial material = cast(field.get(null));
                materials.put(id, material);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void register() {
        materials.forEach((id, material) -> register.object(id, () -> material));
    }
}

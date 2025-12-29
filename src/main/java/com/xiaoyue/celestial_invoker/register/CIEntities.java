package com.xiaoyue.celestial_invoker.register;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.entities.AirBladeEntity;
import com.xiaoyue.celestial_invoker.content.entities.GenericArrowEntity;
import com.xiaoyue.celestial_invoker.content.entities.render.AirBladeEntityRender;
import com.xiaoyue.celestial_invoker.content.entities.render.GenericArrowEntityRender;
import net.minecraft.world.entity.MobCategory;

public class CIEntities {

    public static final EntityEntry<AirBladeEntity> AIR_BLADE;
    public static final EntityEntry<GenericArrowEntity> GENERIC_ARROW;

    static {
        AIR_BLADE = CelestialInvoker.REGISTRATE.<AirBladeEntity>entity("air_blade", AirBladeEntity::new, MobCategory.MISC)
                .properties(e -> e.sized(0.5f, 0.5f)
                        .clientTrackingRange(4).setShouldReceiveVelocityUpdates(true)
                        .updateInterval(20).fireImmune())
                .renderer(() -> AirBladeEntityRender::new).defaultLang().register();
        GENERIC_ARROW = CelestialInvoker.REGISTRATE.<GenericArrowEntity>entity("generic_arrow", GenericArrowEntity::new, MobCategory.MISC)
                .properties(b -> b.sized(0.5f, 0.5f).clientTrackingRange(4)
                        .updateInterval(20).setShouldReceiveVelocityUpdates(true))
                .renderer(() -> GenericArrowEntityRender::new)
                .defaultLang().register();
    }

    public static void register() {
    }
}

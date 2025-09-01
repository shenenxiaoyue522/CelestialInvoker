package com.xiaoyue.celestial_invoker.register;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.entity.AirBladeEntity;
import com.xiaoyue.celestial_invoker.content.generic.entity.AirBladeEntityRender;
import net.minecraft.world.entity.MobCategory;

public class CIEntities {

    public static final EntityEntry<AirBladeEntity> AIR_BLADE;

    static {
        AIR_BLADE = CelestialInvoker.REGISTRATE.<AirBladeEntity>entity("air_blade", AirBladeEntity::new, MobCategory.MISC)
                .properties(e -> e.sized(0.5f, 0.5f)
                        .clientTrackingRange(4).setShouldReceiveVelocityUpdates(true)
                        .updateInterval(20).fireImmune())
                .renderer(() -> AirBladeEntityRender::new).defaultLang().register();
    }

    public static void register() {
    }
}

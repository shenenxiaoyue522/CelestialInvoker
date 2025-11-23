package com.xiaoyue.celestial_invoker.content.ancillary;

import com.xiaoyue.celestial_invoker.content.ancillary.helper.IRegistrateHelper;
import dev.xkmc.l2library.base.L2Registrate;

@SuppressWarnings("unused")
public class CelestialRegistrate extends L2Registrate implements IRegistrateHelper<CelestialRegistrate> {
    public CelestialRegistrate(String modid) {
        super(modid);
    }

    @Override
    public CelestialRegistrate owner() {
        return this;
    }
}

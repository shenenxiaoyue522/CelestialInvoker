package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CelestialInvoker.MODID)
public class CelestialInvoker {

    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MODID);
    
    public CelestialInvoker() {
        CIEntities.register();
        TooltipLoader.generator(MODID, REGISTRATE);
    }

    public static ResourceLocation loc(String s) {
        return ResourceLocation.parse(s);
    }
}

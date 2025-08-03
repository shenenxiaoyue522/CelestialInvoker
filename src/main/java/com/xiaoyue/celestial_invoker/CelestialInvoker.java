package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.xiaoyue.celestial_invoker.content.binding.CelestialRegistrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CelestialInvoker.MODID)
@Mod.EventBusSubscriber(modid = CelestialInvoker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialInvoker
{
    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CelestialRegistrate REGISTRATE = new CelestialRegistrate(MODID);
    
    public CelestialInvoker() {
        REGISTRATE.initDefaultConfig();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        boolean client = event.includeClient();
    }

    public static ResourceLocation loc(String s) {
        return new ResourceLocation(MODID, s);
    }
}

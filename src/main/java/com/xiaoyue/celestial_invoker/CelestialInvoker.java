package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.xiaoyue.celestial_invoker.config.ConfigLoader;
import com.xiaoyue.celestial_invoker.data.ConfigLangGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CelestialInvoker.MODID)
@Mod.EventBusSubscriber(modid = CelestialInvoker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialInvoker
{
    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public CelestialInvoker() {
        ConfigLoader.mapConfig(MODID).initConfigs(ModConfig.Type.COMMON);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        boolean client = event.includeClient();
//        gen.addProvider(client, new ConfigLangGen(gen.getPackOutput(), MODID));
    }

    public static ResourceLocation loc(String s) {
        return new ResourceLocation(MODID, s);
    }
}

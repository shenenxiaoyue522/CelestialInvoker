package com.xiaoyue.celestial_invoker;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.xiaoyue.celestial_invoker.content.network.NetworkHandler;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipLoader;
import com.xiaoyue.celestial_invoker.register.CIEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(CelestialInvoker.MODID)
@Mod.EventBusSubscriber(modid = CelestialInvoker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialInvoker {

    public static final String MODID = "celestial_invoker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MODID);

    public CelestialInvoker() {
        CIEntities.register();
        TooltipLoader.generator(MODID, REGISTRATE);
    }

    @SubscribeEvent
    public static void onCommonStep(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }

    public static ResourceLocation loc(String s) {
        return new ResourceLocation(MODID, s);
    }
}

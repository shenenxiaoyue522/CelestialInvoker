package com.xiaoyue.celestial_invoker;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class CInvokerClient {

    @SubscribeEvent
    public static void clientStep(RegisterGuiLayersEvent event) {
    }
}

package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.client.overlay.HudManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        HudManager.INSTANCE.tick();
    }
}

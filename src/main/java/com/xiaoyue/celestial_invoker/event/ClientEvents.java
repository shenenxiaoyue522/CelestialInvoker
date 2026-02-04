package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.client.overlay.HudElement;
import com.xiaoyue.celestial_invoker.content.client.overlay.HudManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderHud(CustomizeGuiOverlayEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        HudManager manager = HudManager.INSTANCE;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        for (HudElement element : manager.getAllElements()) {
            if (element.isVisible()) {
                element.render(event.getGuiGraphics(), event.getPartialTick(), screenWidth, screenHeight);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        HudManager.INSTANCE.tick();
    }
}

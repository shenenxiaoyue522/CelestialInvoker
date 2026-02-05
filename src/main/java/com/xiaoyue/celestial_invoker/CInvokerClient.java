package com.xiaoyue.celestial_invoker;

import com.xiaoyue.celestial_invoker.content.client.HudOverlayHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CInvokerClient {

    @SubscribeEvent
    public static void clientStep(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void initHud(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "hud_manager", new HudOverlayHandler());
    }
}

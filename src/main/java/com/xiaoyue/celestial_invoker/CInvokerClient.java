package com.xiaoyue.celestial_invoker;

import com.xiaoyue.celestial_invoker.content.ancillary.helper.ItemBarHelper;
import com.xiaoyue.celestial_invoker.content.client.HudOverlayHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CInvokerClient {

    @SubscribeEvent
    public static void clientStep(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void initHud(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("custom_bar_handler", new HudOverlayHandler());
    }

    @SubscribeEvent
    public static void initItemDecor(RegisterItemDecorationsEvent event) {
        ForgeRegistries.ITEMS.getValues().forEach(item -> event.register(item, new ItemBarHelper()));
    }

}

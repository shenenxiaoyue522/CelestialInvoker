package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.item.ICustomTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void getTextColor(RenderTooltipEvent.Color event) {
        if (event.getItemStack().getItem() instanceof ICustomTooltip custom) {
            custom.getTextColor(event, event.getItemStack());
        }
    }

    @SubscribeEvent
    public static void getTextComponents(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().getItem() instanceof ICustomTooltip custom) {
            custom.getTextComponents(event, event.getItemStack());
        }
    }
}

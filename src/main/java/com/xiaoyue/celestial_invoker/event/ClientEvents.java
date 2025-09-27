package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.item.GenericBowItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void getDrawingFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (player.isUsingItem()) {
            ItemStack stack = player.getUseItem();
            if (stack.getItem() instanceof GenericBowItem bow) {
                bow.getDrawingFov(player, stack, event);
            }
        }
    }
}

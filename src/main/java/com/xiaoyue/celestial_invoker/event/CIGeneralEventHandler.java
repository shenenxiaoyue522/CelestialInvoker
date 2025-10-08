package com.xiaoyue.celestial_invoker.event;

import com.xiaoyue.celestial_invoker.content.generic.item.IClickInteraction;
import com.xiaoyue.celestial_invoker.content.generic.network.ClickEmptyPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CIGeneralEventHandler {

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(player -> {
                ItemStack stack = event.getItemStack();
                if (stack.getItem() instanceof IClickInteraction item) {
                    item.onLeftClickEmpty(stack, event, player);
                }
            }).toServer();
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().level().isClientSide()) {
            new ClickEmptyPacket(player -> {
                ItemStack stack = event.getItemStack();
                if (stack.getItem() instanceof IClickInteraction item) {
                    item.onRightClickEmpty(stack, event, player);
                }
            }).toServer();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getItemStack().getItem() instanceof IClickInteraction item) {
            item.onLeftClickBlock(event.getItemStack(), event, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().getItem() instanceof IClickInteraction item) {
            item.onRightClickBlock(event.getItemStack(), event, event.getEntity());
        }
    }
}

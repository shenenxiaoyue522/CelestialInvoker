package com.xiaoyue.celestial_invoker.content.generic.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;

public interface ICustomTooltip {

    default void getTextColor(RenderTooltipEvent.Color event, ItemStack stack) {

    }

    default void getTextComponents(RenderTooltipEvent.GatherComponents event, ItemStack stack) {

    }

}

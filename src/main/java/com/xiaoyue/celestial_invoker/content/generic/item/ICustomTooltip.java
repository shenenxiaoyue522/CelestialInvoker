package com.xiaoyue.celestial_invoker.content.generic.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;

public interface ICustomTooltip {

    void getTextColor(RenderTooltipEvent.Color event, ItemStack stack);

}

package com.xiaoyue.celestial_invoker.content.generic.items.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ICustomName {

    default Component getCustomName(ItemStack stack, Component origin) {
        return origin;
    }
}

package com.xiaoyue.celestial_invoker.content.generic.items.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IClickInteraction {

    default void onLeftClickEmpty(Player player, ItemStack stack, InteractionHand hand) {
    }

    default void onRightClickEmpty(Player player, ItemStack stack, InteractionHand hand) {
    }
}

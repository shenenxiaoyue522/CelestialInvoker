package com.xiaoyue.celestial_invoker.content.generic.item.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public interface IClickInteraction {

    default void onLeftClickEmpty(Player player, ItemStack stack, InteractionHand hand) {
    }

    default void onRightClickEmpty(Player player, ItemStack stack, InteractionHand hand) {
    }

    default void onLeftClickBlock(Player player, ItemStack stack, PlayerInteractEvent.LeftClickBlock event) {
    }

    default void onRightClickBlock(Player player, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
    }
}

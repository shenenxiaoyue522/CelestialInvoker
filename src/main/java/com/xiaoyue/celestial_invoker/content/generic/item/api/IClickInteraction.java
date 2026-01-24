package com.xiaoyue.celestial_invoker.content.generic.item.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IClickInteraction {

    default void onLeftClickEmpty(ItemStack stack, PlayerInteractEvent.LeftClickEmpty event, Player player) {

    }

    default void onRightClickEmpty(ItemStack stack, PlayerInteractEvent.RightClickEmpty event, Player player) {

    }

    default void onLeftClickBlock(ItemStack stack, PlayerInteractEvent.LeftClickBlock event, Player player) {

    }

    default void onRightClickBlock(ItemStack stack, PlayerInteractEvent.RightClickBlock event, Player player) {

    }
}

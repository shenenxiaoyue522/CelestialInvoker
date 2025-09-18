package com.xiaoyue.celestial_invoker.content.generic.shared;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IModuleMenu {

    default void onTake(Slot slot, Player player, ItemStack stack) {
    }

    default boolean mayPlace(Slot slot, ItemStack stack) {
        return true;
    }

    default int getMaxStackSize(Slot slot, ItemStack stack) {
        return 64;
    }

    default void onSlotChanged(Slot slot) {
    }
}

package com.xiaoyue.celestial_invoker.content.generic.shared;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IControlSlotMenu {

    default ModuleSlot slot(AbstractContainerMenu menu, Container inv, int slot, int x, int y) {
        return new ModuleSlot(menu, inv, slot, x, y);
    }

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

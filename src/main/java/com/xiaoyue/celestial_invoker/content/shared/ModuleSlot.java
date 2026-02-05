package com.xiaoyue.celestial_invoker.content.shared;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModuleSlot extends Slot {
    public final AbstractContainerMenu menu;

    public ModuleSlot(AbstractContainerMenu menu, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        this.menu = menu;
    }

    @Override
    public int getMaxStackSize(ItemStack pStack) {
        if (menu instanceof IControlSlotMenu moduleMenu) {
            return moduleMenu.getMaxStackSize(this, pStack);
        }
        return super.getMaxStackSize(pStack);
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        if (menu instanceof IControlSlotMenu moduleMenu) {
            moduleMenu.onTake(this, pPlayer, pStack);
        }
        super.onTake(pPlayer, pStack);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        if (menu instanceof IControlSlotMenu moduleMenu) {
            return moduleMenu.mayPlace(this, pStack);
        }
        return super.mayPlace(pStack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (menu instanceof IControlSlotMenu moduleMenu) {
            moduleMenu.onSlotChanged(this);
        }
    }
}

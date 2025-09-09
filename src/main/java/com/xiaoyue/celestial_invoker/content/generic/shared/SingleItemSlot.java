package com.xiaoyue.celestial_invoker.content.generic.shared;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SingleItemSlot extends Slot {
    public SingleItemSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

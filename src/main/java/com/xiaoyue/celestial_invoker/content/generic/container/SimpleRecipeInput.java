package com.xiaoyue.celestial_invoker.content.generic.container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeInput;

public class SimpleRecipeInput extends SimpleContainer implements RecipeInput {
    public SimpleRecipeInput(int size) {
        super(size);
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}

package com.xiaoyue.celestial_invoker.content.common.registrar;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record MetalItemEntry<I extends Item, B extends Block>(ItemEntry<I> ingot, ItemEntry<I> nugget, BlockEntry<B> block) {

}

package com.xiaoyue.celestial_invoker.content.binding.tier;

import dev.xkmc.l2damagetracker.contents.materials.api.ArmorMat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class ArmorStats extends ArmorMat {
    public ArmorStats(String name, int durability, int[] protection, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
        super(name, durability, protection, enchant, sound, tough, kb, repair);
    }

    public static ArmorStats create(String name, int durability, int[] protection, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
        return new ArmorStats(name, durability, protection, enchant, sound, tough, kb, repair);
    }

    public static ArmorStats create(String name, int durability, int[] protection, int enchant, float tough, float kb, Supplier<Ingredient> repair) {
        return new ArmorStats(name, durability, protection, enchant, SoundEvents.ARMOR_EQUIP_GENERIC, tough, kb, repair);
    }
}

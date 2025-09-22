package com.xiaoyue.celestial_invoker.content.ancillary.material;

import dev.xkmc.l2damagetracker.contents.materials.api.ArmorMat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class ArmorMate extends ArmorMat {
    public static final ArmorMate EMPTY = ArmorMate.create("", 0, new int[]{0, 0, 0, 0}, 0, 0);

    public ArmorMate(String name, int durability, int[] protection, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
        super(name, durability, protection, enchant, sound, tough, kb, repair);
    }

    public static ArmorMate create(String name, int durability, int[] protection, int enchant, SoundEvent sound, float tough, float kb, Supplier<Ingredient> repair) {
        return new ArmorMate(name, durability, protection, enchant, sound, tough, kb, repair);
    }

    public static ArmorMate create(String name, int durability, int[] protection, int enchant, float tough, float kb, Supplier<Ingredient> repair) {
        return new ArmorMate(name, durability, protection, enchant, SoundEvents.ARMOR_EQUIP_GENERIC, tough, kb, repair);
    }

    public static ArmorMate create(String name, int durability, int[] protection, int enchant, float tough, Supplier<Ingredient> repair) {
        return new ArmorMate(name, durability, protection, enchant, SoundEvents.ARMOR_EQUIP_GENERIC, tough, 0, repair);
    }

    public static ArmorMate create(String name, int durability, int[] protection, int enchant, float tough) {
        return new ArmorMate(name, durability, protection, enchant, SoundEvents.ARMOR_EQUIP_GENERIC, tough, 0, Ingredient::of);
    }
}

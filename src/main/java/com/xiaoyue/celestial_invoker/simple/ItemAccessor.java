package com.xiaoyue.celestial_invoker.simple;

import net.minecraft.world.item.Item;

import java.util.UUID;

public class ItemAccessor extends Item {
    public ItemAccessor(Properties pProperties) {
        super(pProperties);
    }

    public static UUID getBaseDamageUUID() {
        return BASE_ATTACK_DAMAGE_UUID;
    }

    public static UUID getBaseSpeedUUID() {
        return BASE_ATTACK_SPEED_UUID;
    }
}

package com.xiaoyue.celestial_invoker.event.api;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class LivingHealthChangeEvent extends Event implements ICancellableEvent {

    private final LivingEntity entity;
    private float newHealth;

    public LivingHealthChangeEvent(LivingEntity entity, float newHealth) {
        this.entity = entity;
        this.newHealth = newHealth;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getOriginalHealth() {
        return entity.getHealth();
    }

    public float getNewHealth() {
        return newHealth;
    }

    public boolean isAddition() {
        return newHealth > entity.getHealth();
    }

    public void setNewHealth(float newHealth) {
        this.newHealth = newHealth;
    }
}

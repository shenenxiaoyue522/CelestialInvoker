package com.xiaoyue.celestial_invoker.content.common;

import com.xiaoyue.celestial_invoker.content.entities.GenericArrowEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArrowContext {

    public double damage = 2.0;
    public byte pierce = 0;
    public boolean ignoreWater, ignoreGravity = false;
    public Consumer<GenericArrowEntity> onTick;
    public BiConsumer<GenericArrowEntity, Entity> hitEntity;
    public BiConsumer<GenericArrowEntity, BlockHitResult> hitBlock;

    public ArrowContext damage(double damage) {
        this.damage = damage;
        return this;
    }

    public ArrowContext pierce(byte pierce) {
        this.pierce = pierce;
        return this;
    }

    public ArrowContext ignoreWater() {
        this.ignoreWater = true;
        return this;
    }

    public ArrowContext ignoreGravity() {
        this.ignoreGravity = true;
        return this;
    }

    public ArrowContext onTick(Consumer<GenericArrowEntity> onTick) {
        this.onTick = onTick;
        return this;
    }

    public ArrowContext hitEntity(BiConsumer<GenericArrowEntity, Entity> hitEntity) {
        this.hitEntity = hitEntity;
        return this;
    }

    public ArrowContext hitBlock(BiConsumer<GenericArrowEntity, BlockHitResult> hitBlock) {
        this.hitBlock = hitBlock;
        return this;
    }
}

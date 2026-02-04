package com.xiaoyue.celestial_invoker.content.generic.builder;

import com.xiaoyue.celestial_invoker.content.entities.GenericArrowEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArrowDataBuilder {

    public double damage = 2.0;
    public byte pierce = 0;
    public boolean ignoreWater, ignoreGravity = false;
    public Consumer<GenericArrowEntity> onTick;
    public BiConsumer<GenericArrowEntity, Entity> hitEntity;
    public BiConsumer<GenericArrowEntity, BlockHitResult> hitBlock;

    public ArrowDataBuilder damage(double damage) {
        this.damage = damage;
        return this;
    }

    public ArrowDataBuilder pierce(byte pierce) {
        this.pierce = pierce;
        return this;
    }

    public ArrowDataBuilder ignoreWater() {
        this.ignoreWater = true;
        return this;
    }

    public ArrowDataBuilder ignoreGravity() {
        this.ignoreGravity = true;
        return this;
    }

    public ArrowDataBuilder onTick(Consumer<GenericArrowEntity> onTick) {
        this.onTick = onTick;
        return this;
    }

    public ArrowDataBuilder hitEntity(BiConsumer<GenericArrowEntity, Entity> hitEntity) {
        this.hitEntity = hitEntity;
        return this;
    }

    public ArrowDataBuilder hitBlock(BiConsumer<GenericArrowEntity, BlockHitResult> hitBlock) {
        this.hitBlock = hitBlock;
        return this;
    }
}

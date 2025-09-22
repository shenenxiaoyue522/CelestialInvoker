package com.xiaoyue.celestial_invoker.content.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class ArmorModelSetter extends HumanoidModel<LivingEntity> {
    public ArmorModelSetter(ModelPart pRoot) {
        super(pRoot);
    }

    public abstract List<ModelLayerLocation> getLocations();

    public abstract LayerDefinition getDefinition();

}

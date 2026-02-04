package com.xiaoyue.celestial_invoker.content.entities.render;

import com.xiaoyue.celestial_invoker.content.entities.GenericArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GenericArrowEntityRender extends ArrowRenderer<GenericArrowEntity> {
    public static final ResourceLocation TEXTURE_ARROW = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public GenericArrowEntityRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public ResourceLocation getTextureLocation(GenericArrowEntity arrow) {
        return TEXTURE_ARROW;
    }
}

package com.xiaoyue.celestial_invoker.content.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiaoyue.celestial_invoker.content.entities.SimpleThrowEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ThrowableEntityRender<T extends SimpleThrowEntity> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;
    private final Vec3 scale;
    private final float offset;
    private final Matrix4f preTransform = new Matrix4f();

    protected ThrowableEntityRender(EntityRendererProvider.Context context, Vec3 scale, float offset) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.175F;
        this.scale = scale.scale(0.85);
        this.offset = offset;
        preTransform.translate(-offset, -offset, 0);
        preTransform.scale((float) scale.x, (float) scale.y, (float) scale.z);
    }

    public void render(T entity, float v, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        ItemStack stack = entity.weapon;
        BakedModel bakedmodel = this.itemRenderer.getModel(stack, entity.level(), null, entity.getId());
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO - 90.0F, entity.getYRot() - 90.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO - 45.0F, entity.getXRot() - 45.0F)));
        poseStack.last().pose().mul(preTransform);
        poseStack.translate(0, entity.getBbHeight() / 2, 0);
        this.itemRenderer.render(stack, ItemDisplayContext.NONE, false, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, bakedmodel);
        poseStack.popPose();
        super.render(entity, v, partialTick, poseStack, buffer, light);
    }

    public Vec3 getScale() {
        return scale;
    }

    public float getOffset() {
        return offset;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}

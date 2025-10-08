package com.xiaoyue.celestial_invoker.content.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaoyue.celestial_invoker.content.entities.MagicProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MagicProjectileRender extends EntityRenderer<MagicProjectile> {

    private static final int BASE_PARTICLE_COUNT = 3;
    private static final float PARTICLE_SIZE = 0.3f;
    private static final float ROTATION_SPEED = 5.0f;

    public MagicProjectileRender(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public void render(MagicProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.level().isClientSide && entity.age > 3) {
            renderParticleEffects(entity, partialTicks);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MagicProjectile entity) {
        return new ResourceLocation("textures/block/stone.png");
    }

    private void renderParticleEffects(MagicProjectile entity, float partialTicks) {
        Level level = entity.level();
        int color = entity.config.color;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        Vec3 position = entity.position();
        float age = entity.tickCount + partialTicks;
        renderCoreParticles(level, position, age, red, green, blue);
        renderOrbitingParticles(level, position, age, red, green, blue);
        renderTrailParticles(entity, position, partialTicks, red, green, blue);
        renderEffectSpecificParticles(entity, level, position, age);
    }

    private void renderCoreParticles(Level level, Vec3 position, float age, float red, float green, float blue) {
        for (int i = 0; i < BASE_PARTICLE_COUNT; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 0.3;
            double offsetY = (level.random.nextDouble() - 0.5) * 0.3;
            double offsetZ = (level.random.nextDouble() - 0.5) * 0.3;
            level.addParticle(new DustParticleOptions(new Vector3f(red, green, blue), 2.0f), position.x + offsetX,
                    position.y + offsetY, position.z + offsetZ, 0, 0, 0
            );
        }
        float pulse = (float) Math.sin(age * 0.3f) * 0.1f + 0.9f;
        float pulseSize = PARTICLE_SIZE * pulse;
        level.addParticle(new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), pulseSize * 1.5f), position.x,
                position.y, position.z, 0, 0, 0
        );
    }

    private void renderOrbitingParticles(Level level, Vec3 position, float age, float red, float green, float blue) {
        int orbitCount = 4;
        float orbitRadius = 0.5f;
        for (int i = 0; i < orbitCount; i++) {
            float angle = (age * ROTATION_SPEED + (i * 360.0f / orbitCount)) * ((float) Math.PI / 180.0f);
            double orbitX = Math.cos(angle) * orbitRadius;
            double orbitZ = Math.sin(angle) * orbitRadius;
            double orbitY = Math.sin(angle * 1.5f) * orbitRadius * 0.3;
            level.addParticle(new DustParticleOptions(new Vector3f(red, green, blue), 1.0f), position.x + orbitX,
                    position.y + orbitY, position.z + orbitZ, 0, 0, 0
            );
        }
    }

    private void renderTrailParticles(MagicProjectile entity, Vec3 position, float partialTicks, float red, float green, float blue) {
        Level level = entity.level();
        if (entity.tickCount % 2 == 0) {
            Vec3 motion = entity.getDeltaMovement();
            Vec3 trailDirection = motion.normalize().scale(-0.3);
            for (int i = 0; i < 2; i++) {
                Vec3 trailPos = position.add((level.random.nextDouble() - 0.5) * 0.2, (level.random.nextDouble() - 0.5) * 0.2,
                        (level.random.nextDouble() - 0.5) * 0.2);
                float trailSize = PARTICLE_SIZE * 0.7f;
                level.addParticle(new DustParticleOptions(new Vector3f(red, green, blue), trailSize), trailPos.x, trailPos.y, trailPos.z,
                        trailDirection.x * level.random.nextDouble(), trailDirection.y * level.random.nextDouble(),
                        trailDirection.z * level.random.nextDouble());
            }
        }
    }

    private void renderEffectSpecificParticles(MagicProjectile entity, Level level, Vec3 position, float age) {
        entity.config.forEffects(e -> e.renderExtra(entity, level, position, age));
    }

}


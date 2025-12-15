package com.xiaoyue.celestial_invoker.content.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleTexParticle<S extends ParticleOptions> extends TextureSheetParticle implements ParticleProvider<S>, ParticleEngine.SpriteParticleRegistration<S> {
    public final SpriteSet sprites;
    public final ParticleRenderType type;
    public final int lifeTime;

    public SimpleTexParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet sprites, double pXSpeed, double pYSpeed, double pZSpeed, ParticleRenderType type, int lifeTime) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = sprites;
        this.type = type;
        this.lifeTime = lifeTime;
        if (!excursion()) {
            this.xd = pXSpeed;
            this.yd = pYSpeed;
            this.zd = pZSpeed;
        }
    }

    public boolean excursion() {
        return true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return type;
    }

    @Override
    public abstract @Nullable Particle createParticle(S type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed);

    @Override
    public abstract ParticleProvider<S> create(SpriteSet spriteSet);

}

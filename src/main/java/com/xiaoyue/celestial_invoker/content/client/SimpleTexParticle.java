package com.xiaoyue.celestial_invoker.content.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(value = Dist.CLIENT)
public abstract class SimpleTexParticle extends TextureSheetParticle implements ParticleProvider<SimpleParticleType>, ParticleEngine.SpriteParticleRegistration<SimpleParticleType> {
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
    public abstract @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed);

    @Override
    public ParticleProvider<SimpleParticleType> create(SpriteSet spriteSet) {
        return this;
    }
}

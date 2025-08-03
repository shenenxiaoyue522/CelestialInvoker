package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ParticleDescriptionProvider;
import net.minecraftforge.fml.LogicalSide;

public class RegistrateParticleTexProvider extends ParticleDescriptionProvider implements RegistrateProvider {
    private final AbstractRegistrate<?> owner;

    protected RegistrateParticleTexProvider(PackOutput output, ExistingFileHelper fileHelper, AbstractRegistrate<?> owner) {
        super(output, fileHelper);
        this.owner = owner;
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    protected void addDescriptions() {
        owner.genData(CelestialProviders.PARTICLE_TEX, this);
    }

    @Override
    public void sprite(ParticleType<?> type, ResourceLocation texture) {
        super.sprite(type, texture);
    }

    @Override
    public void spriteSet(ParticleType<?> type, Iterable<ResourceLocation> textures) {
        super.spriteSet(type, textures);
    }

    @Override
    public void spriteSet(ParticleType<?> type, ResourceLocation texture, ResourceLocation... textures) {
        super.spriteSet(type, texture, textures);
    }

    @Override
    public void spriteSet(ParticleType<?> type, ResourceLocation baseName, int numOfTextures, boolean reverse) {
        super.spriteSet(type, baseName, numOfTextures, reverse);
    }
}

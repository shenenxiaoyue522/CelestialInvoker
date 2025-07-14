package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.fml.LogicalSide;

public class RegistrateSoundEventProvider extends SoundDefinitionsProvider implements RegistrateProvider {
    private final AbstractRegistrate<?> owner;

    public RegistrateSoundEventProvider(PackOutput output, ExistingFileHelper helper, AbstractRegistrate<?> owner) {
        super(output, owner.getModid(), helper);
        this.owner = owner;
    }

    @Override
    public void add(SoundEvent soundEvent, SoundDefinition definition) {
        super.add(soundEvent, definition);
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    public void registerSounds() {
        owner.genData(GeneratorTypes.SOUND_EVENT, this);
    }
}

package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.providers.ProviderType;

public class CelestialProviders {

    public static final ProviderType<RegistrateLootModifierProvider> LOOT_MODIFIER = ProviderType.register("global_loot_modifier",
            (r, e) -> new RegistrateLootModifierProvider(e.getGenerator().getPackOutput(), r));

    public static final ProviderType<RegistrateRecordDataProvider> RECORD_DATA = ProviderType.register("record_data",
            (r, e) -> new RegistrateRecordDataProvider(e.getGenerator(), r));

    public static final ProviderType<RegistrateSoundEventProvider> SOUND_EVENT = ProviderType.register("sound_event",
            (r, e) -> new RegistrateSoundEventProvider(e.getGenerator().getPackOutput(), e.getExistingFileHelper(), r));

    public static final ProviderType<RegistrateParticleTexProvider> PARTICLE_TEX = ProviderType.register("particle_tex",
            (r, e) -> new RegistrateParticleTexProvider(e.getGenerator().getPackOutput(), e.getExistingFileHelper(), r));

}

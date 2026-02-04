package com.xiaoyue.celestial_invoker.content.ancillary.generator;

import com.tterrag.registrate.providers.ProviderType;

public class CelestialProviders {

    public static final ProviderType<RegistrateLootModifierProvider> LOOT_MODIFIER = ProviderType.register("global_loot_modifier",
            (r, e) -> new RegistrateLootModifierProvider(e.getGenerator().getPackOutput(), e.getLookupProvider(), r));

}

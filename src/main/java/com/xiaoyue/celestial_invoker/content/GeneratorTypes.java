package com.xiaoyue.celestial_invoker.content;

import com.tterrag.registrate.providers.ProviderType;
import com.xiaoyue.celestial_invoker.content.generator.RegistrateLootModifierProvider;
import com.xiaoyue.celestial_invoker.content.generator.RegistrateRecordDataProvider;

public class GeneratorTypes {

    public static final ProviderType<RegistrateLootModifierProvider> LOOT_MODIFIER = ProviderType.register("global_loot_modifier",
            (r, e) -> new RegistrateLootModifierProvider(e.getGenerator().getPackOutput(), r));

    public static final ProviderType<RegistrateRecordDataProvider> RECORD_DATA = ProviderType.register("record_data",
            (r, e) -> new RegistrateRecordDataProvider(e.getGenerator(), r));

}

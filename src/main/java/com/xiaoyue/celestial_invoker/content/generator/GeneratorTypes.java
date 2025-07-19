package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.RegistrySetBuilder;

public class GeneratorTypes {

    public static final ProviderType<RegistrateLootModifierProvider> LOOT_MODIFIER = ProviderType.register("global_loot_modifier",
            (r, e) -> new RegistrateLootModifierProvider(e.getGenerator().getPackOutput(), r));

    public static final ProviderType<RegistrateRecordDataProvider> RECORD_DATA = ProviderType.register("record_data",
            (r, e) -> new RegistrateRecordDataProvider(e.getGenerator(), r));

    public static final ProviderType<RegistrateSoundEventProvider> SOUND_EVENT = ProviderType.register("sound_event",
            (r, e) -> new RegistrateSoundEventProvider(e.getGenerator().getPackOutput(), e.getExistingFileHelper(), r));

    public static ProviderType<RegistrateDataEntriesProvider> dataEntries(RegistrySetBuilder builder) {
        return ProviderType.register("data_entries", (r, e) ->
                new RegistrateDataEntriesProvider(e.getGenerator().getPackOutput(), e.getLookupProvider(), r, builder));
    }
}

package com.xiaoyue.celestial_invoker.register;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.common.registrar.NeoForgeRegister;
import com.xiaoyue.celestial_invoker.content.generic.shared.CompoundData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class CIObjects {

    public static final NeoForgeRegister<DataComponentType<?>> TYPE = CelestialInvoker.EXTRA.neoforgeRegister(Registries.DATA_COMPONENT_TYPE);

    public static final Supplier<DataComponentType<CompoundData>> COMPOUND_DATA = TYPE.component("compound_data",
            b -> b.persistent(CompoundData.CODEC).networkSynchronized(CompoundData.STREAM_CODEC));

    public static void register() {
    }
}

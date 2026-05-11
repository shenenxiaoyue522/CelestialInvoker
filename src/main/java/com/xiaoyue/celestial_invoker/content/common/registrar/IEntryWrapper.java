package com.xiaoyue.celestial_invoker.content.common.registrar;

import net.minecraft.resources.ResourceLocation;

public interface IEntryWrapper<S> {

    S get();

    ResourceLocation getId();

    String getRegisteredName();
}

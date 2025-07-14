package com.xiaoyue.celestial_invoker.generic;

import com.xiaoyue.celestial_invoker.generic.builder.SimpleItemBuilder;

public class SimpleItemImpl extends ISimpleItem.Factory {
    private final SimpleItemBuilder.Impl builder;

    public SimpleItemImpl(Properties pProperties, SimpleItemBuilder.Impl builder) {
        super(pProperties);
        this.builder = builder;
    }

    @Override
    public SimpleItemBuilder.Impl getBuilder() {
        return builder;
    }
}

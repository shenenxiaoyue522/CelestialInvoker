package com.xiaoyue.celestial_invoker.content.generic.shared;

import com.xiaoyue.celestial_invoker.content.generic.builder.SimpleItemBuilder;
import com.xiaoyue.celestial_invoker.content.generic.item.ISimpleItem;

public class SimpleItem extends ISimpleItem.Factory {
    private final SimpleItemBuilder.Impl builder;

    public SimpleItem(Properties pProperties, SimpleItemBuilder.Impl builder) {
        super(pProperties);
        this.builder = builder;
    }

    @Override
    public SimpleItemBuilder.Impl getBuilder() {
        return builder;
    }
}

package com.xiaoyue.celestial_invoker.content.tooltip;

import java.util.ArrayList;
import java.util.List;

public class TooltipHolder extends ArrayList<TooltipEntry> {

    public static TooltipHolder define(TooltipEntry... entries) {
        TooltipHolder holder = new TooltipHolder();
        holder.addAll(List.of(entries));
        return holder;
    }
}

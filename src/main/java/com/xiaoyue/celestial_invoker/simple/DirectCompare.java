package com.xiaoyue.celestial_invoker.simple;

import java.util.Comparator;
import java.util.function.BiFunction;

public class DirectCompare implements Comparator<String> {
    private final BiFunction<String, String, Integer> func;

    public DirectCompare(BiFunction<String, String, Integer> func) {
        this.func = func;
    }

    @Override
    public int compare(String s1, String s2) {
        return func.apply(s1, s2);
    }
}

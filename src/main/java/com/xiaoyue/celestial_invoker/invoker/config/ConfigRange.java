package com.xiaoyue.celestial_invoker.invoker.config;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ConfigRange<V extends Comparable<? super V>> implements Predicate<Object> {

    private final Class<? extends V> clazz;
    private final V min;
    private final V max;

    public ConfigRange(Class<V> clazz, V min, V max) {
        this.clazz = clazz;
        this.min = min;
        this.max = max;
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Range min must be less then max.");
        }
    }

    public Class<? extends V> getClazz() {
        return this.clazz;
    }

    public V getMin() {
        return this.min;
    }

    public V getMax() {
        return this.max;
    }

    private boolean isNumber(@Nullable Object other) {
        return Number.class.isAssignableFrom(this.clazz) && other instanceof Number;
    }

    public boolean test(Object t) {
        if (this.isNumber(t)) {
            Number n = (Number)t;
            return ((Number) this.min).doubleValue() <= n.doubleValue() && n.doubleValue() <= ((Number) this.max).doubleValue();
        } else if (!this.clazz.isInstance(t)) {
            return false;
        } else {
            V c = this.clazz.cast(t);
            return c.compareTo(this.min) >= 0 && c.compareTo(this.max) <= 0;
        }
    }

    public String toString() {
        if (this.clazz == Integer.class) {
            if (this.max.equals(Integer.MAX_VALUE)) {
                return "> " + this.min;
            }
            if (this.min.equals(Integer.MIN_VALUE)) {
                return "< " + this.max;
            }
        }
        return this.min + " ~ " + this.max;
    }
}

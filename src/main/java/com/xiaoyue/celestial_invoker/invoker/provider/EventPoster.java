package com.xiaoyue.celestial_invoker.invoker.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface EventPoster {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface ArmorModelSet {

    }
}

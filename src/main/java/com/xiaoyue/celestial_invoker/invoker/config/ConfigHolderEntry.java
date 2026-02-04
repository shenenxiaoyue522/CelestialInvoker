package com.xiaoyue.celestial_invoker.invoker.config;

import net.neoforged.fml.config.ModConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigHolderEntry {

    String category() default "";

    ModConfig.Type type() default ModConfig.Type.COMMON;
}

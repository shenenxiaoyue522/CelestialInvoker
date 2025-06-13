package com.xiaoyue.celestial_invoker.generic;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigHolderEntry {
    String category() default "";

    ModConfig.Type type() default Type.COMMON;
}

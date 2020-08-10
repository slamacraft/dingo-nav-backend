package com.dingdo.common.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 将方法注解为指令可执行方法，并注入到InstructionMethodContext中
 * 注意：要求该方法的实例存在于applicationContext中，因此无法加载未在applicationContext中注册实例的方法
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface Instruction {
    String name();
    String descrption();
    String errorMsg() default "指令参数错误！";
}

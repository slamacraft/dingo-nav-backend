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
    /**
     * 指令的英文名称
     */
    String name();

    /**
     * 指令的中文名称
     */
    String description();

    /**
     * 指令执行异常后发送给用户的异常消息
     */
    String errorMsg() default "指令参数错误！";

    /**
     * 指令是否在菜单上显示
     */
    boolean inMenu() default true;  // 是否在菜单上显示该指令
}

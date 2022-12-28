package com.dingo.common.annotation;

import com.dingo.enums.VerificationEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 需要权限校验的方法注解
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface VerifiAnnotation {

    /**
     * 校验权限的级别
     * @return  权限级别的枚举
     */
    VerificationEnum level() default VerificationEnum.MANAGER;

}

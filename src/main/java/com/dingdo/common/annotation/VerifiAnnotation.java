package com.dingdo.common.annotation;

import com.dingdo.enums.VerificationEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface VerifiAnnotation {

    /**
     * 校验权限的级别
     * @return
     */
    VerificationEnum level() default VerificationEnum.MANAGER;

}

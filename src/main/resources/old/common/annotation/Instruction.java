package com.dingdo.common.annotation;

import com.dingdo.msgHandler.model.ReqMsg;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 指令方法注解
 * <p>
 *      将方法注解为指令方法，并注入到InstructionMethodContext中<br>
 *      需要该方法的实例存在于applicationContext中，因此无法加载未在applicationContext中
 *      注册实例的方法<br>
 *      并且要求方法的参数固定为({@link ReqMsg}, {@link Map}), 需要的其他参数需从Map中获取
 * </p>
 *
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface Instruction {

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

    /**
     * 是否是短接指令
     * 例如：在用户调用这一指令后，将指令容器的接收入口与该方法进行短接，
     * 不对其进行指令命令调用识别，默认用户的下一次调用的为该指令
     */
    boolean isShortened() default false;
}

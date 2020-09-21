package com.dingdo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * application容器的工具类
 */
public class SpringContextUtils {
    private static ApplicationContext applicationContext = null;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getBean(getApplicationContext(), name, null);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getBean(getApplicationContext(), null, clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getBean(getApplicationContext(), name, clazz);
    }

    public static <T> T getBean(ApplicationContext context, String name, Class<T> clazz) {
        if (context == null) {
            return null;
        }
        if (StringUtils.isBlank(name)) {
            return context.getBean(clazz);
        } else if (clazz == null) {
            return (T) context.getBean(name);
        } else {
            return context.getBean(name, clazz);
        }
    }
}

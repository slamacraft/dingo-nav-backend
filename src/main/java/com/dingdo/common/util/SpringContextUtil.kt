package com.dingdo.common.util

import org.springframework.context.ApplicationContext

object SpringContextUtil {

    @JvmStatic
    lateinit var applicationContext: ApplicationContext

    @JvmStatic
    fun getApplication() = applicationContext

    @JvmStatic
    fun setApplication(applicationContext: ApplicationContext) =
        applicationContext.also { this.applicationContext = it }

    @JvmStatic
    fun getBean(name: String): Any = applicationContext.getBean(name)

    @JvmStatic
    fun <T> getBean(clazz: Class<T>): T = applicationContext.getBean(clazz)
}

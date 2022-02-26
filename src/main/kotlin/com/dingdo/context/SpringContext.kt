package com.dingdo.context

import org.springframework.context.ApplicationContext

object SpringContext {

    @JvmStatic
    lateinit var applicationContext: ApplicationContext

    @JvmStatic
    fun getApplication() = applicationContext

    @JvmStatic
    fun setApplication(applicationContext: ApplicationContext) =
        applicationContext.also { SpringContext.applicationContext = it }

    @JvmStatic
    fun getBean(name: String): Any = applicationContext.getBean(name)

    @JvmStatic
    fun <T> getBean(clazz: Class<T>): T = applicationContext.getBean(clazz)
}

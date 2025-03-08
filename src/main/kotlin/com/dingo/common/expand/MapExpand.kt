package com.dingo.common.expand

import kotlin.reflect.KClass

fun <T : Any> Map<String, *>.castTo(clazz: KClass<T>): T {
    return castTo(clazz.java)
}

/**
 * 将Map转换为对象
 */
fun <T> Map<String, *>.castTo(clazz: Class<T>): T {
    val bean = clazz.constructors[0].newInstance() as T
    for ((key, value) in this) {
        try {
            val field = clazz.getDeclaredField(key)
            field.isAccessible = true
            when (value) {
                is Map<*, *> -> field[bean] = (value as Map<String, *>).castTo(field.type)
                is Boolean -> field[bean] = value
                else -> field[bean] = field.type.cast(value)
            }
        } catch (e: NoSuchFieldException) {
            // 如果没有这个字段就跳过
        }
    }
    return bean
}

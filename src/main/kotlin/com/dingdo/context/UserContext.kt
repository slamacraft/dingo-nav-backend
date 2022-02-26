package com.dingdo.core.context

import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
object UserContext {
    private val userMap = HashMap<Long, UserInfo>()

    fun getUser(id: Long): UserInfo {
        return userMap.getOrPut(id) { UserInfo(id) }
    }
}

class UserInfo(val id: Long) {
    private val infoMap = HashMap<KClass<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInfo(clazz: KClass<T>, defaultSupplier: () -> T): T {
        return infoMap.getOrPut(clazz) { defaultSupplier.invoke() } as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> removeInfo(clazz: KClass<T>): T? {
        return infoMap.remove(clazz) as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInfo(clazz: KClass<T>): T? {
        return infoMap[clazz] as T?
    }

    fun registerInfo(info:Any): UserInfo {
        infoMap[info::class] = info
        return this
    }
}

package com.dingdo.user

import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Component
class UserContext {
    private val userMap = HashMap<Long, UserInfo>()

    fun getUser(id: Long): UserInfo {
        return userMap.getOrPut(id) { UserInfo(id) }
    }
}

class UserInfo(val id: Long) {
    private val infoMap = HashMap<KClass<*>, Any>()

    fun <T : Any> getInfo(clazz: KClass<T>): T {
        return infoMap.getOrPut(clazz) { clazz.createInstance() } as T
    }
}

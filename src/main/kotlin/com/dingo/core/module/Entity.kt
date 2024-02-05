package com.dingo.core.module

import java.time.LocalDateTime
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmErasure

open abstract class Entity<E : Entity<E>> {
    var id: Long = 0
    var createBy: Long = 0
    var createTime: LocalDateTime = LocalDateTime.now()
    var updateBy: Long = 0
    var updateTime: LocalDateTime = LocalDateTime.now()

    open abstract class Factory<E : Entity<E>> : TypeReference {
        private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }
        private fun createType(): E = referencedKotlinType.jvmErasure.createInstance() as E
        operator fun invoke(): E {
            return createType()
        }

        operator fun invoke(initFun: E.() -> Unit): E {
            return invoke().apply(initFun)
        }
    }
}

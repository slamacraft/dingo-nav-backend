@file:Suppress("UNUSED_EXPRESSION")

package com.dingo.core.module

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmErasure


open abstract class Table<E : Entity<E>>(tableName: String) : IdTable<Long>(tableName), TypeReference {
    private val bingMap: MutableMap<Column<*>, E.(Any?) -> Unit> = mutableMapOf()
    private val referencedKotlinType: KType by lazy { findSuperclassTypeArgument(javaClass.kotlin) }

    override val id = entityId("id", long("id")
        .bindTo { id = it!! })

    val createBy = long("create_by")
        .bindTo { createBy = it!! }

    val createTime = datetime("create_time")
        .bindTo { createTime = it!! }

    val updateBy = long("update_by")
        .bindTo { updateBy = it!! }

    val updateTime = datetime("update_time")
        .bindTo { updateTime = it!! }

    fun <T> Column<T>.bindTo(setFun: E.(T?) -> Unit): Column<T> {
        bingMap[this] = setFun as (E, Any?) -> Unit
        return this
    }

    fun FieldSet.getByIdOrNull(pid: Long): E? =
        Query(this, null)
            .where { id eq pid }
            .one()

    fun Query.one(): E? = firstOrNull()?.let {
        val entity = createType()
        val a = ::id
        bingMap.forEach { (column, function) ->
            function(entity, it[column])
        }
        entity
    }

    fun Query.list(): List<E> = map {
        val entity = createType()
        bingMap.forEach { (column, function) ->
            function(entity, it[column])
        }
        entity
    }


    private fun createType(): E = referencedKotlinType.jvmErasure.createInstance() as E
}
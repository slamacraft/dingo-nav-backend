package com.dingo.module.oss.entity

import com.dingo.core.module.BaseEntity
import com.dingo.core.module.BaseTable
import com.dingo.core.module.Entity
import com.dingo.core.module.Table
import com.fasterxml.jackson.databind.ser.Serializers.Base
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

interface OssEntity : Entity<OssEntity>, BaseEntity {
    companion object : Entity.Factory<OssEntity>()

    var name: String // 文件名称
    var size: Long // 文件后缀
    var url: String // 文件公开地址（不一定公开）
}


object OssTable : BaseTable<OssEntity>("bot_oss") {
    val name = varchar("name", 128)
    val size = long("size")
    val url = varchar("url", 512)
}
package com.dingo.module.oss.entity

import com.dingo.core.module.Entity
import com.dingo.core.module.Table

class OssEntity : Entity<OssEntity>() {
    companion object : Factory<OssEntity>()

    lateinit var name: String // 文件名称
    var size: Long = 0 // 文件后缀
    lateinit var url: String // 文件公开地址（不一定公开）
}


object OssTable : Table<OssEntity>("bot_oss") {
    val name = varchar("name", 128)
        .bindTo { name = it!! }
    val size = long("size")
        .bindTo { size = it!! }
    val url = varchar("url", 512)
        .bindTo { name = it!! }
}
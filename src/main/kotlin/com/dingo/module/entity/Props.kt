package com.dingo.module.entity

import com.baomidou.mybatisplus.annotation.TableName
import com.dingo.config.configuration.mybatis.BaseEntity

open class PropsGroupEntity : BaseEntity() {
    lateinit var name: String
    lateinit var from: String
}

@TableName("props")
open class PropsEntity : BaseEntity() {
    lateinit var name: String
    lateinit var icon: ByteArray
    lateinit var iconFormat: String
}

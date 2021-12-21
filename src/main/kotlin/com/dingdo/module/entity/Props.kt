package com.dingdo.module.entity

import com.baomidou.mybatisplus.annotation.TableName
import com.dingdo.config.configuration.mybatis.BaseEntity

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

package com.dingo.config.configuration.mybatis

import com.baomidou.mybatisplus.annotation.*
import java.util.*

open class BaseEntity {
    @TableId(type = IdType.AUTO)
    var id: Int? = null

    @TableField(fill = FieldFill.INSERT)
    var createTime: Date? = null

    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updateTime: Date? = null

    @TableLogic(value = "1", delval = "0")
    var valid: Boolean = true
}

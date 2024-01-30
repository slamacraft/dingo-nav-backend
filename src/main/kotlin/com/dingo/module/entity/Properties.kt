package com.dingo.module.entity

import com.dingo.config.configuration.mybatis.BaseEntity

/**
 * 群机器人配置
 */
class GroupPropertyEntity : BaseEntity() {
    lateinit var groupId: String
    lateinit var name: String
}

class UserPropertyEntity : BaseEntity() {
    lateinit var userId: String
    lateinit var name: String
}

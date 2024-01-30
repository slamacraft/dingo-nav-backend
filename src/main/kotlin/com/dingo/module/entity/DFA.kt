package com.dingo.module.entity

import com.dingo.config.configuration.mybatis.BaseEntity


//class StageRuleEntity : BaseEntity() {
//    lateinit var propertyId: String // 配置id
//    lateinit var propertyType: String   // 配置类型，群配置还是个人配置
//    lateinit var type: String
//}

class UserStageEntity : BaseEntity() {
    lateinit var userId: String
    lateinit var type: String
}

class UserStageLogEntity : BaseEntity() {
    lateinit var userId: String
    lateinit var type: String
}


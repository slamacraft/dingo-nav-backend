package com.dingo.config.configuration.mybatis

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import org.apache.ibatis.reflection.MetaObject
import org.springframework.stereotype.Component
import java.util.*

@Component("MbpMetaObjectHandler")
class MbpMetaObjectHandler : MetaObjectHandler {

    override fun insertFill(metaObject: MetaObject?) {
        strictInsertFill(metaObject, "createTime", Date::class.java, Date())
        strictInsertFill(metaObject, "updateTime", Date::class.java, Date())
    }

    override fun updateFill(metaObject: MetaObject?) {
        strictUpdateFill(metaObject, "updateTime", Date::class.java, Date())
    }
}

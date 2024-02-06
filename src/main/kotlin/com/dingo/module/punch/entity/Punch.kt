package com.dingo.module.punch.entity

import com.dingo.core.module.BaseEntity
import com.dingo.core.module.Entity

interface PunchEntity :Entity<PunchEntity>, BaseEntity{
    companion object: Entity.Factory<PunchEntity>()

}

//object PunchTable : BaseTable<PunchEntity>("bot_punch") {
//    override val id = entityId("id", long("id")
//        .bindTo { id = it!! })
//}
package com.dingo.module.punch.entity

import com.dingo.core.module.Entity
import com.dingo.core.module.Table

class PunchEntity : Entity<PunchEntity>(){
    companion object: Factory<PunchEntity>()
}

object PunchTable : Table<PunchEntity>("bot_punch") {
}
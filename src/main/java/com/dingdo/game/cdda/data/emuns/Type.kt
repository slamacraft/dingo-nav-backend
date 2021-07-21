package com.dingdo.game.cdda.data.emuns

import com.dingdo.game.cdda.data.model.common.BaseData
import com.dingdo.game.cdda.data.model.monster.Monster
import java.util.*

enum class Type(val typeName: String, val instanceSupplier: () -> BaseData) {
    MONSTER("MONSTER", { Monster::class.java.newInstance() })

    ;

    companion object {
        fun getEnum(name: String): Optional<Type> {
            return Optional.ofNullable(values().firstOrNull() { it.typeName == name })
        }
    }

}

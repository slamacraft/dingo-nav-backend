package com.dingdo.plugin.game.cdda.data.emuns

import com.dingdo.plugin.game.cdda.data.model.common.BaseData
import com.dingdo.plugin.game.cdda.data.model.*
import java.util.*
import kotlin.reflect.full.createInstance

enum class Type(val typeName: String, val instanceSupplier: () -> BaseData) {
    MONSTER("MONSTER", { Monster::class.createInstance() }),
    GENERIC("GENERIC", { Generic::class.createInstance() }),
    ARMOR("ARMOR", { Armor::class.createInstance() }),
    BODY_PART("body_part", { BodyPart::class.createInstance() }),
    MATERIAL("material", { Materials::class.createInstance() }),
    MUTATION("mutation", { Mutations::class.createInstance()}),
    PROFESSION("profession", { Professions::class.createInstance()})
    ;

    companion object {
        fun getEnum(name: String): Optional<Type> {
            return Optional.ofNullable(values().firstOrNull() { it.typeName == name })
        }
    }

}

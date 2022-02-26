package com.dingdo.core.dfa

import com.dingdo.RobotApplication
import com.dingdo.common.PackageScanner
import com.dingdo.enums.UserStageEnum
import net.mamoe.mirai.event.events.MessageEvent
import java.lang.reflect.ParameterizedType


class UserStage(private val stage: UserStageEnum) {

    companion object {
        private val converterMap: HashMap<UserStageEnum, List<StageConverter>> = HashMap()

        private fun getConverterMap(): HashMap<UserStageEnum, List<StageConverter>> {
            if (converterMap.isNotEmpty()) return converterMap

            val converterList = PackageScanner.classes
                .filter { StageConverter::class.java.isAssignableFrom(it) && !it.isInterface }
                .map { it.getConstructor().newInstance() as StageConverter }

            val reverse = converterList.associateBy({ it }, { it.node() })
                .reverse()

            converterMap.putAll(reverse)

            return converterMap
        }
    }

    fun nextStage(msg: MessageEvent): UserStage {
        val stageConverter = getConverterMap()[stage]!!
            .find { it.applyIf(msg) }
            ?: return this
        return stageConverter.convert(msg)
    }
}


fun <K, V> Map<K, Collection<V>>.reverse(): Map<V, List<K>> {
    val result = HashMap<V, MutableList<K>>()
    for (entry in this) {
        for (value in entry.value) {
            val list = result[value] ?: mutableListOf()
            list.add(entry.key)
            result[value] = list
        }
    }
    return result
}


fun main() {
    PackageScanner.doScan(RobotApplication::class.java)
}

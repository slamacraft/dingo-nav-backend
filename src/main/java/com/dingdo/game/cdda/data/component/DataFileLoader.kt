package com.dingdo.game.cdda.data.component

import com.dingdo.common.util.existOrCreate
import com.dingdo.game.cdda.data.emuns.Type
import com.dingdo.game.cdda.data.model.common.BaseData
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.io.File

object DataFileLoader {

    private val objectMapper: ObjectMapper = ObjectMapper()
    val dataMap = HashMap<String, DataTypeCollection>()

    init {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun loadData(src: String) {

        File(src).walk()
            .filter { it.isFile }
            .forEach { jsonFile ->
                val dataStr = jsonFile.existOrCreate().readText()
                val data = objectMapper.readTree(dataStr)

                data.forEach { rootNode ->
                    val typeStr = rootNode.path("type").asText()
                    Type.getEnum(typeStr).map {
                        val dataModel = it.instanceSupplier().parse(rootNode)
                        val dataTypeCollection = dataMap.getOrPut(typeStr) { DataTypeCollection(it) }
                        dataTypeCollection.dataIdMap[dataModel.id] = dataModel
                        dataTypeCollection.dataNameMap[dataModel.name] = dataModel
                    }
                }
            }
    }

    fun searchByName(name: String): BaseData? {
        for (dataCollection in dataMap) {
            val baseData = dataCollection.value.dataNameMap[name]
            if (baseData != null) {
                return baseData
            }
        }
        return null
    }
}

class DataTypeCollection constructor(val type: Type) {
    val dataIdMap = HashMap<String, BaseData>()
    val dataNameMap = HashMap<String, BaseData>()
}

fun main() {
    DataFileLoader.loadData("D:\\workspace\\springboot-webjar\\cddaData\\monsters")
    val dataMap = DataFileLoader.dataMap
    println("load OK")
}
